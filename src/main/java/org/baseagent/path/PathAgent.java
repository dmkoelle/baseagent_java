package org.baseagent.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.baseagent.sim.Simulation;
import org.baseagent.worldmap.WorldMapAgent;

/**
 * Simple PathAgent that follows a list of geographic waypoints (MapAgent
 * instances). This implementation focuses on demonstrating movement between
 * network nodes on a MapCanvas.
 */
public class PathAgent extends WorldMapAgent implements HasPathPosition {

    private Segment currentSegment;
    private Segment nextSegment;
    private double distanceOnSegment;
    private List<WorldMapAgent> waypoints = new ArrayList<>();
    private int currentWaypointIndex = 0;
    // degrees per simulation step (tune for sensible animation speed)
    private double speedDegreesPerStep = 0.06;

    public PathAgent() {
        super();
    }

    public void setSpeedDegreesPerStep(double speed) {
        this.speedDegreesPerStep = speed;
    }

    public void setWaypoints(List<WorldMapAgent> wps) {
        this.waypoints.clear();
        if (wps != null)
            this.waypoints.addAll(wps);
        this.currentWaypointIndex = 0;
        if (!this.waypoints.isEmpty()) {
            WorldMapAgent first = this.waypoints.get(0);
            this.setLatLon(first.getLatitude(), first.getLongitude());
        }
    }

    public void setWaypoints(WorldMapAgent... wps) {
        setWaypoints(Arrays.asList(wps));
    }

    @Override
    public void step(Simulation simulation) {
        // Move toward current waypoint
        if (waypoints.isEmpty())
            return;
        WorldMapAgent target = waypoints.get(currentWaypointIndex);
        double targetLat = target.getLatitude();
        double targetLon = target.getLongitude();
        double lat = this.getLatitude();
        double lon = this.getLongitude();

        double dx = targetLon - lon;
        double dy = targetLat - lat;
        double dist = Math.hypot(dx, dy);
        if (dist < 1e-4) {
            // reached waypoint -> advance
            currentWaypointIndex = (currentWaypointIndex + 1) % waypoints.size();
            return;
        }

        double step = Math.min(speedDegreesPerStep, dist);
        double nx = lon + (dx / dist) * step;
        double ny = lat + (dy / dist) * step;
        this.setLatLon(ny, nx);
    }

    // --- HasPathPosition methods (minimal stubs for compatibility) ---

    @Override
    public Segment getCurrentSegment() {
        return this.currentSegment;
    }

    @Override
    public double getHeading() {
        return 0;
    }

    @Override
    public Segment deterimeNextSegment(Intersection intersection) {
        return null;
    }

    @Override
    public void setNextSegment(Segment segment) {
        this.nextSegment = segment;
    }

    @Override
    public Segment getNextSegment() {
        return this.nextSegment;
    }

}