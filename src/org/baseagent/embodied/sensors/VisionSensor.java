package org.baseagent.embodied.sensors;

/**
 * @author David Koelle
 */
public class VisionSensor {
	public VisionSensor(double range, double leftRadians, double rightRadians, VisionSensor.Strategy strategy) {
		
	}
	
	/**
	 * If you create a special sensor that doesn't just work
	 * on signals, like a vision sensor, you'll want to 
	 * override how getValue works.
	 */
//	public Container getValue() {
//		
//	}
//	
	public enum Strategy { STACK }
}
