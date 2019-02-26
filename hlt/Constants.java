package hlt;

public class Constants {

    ////////////////////////////////////////////////////////////////////////
    // Implementation-independent language-agnostic constants

    /** Games will not have more players than this */
    public static final int MAX_PLAYERS = 4;

    /** Max number of units of distance a ship can travel in a turn */
    public static final int MAX_SPEED = 7;

    /** Radius of a ship */
    public static final double SHIP_RADIUS = 0.5;

    /** Starting health of ship, also its max */
    public static final int MAX_SHIP_HEALTH = 255;

    /** Starting health of ship, also its max */
    public static final int BASE_SHIP_HEALTH = 255;

    /** Weapon cooldown period */
    public static final int WEAPON_COOLDOWN = 1;

    /** Weapon damage radius */
    public static final double WEAPON_RADIUS = 5.0;

    /** Weapon damage */
    public static final int WEAPON_DAMAGE = 64;

    /** Radius in which explosions affect other entities */
    public static final double EXPLOSION_RADIUS = 10.0;

    /** Distance from the edge of the planet at which ships can try to dock */
    public static final double DOCK_RADIUS = 4.0;

    /** Number of turns it takes to dock a ship */
    public static final int DOCK_TURNS = 5;

    /** Number of production units per turn contributed by each docked ship */
    public static final int BASE_PRODUCTIVITY = 6;

    /** Distance from the planets edge at which new ships are created */
    public static final double SPAWN_RADIUS = 2.0;

    ////////////////////////////////////////////////////////////////////////
    // Implementation-specific constants

    public static final double FORECAST_FUDGE_FACTOR = SHIP_RADIUS + 1.2;
    public static final int MAX_NAVIGATION_CORRECTIONS = 180;

    /**
     * Used in Position.getClosestPoint()
     * Minimum distance specified from the object's outer radius.
     */
    public static final int MIN_DISTANCE_FOR_CLOSEST_POINT = 3;

    /** Maximum distance between an allied and an enemy ship
     * in order to directly attack the enemy ship */
    public static final int MAX_AGGRESSIVE_DISTANCE = 400;

    /** Minimum distance between an allied and an enemy ship
     * in order to directly attack the enemy ship */
    public static final int MIN_AGGRESSIVE_DISTANCE = 100;

    public static final int SMALL_MAP_WIDTH = 300;

    /** Number of ships allowed to be docked on a planet */
    public static final int MAX_SHIPS_PER_PLANET = 3;

    /** Ideal distance between an allied ship and an enemy ship
     * in order to avoid collisions */
    public static final int ATTACKING_DISTANCE = 5;
	
	/** Angle of each step in ship navigation */
    public static final double ANGULAR_STEP_RAD = Math.PI / 180.0;
}
