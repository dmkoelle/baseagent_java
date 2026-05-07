package org.baseagent;

import org.baseagent.sim.Simulation;

/**
 * A pheromone is a beacon that dissipates over time and eventually goes away
 */
public class Pheromone extends Beacon {
    private double intensity = 1.0;
    private double dissipationRate = 0.05; // fraction lost per step

    public Pheromone() {
        super();
    }

    public Pheromone(double initialIntensity, double dissipationRate) {
        super();
        this.intensity = initialIntensity;
        this.dissipationRate = dissipationRate;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getDissipationRate() {
        return dissipationRate;
    }

    public void setDissipationRate(double dissipationRate) {
        this.dissipationRate = dissipationRate;
    }

    @Override
    public void step(Simulation s) {
        // dissipate
        intensity -= dissipationRate;
        if (intensity <= 0.0) {
            // remove from simulation when gone
            try {
                s.remove(this);
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    @Override
    public String toString() {
        return "Pheromone(" + intensity + ")";
    }
}