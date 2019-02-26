package hlt;

/**
 * Class used to store when a ship finishes to dock.
 * 
 * @author Marius-Catalin Vlad
 *
 */
public class DockingTimeout {

    private int shipID;
    private int turnToDock;

    public DockingTimeout(int shipID, int turnToDock) {
        super();
        this.shipID = shipID;
        this.turnToDock = turnToDock;
    }

    public int getShipID() {
        return shipID;
    }

    public void setShipID(int shipID) {
        this.shipID = shipID;
    }

    public int getTurnToDock() {
        return turnToDock;
    }

    public void setTurnToDock(int turnToDock) {
        this.turnToDock = turnToDock;
    }

    @Override
    public String toString() {
        return "DockedTimeout: Ship: " + shipID + " Turn to dock: " + turnToDock;
    }

}
