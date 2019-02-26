import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import hlt.Constants;
import hlt.DockMove;
import hlt.GameMap;
import hlt.Log;
import hlt.Move;
import hlt.Navigation;
import hlt.Networking;
import hlt.Planet;
import hlt.Position;
import hlt.Ship;
import hlt.Ship.DockingStatus;
import hlt.ThrustMove;

public class Polaris_v8 {
    
    /**
     * Decrease the speed of the ship if there are enemy ships nearby in order to minimize the
     * chances of their collision.
     */
    public static int adjustShipSpeed(GameMap gameMap, Ship ship) {
        int speed = Constants.MAX_SPEED;
        int minDistance = Constants.ATTACKING_DISTANCE;

        for (Ship enemyShip : gameMap.getAllShips()) {
            if (enemyShip.getOwner() != gameMap.getMyPlayerId()) {
                if (enemyShip.getDistanceTo(ship) < minDistance) {
                    return 0;
                }

		if(gameMap.getWidth() > Constants.SMALL_MAP_WIDTH){
	       		speed = Math.min(speed, (int) (enemyShip.getDistanceTo(ship) - (Constants.MAX_SPEED + 1)));

			if(speed <= 0){
				speed = 1;
			}
		}

		
            }
        }

        return speed;
    }

    /**
     * Attack nearby enemy ships in order to counter-attack and defend the territory.
     */
    public static Ship getNearestEnemyFromShip(GameMap gameMap, Ship ship, int currentTurn) {
        Ship nearestEnemyShip = null;
        double minDistance = Double.MAX_VALUE;

	double radius = 0;

	if(gameMap.getWidth() < Constants.SMALL_MAP_WIDTH){
		radius = Math.max(Constants.MIN_AGGRESSIVE_DISTANCE, Constants.MAX_AGGRESSIVE_DISTANCE - 2 * currentTurn);
	}else{
       		radius = Math.max(0, 90 - currentTurn / 3);
	}

        for (Ship enemyShip : gameMap.getAllShips()) {
            if (enemyShip.getOwner() != gameMap.getMyPlayerId()) {
                double distToEnemy = enemyShip.getDistanceTo(ship);

                if (distToEnemy < Math.min(radius, minDistance)) {
                    minDistance = distToEnemy;
                    nearestEnemyShip = enemyShip;
                }
            }
        }

        return nearestEnemyShip;
    }

    /**
     * Get a random enemy ship that is docked on that planet.
     */
    public static Ship getDockedEnemyShip(GameMap gameMap, Planet planet, Ship myShip) {
        Ship enemyDockedShip = null;

        for (Ship ship : gameMap.getAllShips()) {
            if (ship.getId() == planet.getDockedShips().get(myShip.getId()
                    % planet.getDockedShips().size())) {
                enemyDockedShip = ship;
                break;
            }
        }

        return enemyDockedShip;
    }

    /**
     * Move a ship towards the targeted enemy ship
     */
    public static boolean moveShipTowardsTarget(GameMap gameMap, Ship ship, Position target,
            int shipSpeed, List<Move> moveList, List<Position> occupiedPositions,
            List<Ship> freeShips) {

        if (target != null) {
            int tempSpeed = shipSpeed;
            Position newPos = null;
            ThrustMove newThrustMove;

            newThrustMove = Navigation.navigateShipTowardsTarget(gameMap, ship, target, tempSpeed,
                    true, Constants.MAX_NAVIGATION_CORRECTIONS, Constants.ANGULAR_STEP_RAD);

            if (newThrustMove != null) {
                moveList.add(newThrustMove);

                double thrustAngle = Math.toRadians(newThrustMove.getAngle());
                double newXPos = ship.getXPos() + newThrustMove.getThrust() * Math.cos(thrustAngle);
                double newYPos = ship.getYPos() + newThrustMove.getThrust() * Math.sin(thrustAngle);

                newPos = new Position(newXPos, newYPos);
                occupiedPositions.add(newPos);
                gameMap.occupiedPositions.add(new Ship(1, 1, newXPos, newYPos, 1,
                        DockingStatus.Undocked, 1, 1, 1));
                freeShips.remove(freeShips.indexOf(ship));
                return true;
            }
        }

        return false;
    }

    /**
     * Main function.
     */
    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Polaris_v8");
        final ArrayList<Move> moveList = new ArrayList<>();

        final String initialMapIntelligence = "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        Log.log(initialMapIntelligence);

        int turns = 0;

        // List used to store ships positions in the next round
        LinkedList<Position> occupiedPositions = new LinkedList<Position>();
        // List used to store all undocked ships
        LinkedList<Ship> freeShips = new LinkedList<Ship>();
        // List used to store all unassigned planets
        LinkedList<Planet> freePlanets = new LinkedList<Planet>();

        // In case of exception write stack trace to file
        PrintStream errStream = null;
        try {
            errStream = new PrintStream(new File("../error.log"));
            for (;;) {
                // ================================================================================
                // --------------- UPDATING DATA ---------------
                // ================================================================================
                /*
                 *  Update turn dependent variables
                 */
                turns++;
                moveList.clear();
                networking.updateMap(gameMap);
                occupiedPositions.clear();
                freeShips.clear();

                for (Ship ship : gameMap.getMyPlayer().getShips().values()) {
                    if (ship.getDockingStatus() == Ship.DockingStatus.Undocked) {
                        freeShips.add(ship);
                    }
                }

                // ================================================================================
                // --------- DECIDING SHIPS BEHAVIOUR and SENDING MOVEMENT COMMANDS ---------
                // ================================================================================

                while (!freeShips.isEmpty()) {
                    freePlanets.clear();
                    freePlanets.addAll(gameMap.getAllPlanets().values());

                    /*
                     * Find the best {Undocked Allied Ship, Planet} pair on the map that satisfies
                     * the heuristic and store it in {minShip, minPlanet}.
                     */
                    int n = Math.min(freePlanets.size(), freeShips.size());
                    for (int i = 0; i < n; i++) {
                        double minScore = Double.MAX_VALUE;
                        Planet minPlanet = null;
                        Ship minShip = null;

                        for (Ship ship : freeShips) {
                            for (Planet planet : freePlanets) {

                                double planetPotential = (double) planet.getDockedShips().size()
                                        / (double) planet.getDockingSpots();

                                /*
                                 * Heuristic
                                 */
                                boolean isMyPlanet = (planet.getOwner() == gameMap.getMyPlayerId());

                                double score = planet.getDistanceTo(ship)
                                        + (isMyPlanet
                                                ? (30 * planetPotential)
                                                : 0)
                                        + ((planet.isFull() && isMyPlanet)
                                                ? 1000
                                                : 0);

                                if (score < minScore) {
                                    minScore = score;
                                    minPlanet = planet;
                                    minShip = ship;
                                }
                            }
                        }

                        /*
                         * Decrease the speed of the ship if there are enemy ships nearby in order
                         * to minimize the chances of their collision.
                         */
                        int speed = adjustShipSpeed(gameMap, minShip);

                        /*
                         * Attack nearby enemy ships in order to counter-attack and defend the
                         * territory
                         */
                        Ship nearestEnemyShip = getNearestEnemyFromShip(gameMap, minShip, turns);

                        boolean newMoveFound = moveShipTowardsTarget(gameMap, minShip,
                                nearestEnemyShip, speed, moveList, occupiedPositions, freeShips);

                        if (newMoveFound) {
                            continue;
                        }

                        /*
                         * If the minPlanet is owned by an enemy bot, then attack the docked enemy
                         * ship first.
                         */
                        if (minPlanet.isOwned()
                                && minPlanet.getOwner() != gameMap.getMyPlayerId()) {
                            /*
                             * Get a random enemy ship that is docked on that planet and store it in
                             * enemyDockedShip.
                             */
                            Ship enemyDockedShip = getDockedEnemyShip(gameMap, minPlanet, minShip);

                            /*
                             * Move towards that docked enemy ship.
                             */
                            newMoveFound = moveShipTowardsTarget(gameMap, minShip, enemyDockedShip,
                                    speed, moveList, occupiedPositions, freeShips);

                            if (newMoveFound) {
                                continue;
                            }
                        }

                        /*
                         * If the minShip reached minPlanet, then dock on it.
                         */
                        if (minShip.canDock(minPlanet)) {
                            moveList.add(new DockMove(minShip, minPlanet));
                            freePlanets.remove(freePlanets.indexOf(minPlanet));
                            freeShips.remove(freeShips.indexOf(minShip));
                            continue;
                        }

                        /*
                         * If the minShip didn't reach minPlanet yet, then move towards it.
                         */
                        newMoveFound = moveShipTowardsTarget(gameMap, minShip,
                                minShip.getClosestPoint(minPlanet), speed, moveList,
                                occupiedPositions, freeShips);

                        if (newMoveFound) {
                            freePlanets.remove(freePlanets.indexOf(minPlanet));
                        }
                    }
                }

                Networking.sendMoves(moveList);
            }
        } catch (Exception e) {
            e.printStackTrace(errStream);
        }
    }
}
