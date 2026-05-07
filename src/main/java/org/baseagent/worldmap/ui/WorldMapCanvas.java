package org.baseagent.worldmap.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.baseagent.ui.Toast;
import org.baseagent.worldmap.WorldMap;
import org.baseagent.worldmap.WorldMapAgent;
import org.baseagent.worldmap.WorldMapGridLayer;
import org.baseagent.grid.Grid;
import org.baseagent.grid.ui.GridCanvasContext;
import org.baseagent.grid.ui.GridDrawable;
import org.baseagent.grid.ui.GridOverlayRenderer;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

public class WorldMapCanvas extends Canvas {
    private WorldMap map;
    private String id = "DEFAULT_MAP_CANVAS_ID";
    private Grid dummyGrid;
    private GridCanvasContext gcc;

    private List<String> orderedListOfLayerNames;
    private java.util.Map<String, WorldMapLayerRenderer> renderersByName;
    private List<GridDrawable> customDrawables;
    private List<Toast> toasts;

    // View state in global slippy pixel coordinates at current slippyZoom
    private double viewOffsetX = 0.0;
    private double viewOffsetY = 0.0;
    private int slippyZoom = 2;
    // fractional zoom relative to slippyZoom (1.0 = no fractional zoom). Range
    // typically [0.5,2)
    private double zoomScale = 1.0;
    private boolean panning = false;
    private double lastMouseX, lastMouseY;

    private Object selectedAgent = null;

    // Tile system & renderers
    private SlippyTileFetcher tileFetcher;
    private WorldMapTileLayerRenderer tileRenderer;
    private GridOverlayRenderer gridOverlayRenderer;

    public WorldMapCanvas(WorldMap map, int tileWidth, int tileHeight, int tileXSpacing, int tileYSpacing) {
        super(map.getWidthInTiles() * tileWidth + (map.getWidthInTiles() - 1) * tileXSpacing,
                map.getHeightInTiles() * tileHeight + (map.getHeightInTiles() - 1) * tileYSpacing);
        this.map = map;
        this.dummyGrid = new Grid(map.getWidthInTiles(), map.getHeightInTiles());
        this.gcc = new GridCanvasContext(null, dummyGrid, null, tileWidth, tileHeight, tileXSpacing, tileYSpacing);
        this.orderedListOfLayerNames = new ArrayList<>();
        this.renderersByName = new HashMap<>();
        this.customDrawables = new ArrayList<>();
        this.toasts = new ArrayList<>();

        // default tile fetcher (Esri World Imagery) with disk cache under ./tilecache
        String esriTemplate = "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}";
        this.tileFetcher = new SlippyTileFetcher(esriTemplate, true, "./tilecache", 256);
        this.tileRenderer = new WorldMapTileLayerRenderer(this.tileFetcher);
        this.gridOverlayRenderer = new GridOverlayRenderer();

        // ensure properties contain slippy info
        this.gcc.getProperties().put("slippyZoom", slippyZoom);
        this.gcc.getProperties().put("viewOffsetX", viewOffsetX);
        this.gcc.getProperties().put("viewOffsetY", viewOffsetY);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                WorldMapCanvas.this.update();
            }
        };
        timer.start();

        // Mouse handlers for pan/zoom/select
        this.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.MIDDLE || (e.getButton() == MouseButton.PRIMARY && e.isShiftDown())) {
                panning = true;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        });

        this.setOnMouseReleased(e -> {
            panning = false;
        });

        this.setOnMouseDragged(e -> {
            if (panning) {
                double dx = e.getX() - lastMouseX;
                double dy = e.getY() - lastMouseY;
                // dragging moves the map: subtract deltas from view offset
                viewOffsetX -= dx;
                viewOffsetY -= dy;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                // update properties for renderers
                this.gcc.getProperties().put("viewOffsetX", viewOffsetX);
                this.gcc.getProperties().put("viewOffsetY", viewOffsetY);
            }
        });

        this.setOnScroll((ScrollEvent e) -> {
            // ctrl+wheel performs smooth fractional zoom; plain wheel pans vertically
            if (e.isControlDown()) {
                double mouseX = e.getX();
                double mouseY = e.getY();
                double worldX = viewOffsetX + mouseX;
                double worldY = viewOffsetY + mouseY;
                double prevScale = zoomScale;
                // capture previous integer zoom level before we normalize zoomScale
                int prevZ = slippyZoom;

                // compute scale factor based on wheel delta (make it gradual)
                double factor = Math.pow(2.0, e.getDeltaY() / 360.0); // 360 delta => double
                zoomScale = zoomScale * factor;

                // normalize zoomScale into [0.5,2) by adjusting integer slippyZoom
                while (zoomScale >= 2.0) {
                    slippyZoom++;
                    zoomScale /= 2.0;
                }
                while (zoomScale < 0.5) {
                    slippyZoom--;
                    zoomScale *= 2.0;
                }

                // compute geographic point under cursor using previous scale and previous
                // integer zoom
                double pxBase = worldX / prevScale;
                double pyBase = worldY / prevScale;
                // Use prevZ here (the integer zoom before normalization). pxBase/pyBase are in
                // pixel coordinates at the previous fractional zoom, so converting using the
                // old integer zoom yields the correct lat/lon and prevents large jumps.
                double[] latlon = org.baseagent.util.GeoUtils.pixelXYToLatLon(pxBase, pyBase, prevZ, 256);

                // compute new world pixel for same geo point at new slippyZoom and scale
                double[] newPxPy = org.baseagent.util.GeoUtils.latLonToPixelXY(latlon[0], latlon[1], slippyZoom, 256);
                viewOffsetX = newPxPy[0] * zoomScale - mouseX;
                viewOffsetY = newPxPy[1] * zoomScale - mouseY;

                this.gcc.getProperties().put("slippyZoom", slippyZoom);
                this.gcc.getProperties().put("viewOffsetX", viewOffsetX);
                this.gcc.getProperties().put("viewOffsetY", viewOffsetY);
                this.gcc.getProperties().put("zoomScale", zoomScale);
            } else {
                // plain wheel pans vertically
                viewOffsetY -= e.getDeltaY();
                this.gcc.getProperties().put("viewOffsetY", viewOffsetY);
            }
            e.consume();
        });

        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && !e.isShiftDown()) {
                // selection
                double mouseX = e.getX();
                double mouseY = e.getY();
                double worldX = viewOffsetX + mouseX;
                double worldY = viewOffsetY + mouseY;
                // find agent whose pixel coords fall near this world point
                selectedAgent = null;
                if (gcc.getSimulation() != null) {
                    for (org.baseagent.Agent a : gcc.getSimulation().getAgents()) {
                        if (a instanceof WorldMapAgent) {
                            WorldMapAgent ma = (WorldMapAgent) a;
                            double[] pxpy = org.baseagent.util.GeoUtils.latLonToPixelXY(ma.getLatitude(),
                                    ma.getLongitude(), slippyZoom, 256);
                            double px = pxpy[0] * zoomScale;
                            double py = pxpy[1] * zoomScale;
                            double dx = px - worldX;
                            double dy = py - worldY;
                            double dist2 = dx * dx + dy * dy;
                            if (dist2 < 100) { // within 10px
                                selectedAgent = ma;
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    public void setMap(WorldMap map) {
        this.map = map;
        this.dummyGrid = new Grid(map.getWidthInTiles(), map.getHeightInTiles());
        this.gcc = new GridCanvasContext(this.gcc.getSimulation(), this.dummyGrid, null, this.gcc.getCellWidth(),
                this.gcc.getCellHeight(), this.gcc.getCellXSpacing(), this.gcc.getCellYSpacing());
    }

    public WorldMap getMap() {
        return this.map;
    }

    public Object getSelectedAgent() {
        return this.selectedAgent;
    }

    public void addMapLayerRenderer(String layerName, WorldMapLayerRenderer r) {
        orderedListOfLayerNames.add(layerName);
        renderersByName.put(layerName, r);
    }

    public void removeMapLayerRenderer(String layerName) {
        orderedListOfLayerNames.remove(layerName);
        renderersByName.remove(layerName);
    }

    public List<GridDrawable> getCustomDrawables() {
        return this.customDrawables;
    }

    public void addToast(Toast toast) {
        this.toasts.add(toast);
    }

    public void removeToast(Toast toast) {
        this.toasts.remove(toast);
    }

    public void setSimulation(org.baseagent.sim.Simulation sim) {
        this.gcc = new GridCanvasContext(sim, this.dummyGrid, null, this.gcc.getCellWidth(), this.gcc.getCellHeight(),
                this.gcc.getCellXSpacing(), this.gcc.getCellYSpacing());
    }

    public void update() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gcc.setGraphicsContext(gc);
        // expose current slippy view to renderers/agents
        gcc.getProperties().put("slippyZoom", slippyZoom);
        gcc.getProperties().put("viewOffsetX", viewOffsetX);
        gcc.getProperties().put("viewOffsetY", viewOffsetY);
        gcc.getProperties().put("zoomScale", zoomScale);

        // Clear
        Color backgroundColor = Color.WHITE;
        if (gcc.getColorPalette().size() > 0)
            backgroundColor = gcc.getColorPalette().get(0);
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        gc.save();
        // No global transform; tileRenderer and agents use viewOffsetX/viewOffsetY to
        // compute screen positions

        // draw base tiles
        if (tileRenderer != null) {
            tileRenderer.draw(gcc, null, this.getWidth(), this.getHeight());
        }

        // Draw map layers
        for (String layerName : orderedListOfLayerNames) {
            WorldMapLayerRenderer renderer = renderersByName.get(layerName);
            WorldMapGridLayer layer = (map == null) ? null : map.getMapLayer(layerName);
            if (renderer != null && layer != null) {
                renderer.draw(gcc, layer, this.getWidth(), this.getHeight());
            }
        }

        // custom drawables
        customDrawables.forEach(d -> d.drawBefore(gcc));

        // draw agents
        if (gcc.getSimulation() != null) {
            for (org.baseagent.Agent a : gcc.getSimulation().getAgents()) {
                if (a instanceof WorldMapAgent) {
                    ((WorldMapAgent) a).draw(gcc);
                }
            }
        }

        customDrawables.forEach(d -> d.draw(gcc));
        customDrawables.forEach(d -> d.drawAfter(gcc));

        gc.restore();

        // draw toasts on top (screen-space)
        for (Toast t : toasts) {
            t.draw(gcc);
        }
    }

    public void setSlippyZoom(int z) {
        this.slippyZoom = z;
        this.gcc.getProperties().put("slippyZoom", slippyZoom);
    }

    public int getSlippyZoom() {
        return this.slippyZoom;
    }

    public void setTileSource(String urlTemplate, boolean useDiskCache, String cacheDir) {
        this.tileFetcher = new SlippyTileFetcher(urlTemplate, useDiskCache, cacheDir, 256);
        this.tileRenderer = new WorldMapTileLayerRenderer(this.tileFetcher);
    }

    /** Shutdown background resources (tile fetcher). Call on application exit. */
    public void shutdown() {
        if (this.tileFetcher != null) {
            this.tileFetcher.shutdown();
        }
    }

    /** Center the view on the given lat/lon at the specified slippy zoom. */
    public void centerOn(double lat, double lon, int zoom) {
        int z = Math.max(0, Math.min(19, zoom));
        double[] pxpy = org.baseagent.util.GeoUtils.latLonToPixelXY(lat, lon, z, 256);
        double px = pxpy[0];
        double py = pxpy[1];
        this.slippyZoom = z;
        // keep current zoomScale; set view offset in scaled pixels
        this.viewOffsetX = px * zoomScale - (this.getWidth() / 2.0);
        this.viewOffsetY = py * zoomScale - (this.getHeight() / 2.0);
        this.gcc.getProperties().put("slippyZoom", slippyZoom);
        this.gcc.getProperties().put("viewOffsetX", viewOffsetX);
        this.gcc.getProperties().put("viewOffsetY", viewOffsetY);
    }

    public GridCanvasContext getGridCanvasContext() {
        return this.gcc;
    }

    public static final String DEFAULT_MAP_CANVAS_ID = "DEFAULT_MAP_CANVAS_ID";
}