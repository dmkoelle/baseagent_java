package org.baseagent.util;

public class GeoUtils {
    private static final double R_MAJOR = 6378137.0;
    private static final double DEG2RAD = Math.PI / 180.0;

    /**
     * Convert lat/lon to WebMercator meters (EPSG:3857)
     */
    public static double[] latLonToMeters(double lat, double lon) {
        double x = R_MAJOR * lon * DEG2RAD;
        double y = R_MAJOR * Math.log(Math.tan(Math.PI / 4.0 + (lat * DEG2RAD) / 2.0));
        return new double[] { x, y };
    }

    /**
     * Convert WebMercator meters back to lat/lon (inverse of latLonToMeters)
     */
    public static double[] metersToLatLon(double x, double y) {
        double lon = (x / R_MAJOR) / DEG2RAD;
        double latRad = 2.0 * Math.atan(Math.exp(y / R_MAJOR)) - Math.PI / 2.0;
        double lat = latRad / DEG2RAD;
        return new double[] { lat, lon };
    }

    /**
     * Convert lat/lon to pixel X/Y in slippy tile scheme at given zoom and
     * tileSize. Returns [pixelX, pixelY] in global pixel coordinates.
     */
    public static double[] latLonToPixelXY(double lat, double lon, int zoom, int tileSize) {
        double sinLat = Math.sin(lat * DEG2RAD);
        double n = Math.pow(2.0, zoom);
        double px = (lon + 180.0) / 360.0 * tileSize * n;
        double py = (0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI)) * tileSize * n;
        return new double[] { px, py };
    }

    /**
     * Convert global pixel X/Y to lat/lon at given zoom and tileSize. Returns [lat,
     * lon].
     */
    public static double[] pixelXYToLatLon(double pixelX, double pixelY, int zoom, int tileSize) {
        double n = Math.pow(2.0, zoom);
        double lon = pixelX / (tileSize * n) * 360.0 - 180.0;
        double y = 0.5 - (pixelY / (tileSize * n));
        double lat = 90.0 - 360.0 * Math.atan(Math.exp(-y * 2.0 * Math.PI)) / Math.PI;
        return new double[] { lat, lon };
    }

    /**
     * Convert global pixel coordinates to tile X,Y indices and pixel offsets.
     */
    public static int[] pixelXYToTileXY(double pixelX, double pixelY, int tileSize) {
        int tileX = (int) Math.floor(pixelX / tileSize);
        int tileY = (int) Math.floor(pixelY / tileSize);
        return new int[] { tileX, tileY };
    }

    /**
     * Normalize longitude to be within [-180, 180)
     */
    public static double normalizeLongitude(double lon) {
        double l = lon;
        while (l < -180.0)
            l += 360.0;
        while (l >= 180.0)
            l -= 360.0;
        return l;
    }
}