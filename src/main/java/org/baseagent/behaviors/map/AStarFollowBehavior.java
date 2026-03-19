package org.baseagent.behaviors.map;

import java.util.List;

import org.baseagent.Agent;
import org.baseagent.behaviors.LifecycleBehavior;
import org.baseagent.util.AStar;
import org.baseagent.worldmap.WorldMapAgent;
import org.baseagent.worldmap.WorldMapGridLayer;

/**
 * Behavior that computes an A* path on a MapLayer and instantiates a
 * MoveBehavior to follow the resulting cell centers. The behavior attaches the
 * MoveBehavior to the agent and then ends itself.
 */
public class AStarFollowBehavior extends LifecycleBehavior {
    private WorldMapGridLayer layer;
    private double endLat;
    private double endLon;
    private double speedDegreesPerStep = 0.2;
    private boolean loop = false;

    public AStarFollowBehavior(WorldMapGridLayer layer, double endLat, double endLon) {
        this.layer = layer;
        this.endLat = endLat;
        this.endLon = endLon;
    }

    public void setSpeedDegreesPerStep(double s) {
        this.speedDegreesPerStep = s;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    @Override
    public void executeBehavior(Agent agent) {
        if (isPaused())
            return;
        if (!(agent instanceof WorldMapAgent))
            return;
        if (!isStarted())
            startBehavior(agent);

        WorldMapAgent ma = (WorldMapAgent) agent;
        double startLat = ma.getLatitude();
        double startLon = ma.getLongitude();

        // compute A* path in cell coordinates
        List<int[]> path = AStar.findPath(layer, startLat, startLon, endLat, endLon);
        if (path == null || path.isEmpty()) {
            // no path; end behavior
            endBehavior(agent);
            return;
        }

        // build a MoveBehavior and add waypoints as cell centers
        MoveBehavior mb = new MoveBehavior(this.speedDegreesPerStep);
        for (int[] cell : path) {
            double[] latlon = layer.cellToLatLon(cell[0], cell[1]);
            mb.addWaypoint(latlon[0], latlon[1]);
        }
        mb.setLoop(this.loop);

        // Attach the move behavior to the agent. The simulation will drive it.
        ma.addBehavior(mb);

        // This behavior's job is done
        endBehavior(agent);
    }
}
