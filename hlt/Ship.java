package hlt;

public class Ship extends Entity {

    public enum DockingStatus { Undocked, Docking, Docked, Undocking }
    
    // The ships class in the game
    /*
     * ATTACKER - attacks enemy ships
     * DEFENDER - defends own ships
     * MINER - goes to planets and mine resources
     */
    public enum Class {ATTACKER, DEFENDER, MINER};
    private Class sClass;

    private final DockingStatus dockingStatus;
    private final int dockedPlanet;
    private final int dockingProgress;
    private final int weaponCooldown;

    // Save the position of the ship in the next turn
    // null if the next position coincides with the current position
    private Position nextTurnPos;
    
    // Ship to attack or planet to dock
    private Entity target;

    public Ship(final int owner, final int id, final double xPos, final double yPos,
                final int health, final DockingStatus dockingStatus, final int dockedPlanet,
                final int dockingProgress, final int weaponCooldown) {

        super(owner, id, xPos, yPos, health, Constants.SHIP_RADIUS);

        this.dockingStatus = dockingStatus;
        this.dockedPlanet = dockedPlanet;
        this.dockingProgress = dockingProgress;
        this.weaponCooldown = weaponCooldown;
        
        nextTurnPos = null;
        sClass = null;
    }

    public int getWeaponCooldown() {
        return weaponCooldown;
    }

    public DockingStatus getDockingStatus() {
        return dockingStatus;
    }

    public int getDockingProgress() {
        return dockingProgress;
    }

    public int getDockedPlanet() {
        return dockedPlanet;
    }

    //-----------------------
    // If next position is null return ship's current position
    public Position getNextTurnPos() {
        return (nextTurnPos == null) ? this : nextTurnPos;
    }

    public void setNextTurnPos(Position nextTurn) {
        this.nextTurnPos = nextTurn;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public Class getsClass() {
        return sClass;
    }

    public void setsClass(Class sClass) {
        this.sClass = sClass;
    }

    //-----------------------
    
    public boolean canDock(final Planet planet) {
        return getDistanceTo(planet) <= Constants.SHIP_RADIUS + Constants.DOCK_RADIUS + planet.getRadius();
    }

    @Override
    public String toString() {
        return "Ship[" +
                super.toString() +
                ", dockingStatus=" + dockingStatus +
                ", dockedPlanet=" + dockedPlanet +
                ", dockingProgress=" + dockingProgress +
                ", weaponCooldown=" + weaponCooldown +
                ", nextTurnPos=" + nextTurnPos +
                ", shipClass=" + sClass + 
                "]";
    }
}
