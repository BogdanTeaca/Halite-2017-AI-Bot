package hlt;

public class ThrustMove extends Move {

    private final int angleDeg;
    private final int thrust;

    public ThrustMove(final Ship ship, final int angleDeg, final int thrust) {
        super(MoveType.Thrust, ship);
        this.thrust = thrust;
        this.angleDeg = angleDeg;
        //-------------------
        double angleRad = Math.toRadians(angleDeg); 
        double xNew = ship.getXPos() + thrust * Math.cos(angleRad);
        double yNew = ship.getYPos() + thrust * Math.sin(angleRad);
        ship.setNextTurnPos(new Position(xNew, yNew));
        //Log.log("Ship: " + ship.toString());
        //Log.log("Ship: " + ship.getId() + " angle: " + angleDeg + " thrust: " + thrust + " xPos: " + ship.getXPos());
        //-------------------
    }

    public int getAngle() {
        return angleDeg;
    }

    public int getThrust() {
        return thrust;
    }
}
