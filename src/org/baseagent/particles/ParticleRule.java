package org.baseagent.particles;

import java.util.List;

import org.baseagent.util.BaseAgentMath;

public class ParticleRule {
	private List<Particle> particles;
	
	public ParticleRule(List<Particle> typeA, List<Particle> typeB, float g) {
		
	}
	
	public void applyRule() {
		for (Particle p1 : typeA) {
			double fx = 0.0;
			double fy = 0.0;
			for (Particle p2 : typeB) {
				double distance = BaseAgentMath.distance(p1.getFineX(), p1.getFineY(), p2.getFineX(), p2.getFineY());
				if (distance > 0.0D) {
					double F = g * 1/d;
					fx += (F * dx);
					fy += (F * dy);
				}
			}
			p1.deltaFinePosition(fx, fy);
		}
	}
}
