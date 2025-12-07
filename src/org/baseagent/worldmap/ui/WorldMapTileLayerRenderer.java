package org.baseagent.worldmap.ui;

import org.baseagent.grid.ui.GridCanvasContext;
import org.baseagent.worldmap.WorldMapGridLayer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class WorldMapTileLayerRenderer implements WorldMapLayerRenderer {
    private final SlippyTileFetcher fetcher;
    private final int prefetchRadius = 1; // tiles around viewport to prefetch

    public WorldMapTileLayerRenderer(SlippyTileFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public void draw(GridCanvasContext gcc, WorldMapGridLayer layer, double canvasWidth, double canvasHeight) {
        // read slippy properties from gcc
        Object zProp = gcc.getProperties().get("slippyZoom");
        Object offXProp = gcc.getProperties().get("viewOffsetX");
        Object offYProp = gcc.getProperties().get("viewOffsetY");
        Object scaleProp = gcc.getProperties().get("zoomScale");
        int z = (zProp instanceof Number) ? ((Number)zProp).intValue() : 2;
        double viewOffsetX = (offXProp instanceof Number) ? ((Number)offXProp).doubleValue() : 0.0;
        double viewOffsetY = (offYProp instanceof Number) ? ((Number)offYProp).doubleValue() : 0.0;
        double zoomScale = (scaleProp instanceof Number) ? ((Number)scaleProp).doubleValue() : 1.0;

        int tileSize = fetcher.getTileSize();
        double scaledTileSize = tileSize * zoomScale;
        int minTileX = (int)Math.floor(viewOffsetX / scaledTileSize);
        int minTileY = (int)Math.floor(viewOffsetY / scaledTileSize);
        int maxTileX = (int)Math.floor((viewOffsetX + canvasWidth) / scaledTileSize);
        int maxTileY = (int)Math.floor((viewOffsetY + canvasHeight) / scaledTileSize);

        GraphicsContext gc = gcc.getGraphicsContext();

        for (int tx = minTileX; tx <= maxTileX; tx++) {
            for (int ty = minTileY; ty <= maxTileY; ty++) {
                Image img = fetcher.getTileImage(z, tx, ty);
                double screenX = tx * scaledTileSize - viewOffsetX;
                double screenY = ty * scaledTileSize - viewOffsetY;
                if (img != null) {
                    gc.drawImage(img, screenX, screenY, scaledTileSize, scaledTileSize);
                } else {
                    // draw lightweight placeholder while tile loads
                    gc.setFill(Color.web("#e6e6e6"));
                    gc.fillRect(screenX, screenY, tileSize, tileSize);
                    gc.setStroke(Color.web("#cccccc"));
                    gc.strokeRect(screenX, screenY, scaledTileSize, scaledTileSize);
                    // small loading indicator
                    gc.setFill(Color.web("#888888"));
                    gc.fillOval(screenX + scaledTileSize/2.0 - 6, screenY + scaledTileSize/2.0 - 6, 12, 12);

                    // asynchronously fetch the tile and neighbouring tiles to prefill cache
                    fetcher.getTileImageAsync(z, tx, ty, 0, (image) -> {
                        // no-op; next frame will paint from cache
                    });

                    // prefetch neighbors
                    for (int dx = -prefetchRadius; dx <= prefetchRadius; dx++) {
                        for (int dy = -prefetchRadius; dy <= prefetchRadius; dy++) {
                            int nx = tx + dx;
                            int ny = ty + dy;
                            if (nx == tx && ny == ty) continue;
                            int priority = Math.abs(dx) + Math.abs(dy) + 1; // nearer neighbors higher priority
                            fetcher.getTileImageAsync(z, nx, ny, priority, (image) -> {
                                // no-op; cached when available
                            });
                        }
                    }
                }
            }
        }
    }
}