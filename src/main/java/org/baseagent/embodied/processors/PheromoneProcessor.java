package org.baseagent.embodied.processors;

import java.util.Random;

import org.baseagent.embodied.ConnectedComponent;
import org.baseagent.embodied.EmbodiedAgent;
import org.baseagent.embodied.Processor;

/**
 * Processor that takes left/right pheromone intensity inputs and produces a
 * direction (radians) and intensity output for a ForceEffector.
 */
public class PheromoneProcessor extends Processor<Double, Double> {
    private ConnectedComponent<Double> leftInput;
    private ConnectedComponent<Double> rightInput;
    private ConnectedComponent<Double> directionOutput;
    private ConnectedComponent<Double> intensityOutput;
    private static final Random RAND = new Random();

    // tuning
    private double steerGain = 0.6; // how strongly difference turns
    private double intensityScale = 0.12; // scale down produced force magnitude (slower)

    public PheromoneProcessor() {
        super();
        this.leftInput = new ConnectedComponent<>();
        this.rightInput = new ConnectedComponent<>();
        this.directionOutput = new ConnectedComponent<>();
        this.intensityOutput = new ConnectedComponent<>();
    }

    @Override
    public void process(EmbodiedAgent agent) {
        Double l = this.leftInput.getInputValue();
        Double r = this.rightInput.getInputValue();
        if (l == null)
            l = 0.0;
        if (r == null)
            r = 0.0;

        double avg = (l + r) / 2.0;
        double diff = (r - l);

        double steer = 0.0;
        if (Math.abs(diff) > 0.01) {
            steer = Math.signum(diff) * Math.min(0.8, Math.abs(diff) * steerGain);
        }

        double desiredDirection;
        double intensity;

        if (avg <= 0.001) {
            // no pheromone signal: random exploratory behavior
            desiredDirection = agent.getHeading() + (RAND.nextDouble() - 0.5) * 0.6;
            intensity = 0.02 + RAND.nextDouble() * 0.03; // very small baseline movement (slower)
        } else {
            // follow pheromone with some jitter
            desiredDirection = agent.getHeading() + steer + (RAND.nextDouble() - 0.5) * 0.12;
            intensity = Math.min(1.0, avg / 6.0) * intensityScale + 0.01; // scale down
        }

        // normalize direction to -PI..PI
        while (desiredDirection > Math.PI)
            desiredDirection -= 2 * Math.PI;
        while (desiredDirection < -Math.PI)
            desiredDirection += 2 * Math.PI;

        this.directionOutput.setOutputValue(desiredDirection);
        this.intensityOutput.setOutputValue(intensity);
    }

    public ConnectedComponent<Double> getLeftInput() {
        return leftInput;
    }

    public ConnectedComponent<Double> getRightInput() {
        return rightInput;
    }

    public ConnectedComponent<Double> getDirectionOutput() {
        return directionOutput;
    }

    public ConnectedComponent<Double> getIntensityOutput() {
        return intensityOutput;
    }
}