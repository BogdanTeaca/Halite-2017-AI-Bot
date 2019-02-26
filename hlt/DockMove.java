package hlt;

public class DockMove extends Move {

    private final long destinationId;

    public DockMove(final Ship ship, final Planet planet) {
        super(MoveType.Dock, ship);
        destinationId = planet.getId();
        //-------------------
        ship.setNextTurnPos(null);
        //-------------------
    }

    public long getDestinationId() {
        return destinationId;
    }
}
