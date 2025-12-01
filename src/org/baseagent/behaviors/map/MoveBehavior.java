// filepath: p:/Projects/BaseAgent/baseagent_java/src/org/baseagent/behaviors/map/MoveBehavior.java
package org.baseagent.behaviors.map;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.Agent;
import org.baseagent.behaviors.LifecycleBehavior;
import org.baseagent.sim.MapAgent;

/**
 * MoveBehavior moves a MapAgent between one or more geographic waypoints (lat, lon).
 * It extends LifecycleBehavior so it can be started/paused by the simulation.
 * By default the behavior loops the waypoint list when it reaches the end.
 */
public class MoveBehavior extends LifecycleBehavior {
    private List<double[]> waypoints = new ArrayList<>();
    private int currentIndex = 0;
    private double speedDegreesPerStep = 0.2; // degrees of latitude/longitude per simulation step
    private boolean loop = true;

    public MoveBehavior() { }

    public MoveBehavior(double speedDegreesPerStep) {
        this.speedDegreesPerStep = speedDegreesPerStep;
    }

    public void addWaypoint(double lat, double lon) {
        waypoints.add(new double[] { lat, lon });
    }

    public void addWaypoint(MapAgent ma) {
        waypoints.add(new double[] { ma.getLatitude(), ma.getLongitude() });
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setSpeedDegreesPerStep(double s) {
        this.speedDegreesPerStep = s;
    }

    @Override
    public void startBehavior(Agent agent) {
        super.startBehavior(agent);
        if (waypoints.size() > 0) currentIndex = 0;
    }

    @Override
    public void executeBehavior(Agent agent) {
        if (isPaused()) return;
        if (!isStarted()) startBehavior(agent);

        if (!(agent instanceof MapAgent)) return;
        MapAgent ma = (MapAgent)agent;
        if (waypoints.isEmpty()) return;

        double[] target = waypoints.get(currentIndex);
        double lat = ma.getLatitude();
        double lon = ma.getLongitude();
        double dx = target[1] - lon; // lon difference
        double dy = target[0] - lat; // lat difference
        double dist = Math.hypot(dx, dy);
        if (dist < 1e-4) {
            // reached
            if (currentIndex < waypoints.size()-1) currentIndex++;
            else if (loop) currentIndex = 0;
            else pauseBehavior(agent);
            return;
        }

        double step = Math.min(speedDegreesPerStep, dist);
        double nx = lon + (dx / dist) * step;
        double ny = lat + (dy / dist) * step;
        ma.setLatLon(ny, nx);
    }
}
