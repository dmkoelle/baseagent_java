package org.baseagent.worldmap;

import java.util.List;

import org.baseagent.behaviors.Behavior;
import org.baseagent.grid.ui.DrawableAgent;
import org.baseagent.grid.ui.GridCanvasContext;
import org.baseagent.util.GeoUtils;

import javafx.scene.paint.Color;

public class WorldMapAgent extends DrawableAgent {
    private int tileX;
    private int tileY;
    private double fineX;
    private double fineY;
    private double heading;
    private String mapLayerName;

    // Geographic position (WGS84)
    private double latitude = 0.0;
    private double longitude = 0.0;

    public void setLatLon(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public WorldMapAgent() {
        super();
        // default drawable does nothing; MapAgent overrides draw(GridCanvasContext)
        // below to use lat/lon
    }

    public WorldMapAgent(String mapLayerName) {
        this();
        this.mapLayerName = mapLayerName;
    }

    public WorldMapAgent(List<Behavior> behaviors) {
        this();
        for (Behavior b : behaviors)
            addBehavior(b);
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public double getFineX() {
        return fineX;
    }

    public double getFineY() {
        return fineY;
    }

    public double getHeading() {
        return heading;
    }

    public void setTileX(int x) {
        this.tileX = x;
        this.fineX = x;
        setDrawX(x);
    }

    public void setTileY(int y) {
        this.tileY = y;
        this.fineY = y;
        setDrawY(y);
    }

    public void setFineX(double x) {
        this.fineX = x;
        this.tileX = (int) Math.round(x);
        setDrawX(x);
    }

    public void setFineY(double y) {
        this.fineY = y;
        this.tileY = (int) Math.round(y);
        setDrawY(y);
    }

    public void setHeading(double h) {
        this.heading = h;
    }

    public WorldMap getMap() {
        return (WorldMap) getSimulation().getUniverse();
    }

    public WorldMapGridLayer getMapLayer() {
        return getMap().getMapLayer(this.mapLayerName);
    }

    @Override
    public void draw(GridCanvasContext gcc) {
        // Draw agent at lat/lon using slippy tile pixel coordinates for the current
        // slippy zoom stored in gcc.properties
        Object zoomProp = gcc.getProperties().get("slippyZoom");
        Object offXProp = gcc.getProperties().get("viewOffsetX");
        Object offYProp = gcc.getProperties().get("viewOffsetY");
        Object scaleProp = gcc.getProperties().get("zoomScale");
        int slippyZoom = (zoomProp instanceof Number) ? ((Number) zoomProp).intValue() : 2;
        double viewOffsetX = (offXProp instanceof Number) ? ((Number) offXProp).doubleValue() : 0.0;
        double viewOffsetY = (offYProp instanceof Number) ? ((Number) offYProp).doubleValue() : 0.0;
        double zoomScale = (scaleProp instanceof Number) ? ((Number) scaleProp).doubleValue() : 1.0;
        int tileSize = 256;
        double[] pxpy = GeoUtils.latLonToPixelXY(getLatitude(), getLongitude(), slippyZoom, tileSize);
        double px = pxpy[0] * zoomScale;
        double py = pxpy[1] * zoomScale;

        // convert to screen coordinates
        double screenX = px - viewOffsetX;
        double screenY = py - viewOffsetY;

        // draw as a circle centered at pixel coords
        double radius = 6.0;
        double drawX = screenX - radius;
        double drawY = screenY - radius;
        gcc.getGraphicsContext().setFill(getColorOrUse(Color.CADETBLUE));
        gcc.getGraphicsContext().fillOval(drawX, drawY, radius * 2, radius * 2);
    }

    // place agent at tile coordinate and keep drawX/Y in tile-space; MapCanvas will
    // convert
    public void placeAt(int tileX, int tileY) {
        setTileX(tileX);
        setTileY(tileY);
    }
}