package org.baseagent.particles;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.HasStep;

public class ParticleSpace implements HasStep {
	private int width;
	private int height;
	private List<Particle> particles;
	private List<ParticleRule> rules;
	
	public ParticleSpace(int width, int height) {
		this.width = width;
		this.height = height;
		this.particles = new ArrayList<>();
		this.rules = new ArrayList<>();
	}
	
	public List<Particle> getParticles() {
		return this.particles;
	}

	public List<Particle> createParticles() {
		return this.particles;
	}

}
