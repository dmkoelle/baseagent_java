package org.baseagent.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
		return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}

	public static double distance(HasGridPosition p1, HasGridPosition p2) {
		return BaseAgentMath.distance(p1.getCellX(), p1.getCellY(), p2.getCellX(), p2.getCellY());
	}
	
	public static double distance(HasGridPosition p1, double x2, double y2) {
		return BaseAgentMath.distance(p1.getCellX(), p1.getCellY(), x2, y2);
	}

	public static double direction(int x1, int y1, int x2, int y2) {
		return Math.atan2(y2-y1, x2-x1);
	}

	public static double direction(HasGridPosition p1, HasGridPosition p2) {
		return BaseAgentMath.direction(p1.getCellX(), p1.getCellY(), p2.getCellX(), p2.getCellY());
	}

	public static double direction(HasGridPosition p1, int x2, int y2) {
		return BaseAgentMath.direction(p1.getCellX(), p1.getCellY(), x2, y2);
	}

	public static CellPoint2D getPointAt(HasGridPosition originalPoint, Vector2D vector) {
		return getPointAt(originalPoint.getCellX(), originalPoint.getCellY(), vector.getMagnitude(), vector.getDirection());
	}

	public static CellPoint2D getPointAt(int x, int y, double magnitude, double direction) {
		int newX = (int)(x + magnitude * Math.cos(direction));
		int newY = (int)(y + magnitude * Math.sin(direction));
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
		list.sort((Pair<Object, Double> p1, Pair<Object, Double> p2) -> p1.getSecond() > p2.getSecond() ? 1 : p1.getSecond() < p2.getSecond() ? -1 : 0);

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
		for (int i=1; i < inputs.size(); i++) {
			dd[i] = dd[i-1] + inputs.get(i);
		}
		int index = Arrays.binarySearch(dd, new Random().nextDouble());
		return (index > 0) ? index : (-index - 1);
	}
	
	// Returns TRUE if the agent at x,y can see the location at i,u - if there are no
	// obstructions between the two positions. Specifically, we're looking for a barrier.
	// The first time we see the barrier, that's okay, because the barrier is detectable.
	// But if there is anything beyond the barrier, the agent cannot see it.
	//
	// Based on Bresenham's line algorithm
	public static boolean canSeeIt(Grid grid, int x, int y, int i, int u, Predicate<GridPosition> barrierCondition)
	{
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
	
	public static boolean canSeeIt(Grid grid, HasGridPosition a, HasGridPosition b, Predicate<GridPosition> barrierCondition) {
		return canSeeIt(grid, a.getCellX(), a.getCellY(), b.getCellX(), b.getCellY(), barrierCondition);
	}
}


	
