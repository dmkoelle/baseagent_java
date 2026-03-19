package org.baseagent.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

import org.baseagent.grid.Grid;
import org.baseagent.grid.GridPosition;
import org.baseagent.grid.HasGridPosition;

public class BaseAgentMath {
    public static double PI = Math.PI;
    public static double HALF_PI = Math.PI / 2.0;
    public static double THREE_HALF_PI = 3.0 * Math.PI / 2.0;

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public static double distance(HasGridPosition p1, HasGridPosition p2) {
        return BaseAgentMath.distance(p1.getCellX(), p1.getCellY(), p2.getCellX(), p2.getCellY());
    }

    public static double distance(HasGridPosition p1, double x2, double y2) {
        return BaseAgentMath.distance(p1.getCellX(), p1.getCellY(), x2, y2);
    }

    public static double direction(int x1, int y1, int x2, int y2) {
        return Math.atan2(y2 - y1, x2 - x1);
    }

    public static double direction(HasGridPosition p1, HasGridPosition p2) {
        return BaseAgentMath.direction(p1.getCellX(), p1.getCellY(), p2.getCellX(), p2.getCellY());
    }

    public static double direction(HasGridPosition p1, int x2, int y2) {
        return BaseAgentMath.direction(p1.getCellX(), p1.getCellY(), x2, y2);
    }

    public static CellPoint2D getPointAt(HasGridPosition originalPoint, Vector2D vector) {
        return getPointAt(originalPoint.getCellX(), originalPoint.getCellY(), vector.getMagnitude(),
                vector.getDirection());
    }

    public static CellPoint2D getPointAt(int x, int y, double magnitude, double direction) {
        int newX = (int) (x + magnitude * Math.cos(direction));
        int newY = (int) (y + magnitude * Math.sin(direction));
        return new CellPoint2D(newX, newY);
    }

    public static List<Pair<Object, Double>> sortByDistance(Map<?, HasGridPosition> points, HasGridPosition origin) {
        List<Pair<Object, Double>> list = new ArrayList<>();

        // Step 1. Calculate the distances
        for (Map.Entry<?, HasGridPosition> entry : points.entrySet()) {
            double distance = BaseAgentMath.distance(entry.getValue(), origin);
            list.add(new Pair<Object, Double>(entry.getKey(), distance));
        }

        // Step 2. Sort the list based on distance
        list.sort((Pair<Object, Double> p1, Pair<Object, Double> p2) -> p1.getSecond() > p2.getSecond() ? 1
                : p1.getSecond() < p2.getSecond() ? -1 : 0);

        return list;
    }

    public static List<Double> normalize(List<Double> inputs) {
        double total = 0.0d;
        for (double d : inputs) {
            total += d;
        }

        List<Double> outputs = new ArrayList<>();
        for (double d : inputs) {
            outputs.add(d / total);
        }
        return outputs;
    }

    public static int sample(List<Double> inputs) {
        double[] dd = new double[inputs.size()];
        dd[0] = inputs.get(0);
        for (int i = 1; i < inputs.size(); i++) {
            dd[i] = dd[i - 1] + inputs.get(i);
        }
        int index = Arrays.binarySearch(dd, new Random().nextDouble());
        return (index > 0) ? index : (-index - 1);
    }

    // Returns TRUE if the agent at x,y can see the location at i,u - if there are
    // no
    // obstructions between the two positions. Specifically, we're looking for a
    // barrier.
    // The first time we see the barrier, that's okay, because the barrier is
    // detectable.
    // But if there is anything beyond the barrier, the agent cannot see it.
    //
    // Based on Bresenham's line algorithm
    public static boolean canSeeIt(Grid grid, int x, int y, int i, int u, Predicate<GridPosition> barrierCondition) {
        int dx = Math.abs(i - x);
        int dy = Math.abs(u - y);

        int sx = x < i ? 1 : -1;
        int sy = y < u ? 1 : -1;

        int err = dx - dy;
        int e2;

        boolean foundBarrierYet = false;
        while (true) {
            if (foundBarrierYet) {
                return false;
            }

            if (barrierCondition.test(new GridPosition(x, y))) {
                if (!foundBarrierYet) {
                    foundBarrierYet = true;
                }
            }
            if (x == i && y == u) {
                break;
            }

            e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x = x + sx;
            }

            if (e2 < dx) {
                err = err + dx;
                y = y + sy;
            }
        }
        return true;
    }

    public static boolean canSeeIt(Grid grid, HasGridPosition a, HasGridPosition b,
            Predicate<GridPosition> barrierCondition) {
        return canSeeIt(grid, a.getCellX(), a.getCellY(), b.getCellX(), b.getCellY(), barrierCondition);
    }

    /**
     * Calculates the interception point where Agent 1 should move to meet Agent 2
     * 
     * @param agent1Pos      Current position of Agent 1
     * @param agent1Speed    Speed of Agent 1
     * @param agent2Pos      Current position of Agent 2
     * @param agent2Velocity Velocity vector of Agent 2 (direction and speed
     *                       combined)
     * @return Optional containing the interception point, or empty if interception
     *         is impossible
     */
    public static Optional<GridPosition> calculateInterceptionPoint(GridPosition agent1Pos, double agent1Speed,
            GridPosition agent2Pos, GridPosition agent2Dest, double agent2Speed) {
        // Calculate Agent 2's velocity vector
        GridPosition agent2Direction = agent2Dest.subtract(agent2Pos);
        double distanceToDestination = agent2Direction.magnitude();

        // Handle case where Agent 2 is already at destination
        if (distanceToDestination < 1e-10) {
            return Optional.of(agent2Pos);
        }

        // Normalize direction and multiply by speed to get velocity
        GridPosition agent2Velocity = agent2Direction.normalize().multiply(agent2Speed);

        // Calculate time for Agent 2 to reach its destination
        double timeToDestination = distanceToDestination / agent2Speed;

        // Vector from agent2 to agent1
        GridPosition relativePos = agent1Pos.subtract(agent2Pos);

        // Coefficients for quadratic equation: at² + bt + c = 0
        double a = agent2Velocity.magnitudeSquared() - agent1Speed * agent1Speed;
        double b = 2 * agent2Velocity.dot(relativePos);
        double c = relativePos.magnitudeSquared();

        // Handle special case where speeds are equal (linear equation)
        if (Math.abs(a) < 1e-10) {
            if (Math.abs(b) < 1e-10) {
                // If both a and b are zero, either always intercept or never intercept
                return Math.abs(c) < 1e-10 ? Optional.of(agent2Pos) : Optional.empty();
            }
            double t = -c / b;
            if (t >= 0 && t <= timeToDestination) {
                return Optional.of(agent2Pos.add(agent2Velocity.multiply(t)));
            }
            return Optional.empty();
        }

        // Solve quadratic equation
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return Optional.empty(); // No real solution - interception impossible
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDiscriminant) / (2 * a);
        double t2 = (-b + sqrtDiscriminant) / (2 * a);

        // Choose the smallest positive time that's within Agent 2's travel time
        double t = -1;
        if (t1 >= 0 && t1 <= timeToDestination && t2 >= 0 && t2 <= timeToDestination) {
            t = Math.min(t1, t2);
        } else if (t1 >= 0 && t1 <= timeToDestination) {
            t = t1;
        } else if (t2 >= 0 && t2 <= timeToDestination) {
            t = t2;
        }

        if (t < 0) {
            return Optional.empty(); // No future interception possible
        }

        // Calculate interception point
        GridPosition interceptionPoint = agent2Pos.add(agent2Velocity.multiply(t));
        return Optional.of(interceptionPoint);
    }
}
