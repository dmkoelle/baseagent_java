package org.baseagent.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.baseagent.grid.HasGridPosition;

public class BaseAgentMath {
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
//		return Math.atan2(y2-y1, x2-x1);
		return Math.atan2(y2, x2) - Math.atan2(y1, x1);  // https://stackoverflow.com/questions/21483999/using-atan2-to-find-angle-between-two-vectors
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
}
