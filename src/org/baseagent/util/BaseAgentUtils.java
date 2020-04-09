package org.baseagent.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.baseagent.Agent;

public class BaseAgentUtils {
	public static int updateMapValue(Map<String, Object> map, String key, int delta) {
		if (map.containsKey(key)) {
			if (map.get(key) instanceof Integer) {
				int currentValue = (Integer)map.get(key);
				map.put(key, currentValue + delta);
				return currentValue + delta;
			} else {
				throw new IllegalArgumentException("Value at key "+key+" is not an integer.");
			}
		} else {
			map.put(key, delta);
			return delta;
		}
	}
	
	public static double updateMapValue(Map<String, Object> map, String key, double delta) {
		if (map.containsKey(key)) {
			if (map.get(key) instanceof Double) {
				double currentValue = (Double)map.get(key);
				map.put(key, currentValue + delta);
				return currentValue + delta;
			} else {
				throw new IllegalArgumentException("Value at key "+key+" is not a double.");
			}
		} else {
			map.put(key, delta);
			return delta;
		}
	}
	
	public static Map<String, Object> copyMap(Map<String, Object> originalMap) {
		Map<String, Object> newMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
			newMap.put(entry.getKey(), entry.getValue());
		}
		return newMap;
	}
	
	public static Boolean flipBooleanMapValue(Map<String, Object> map, String key) {
		Boolean retVal = Boolean.FALSE;
		if (!map.containsKey(key)) {
			retVal = Boolean.TRUE;
			map.put(key, retVal);
		} else {
			retVal = !(Boolean)map.get(key);
			map.put(key, retVal);
		}
		return retVal;
	}
	
	public static Object getValueOrDefault(Map<String, Object> map, String key, Object defaultValue) {
		if (!map.containsKey(key)) {
			map.put(key, defaultValue);
		} 
		return map.get(key);
	}
	
	public static Object getKeyForHighestDoubleValue(Map<Object, Double> map) {
		Double highestValue = 0.0D;
		Object keyForHighestValue = null;
		
		for (Map.Entry<Object, Double> entry : map.entrySet()) {
			if (entry.getValue() > highestValue) {
				highestValue = entry.getValue();
				keyForHighestValue = entry.getKey();
			}
		}

		return keyForHighestValue;
	}
	
	public static boolean chance(double c) {
		return (Math.random() < c);
	}
	
}
