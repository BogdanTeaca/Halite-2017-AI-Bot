package hlt;

public class Collision {
    /**
     * Test whether a given line segment intersects a circular area.
     *
     * @param start  The start of the segment.
     * @param end    The end of the segment.
     * @param circle The circle to test against.
     * @param fudge  An additional safety zone to leave when looking for collisions. Probably set it to ship radius.
     * @return true if the segment intersects, false otherwise
     */
    public static boolean segmentCircleIntersect(final Position start, final Position end, final Entity circle, final double fudge) {
        // Parameterize the segment as start + t * (end - start),
        // and substitute into the equation of a circle
        // Solve for t
        /*
        double alreadyX = 0;
        double alreadyY = 0;
        
        if (circle instanceof Ship && ((Ship)circle).getNextTurnPos() != null) {
            alreadyX = ((Ship) circle).getNextTurnPos().getXPos() - circle.getXPos();
            alreadyY = ((Ship) circle).getNextTurnPos().getYPos() - circle.getYPos();
        }
        */
        final double circleRadius = circle.getRadius();
        final double startX = start.getXPos();
        final double startY = start.getYPos();
        final double endX = end.getXPos();// - alreadyX;
        final double endY = end.getYPos();// - alreadyY;
		
		Position newCircle = (circle instanceof Ship) ? ((Ship) circle).getNextTurnPos() : circle;
        //-------------------
        // If entity is ship take in consideration the position from the next turn rather than its actual position
        //final double centerX = circle.getXPos();
        final double centerX = newCircle.getXPos();
        //final double centerY = circle.getYPos();
        final double centerY = newCircle.getYPos();
        //-------------------
        final double dx = endX - startX;
        final double dy = endY - startY;
        final double a = square(dx) + square(dy);

        final double b = -2 * (square(startX) - (startX * endX)
                            - (startX * centerX) + (endX * centerX)
                            + square(startY) - (startY * endY)
                            - (startY * centerY) + (endY * centerY));

        if (a == 0.0) {
            // Start and end are the same point
            return start.getDistanceTo(newCircle) <= circleRadius + fudge;
        }
        // Time along segment when closest to the circle (vertex of the quadratic)
        final double t = Math.min(-b / (2 * a), 1.0);
        if (t < 0) {
            return false;
        }

        final double closestX = startX + dx * t;
        final double closestY = startY + dy * t;
        final double closestDistance = new Position(closestX, closestY).getDistanceTo(newCircle);
		
        return closestDistance <= circleRadius + fudge;
    }

    public static double square(final double num) {
        return num * num;
    }
}
