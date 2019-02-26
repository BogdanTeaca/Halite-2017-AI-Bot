package hlt;

public class Navigation {

    public static ThrustMove navigateShipToDock(
            final GameMap gameMap,
            final Ship ship,
            final Planet dockTarget,
            final int maxThrust) {
        final int maxCorrections = Constants.MAX_NAVIGATION_CORRECTIONS;
        final boolean avoidObstacles = true;
        final double angularStepRad = Math.PI / 180.0;
        final Position targetPos = ship.getClosestPoint(dockTarget);

        return navigateShipTowardsTarget(gameMap, ship, targetPos, maxThrust, avoidObstacles,
                maxCorrections, angularStepRad);
    }

    /**
     * Backup of original function
     */
    public static ThrustMove navigateShipTowardsTarget2(
            final GameMap gameMap,
            final Ship ship,
            final Position targetPos,
            final int maxThrust,
            final boolean avoidObstacles,
            final int maxCorrections,
            final double angularStepRad) {
        if (maxCorrections <= 0) {
            return null;
        }
        final double distance = ship.getDistanceTo(targetPos);
        final double angleRad = ship.orientTowardsInRad(targetPos);
        if (avoidObstacles && !gameMap.objectsBetween(ship, targetPos).isEmpty()) {
            final double newTargetDx = Math.cos(angleRad + angularStepRad) * distance;
            final double newTargetDy = Math.sin(angleRad + angularStepRad) * distance;
            final Position newTarget = new Position(ship.getXPos() + newTargetDx,
                    ship.getYPos() + newTargetDy);

            return navigateShipTowardsTarget2(gameMap, ship, newTarget, maxThrust, true,
                    (maxCorrections - 1), angularStepRad);
        }

        final int thrust;
        if (distance < maxThrust) {
            // Do not round up, since overshooting might cause collision.
            thrust = (int) distance;
        } else {
            thrust = maxThrust;
        }

        final int angleDeg = Util.angleRadToDegClipped(angleRad);

        return new ThrustMove(ship, angleDeg, thrust);
    }

    /**
     * Mein function of navigating :)
     * Not recursive and checks for both +angularStepRad and -angularStepRad at the same time
     */
    public static ThrustMove navigateShipTowardsTarget(
            final GameMap gameMap,
            final Ship ship,
            final Position targetPos,
            final int maxThrust,
            final boolean avoidObstacles,
            final int maxCorrections,
            final double angularStepRad) {

        double distance = 0;
        double angleRad = 0;
        double distancePlus = 0;
        double angleRadPlus = 0;
        double distanceMinus = 0;
        double angleRadMinus = 0;

        Position targetPosPlus = targetPos;
        Position targetPosMinus = targetPos;
        int idx;
        for (idx = 0; idx < maxCorrections; ++idx) {
            // angleRadPlus + angularStepRad
            distancePlus = ship.getDistanceTo(targetPosPlus);
            angleRadPlus = ship.orientTowardsInRad(targetPosPlus);

            if (avoidObstacles && !gameMap.objectsBetween(ship, targetPosPlus).isEmpty()) {
                final double newTargetDx = Math.cos(angleRadPlus + angularStepRad) * distancePlus;
                final double newTargetDy = Math.sin(angleRadPlus + angularStepRad) * distancePlus;
                targetPosPlus = new Position(ship.getXPos() + newTargetDx,
                        ship.getYPos() + newTargetDy);
            } else {
                // Found best route and exit loop
                distance = distancePlus;
                angleRad = angleRadPlus;
                break;
            }

            // angleRadPlus - angularStepRad
            distanceMinus = ship.getDistanceTo(targetPosMinus);
            angleRadMinus = ship.orientTowardsInRad(targetPosMinus);

            if (avoidObstacles && !gameMap.objectsBetween(ship, targetPosMinus).isEmpty()) {
                final double newTargetDx = Math.cos(angleRadMinus - angularStepRad) * distanceMinus;
                final double newTargetDy = Math.sin(angleRadMinus - angularStepRad) * distanceMinus;
                targetPosMinus = new Position(ship.getXPos() + newTargetDx,
                        ship.getYPos() + newTargetDy);
            } else {
                // Found best route and exit loop
                distance = distanceMinus;
                angleRad = angleRadMinus;
                break;
            }
        }

        if (idx == maxCorrections) {
            return null;
        } else {
            final int thrust;
            if (distance < maxThrust) {
                // Do not round up, since overshooting might cause collision.
                thrust = (int) distance;
            } else {
                thrust = maxThrust;
            }

            final int angleDeg = Util.angleRadToDegClipped(angleRad);

            return new ThrustMove(ship, angleDeg, thrust);
        }
    }
}
