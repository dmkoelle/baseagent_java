package org.baseagent.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javafx.scene.image.Image;

public class SlippyTileFetcher {
    private final String urlTemplate;
    private final boolean useDiskCache;
    private final Path cacheRoot;
    private final int tileSize;
    private final LRUCache<String, Image> memoryCache;
    private final BlockingQueue<FetchTask> fetchQueue;
    private final Thread[] workers;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final int workerCount = 4;
    private final int memoryCacheMaxEntries = 512;
    // Track tiles currently scheduled/being fetched to avoid duplicate queue entries
    private final Set<String> pending = ConcurrentHashMap.newKeySet();

    public SlippyTileFetcher(String urlTemplate, boolean useDiskCache, String cacheDir, int tileSize) {
        this.urlTemplate = urlTemplate;
        this.useDiskCache = useDiskCache;
        this.cacheRoot = Paths.get(cacheDir);
        this.tileSize = tileSize;
        this.memoryCache = new LRUCache<>(memoryCacheMaxEntries);
        this.fetchQueue = new PriorityBlockingQueue<>();
        this.workers = new Thread[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workers[i] = new Thread(() -> workerLoop());
            workers[i].setDaemon(true);
            workers[i].start();
        }
        try {
            if (useDiskCache) Files.createDirectories(cacheRoot);
        } catch (Exception e) {
            // ignore
        }
    }

    private String keyFor(int z, int x, int y) {
        return z + "/" + x + "/" + y;
    }

    /**
     * Non-blocking: returns cached Image if available, otherwise schedules a background fetch and returns null.
     */
    public Image getTileImage(int z, int x, int y) {
        String key = keyFor(z, x, y);
        synchronized (memoryCache) {
            Image img = memoryCache.get(key);
            if (img != null) return img;
        }

        // try disk cache synchronously if enabled
        if (useDiskCache) {
            try {
                Path cachePath = cacheRoot.resolve(String.valueOf(z)).resolve(String.valueOf(x)).resolve(String.valueOf(y) + ".png");
                if (Files.exists(cachePath)) {
                    try (InputStream is = new FileInputStream(cachePath.toFile())) {
                        Image diskImg = new Image(is);
                        synchronized (memoryCache) { memoryCache.put(key, diskImg); }
                        return diskImg;
                    }
                }
            } catch (Exception ex) {
                // ignore disk read errors
            }
        }

        // schedule background fetch with low priority (larger number = lower priority)
        scheduleFetchWithPriority(z, x, y, Integer.MAX_VALUE, null);
        return null;
    }

    /**
     * Asynchronous fetch that invokes callback when image is available.
     * Priority: lower value means higher priority (0 = highest).
     */
    public void getTileImageAsync(int z, int x, int y, int priority, Consumer<Image> callback) {
        String key = keyFor(z, x, y);
        synchronized (memoryCache) {
            Image img = memoryCache.get(key);
            if (img != null) {
                if (callback != null) callback.accept(img);
                return;
            }
        }
        scheduleFetchWithPriority(z, x, y, priority, callback);
    }

    private void scheduleFetchWithPriority(int z, int x, int y, int priority, Consumer<Image> callback) {
        String key = keyFor(z, x, y);
        if (!pending.add(key)) {
            // already pending; do not enqueue duplicate
            return;
        }
        FetchTask task = new FetchTask(z, x, y, priority, callback, key);
        fetchQueue.offer(task);
    }

    private void workerLoop() {
        while (running.get()) {
            FetchTask task = null;
            try {
                task = fetchQueue.take();
                // mark as being processed (pending already contains key)
                String key = keyFor(task.z, task.x, task.y);
                synchronized (memoryCache) {
                    Image cached = memoryCache.get(key);
                    if (cached != null) {
                        if (task.callback != null) task.callback.accept(cached);
                        pending.remove(key);
                        continue;
                    }
                }

                int z = task.z;
                int x = task.x;
                int y = task.y;
                int num = 1 << z;
                int xx = ((x % num) + num) % num;
                int yy = y;
                if (yy < 0 || yy >= num) {
                    if (task.callback != null) task.callback.accept(null);
                    pending.remove(key);
                    continue;
                }

                Path cachePath = cacheRoot.resolve(String.valueOf(z)).resolve(String.valueOf(xx)).resolve(String.valueOf(yy) + ".png");
                File cacheFile = cachePath.toFile();
                if (useDiskCache && cacheFile.exists()) {
                    try (InputStream is = new FileInputStream(cacheFile)) {
                        Image diskImg = new Image(is);
                        synchronized (memoryCache) { memoryCache.put(key, diskImg); }
                        if (task.callback != null) task.callback.accept(diskImg);
                        pending.remove(key);
                        continue;
                    } catch (Exception ex) {
                        // continue to network fetch
                    }
                }

                String urlStr = urlTemplate.replace("{z}", String.valueOf(z)).replace("{x}", String.valueOf(xx)).replace("{y}", String.valueOf(yy));
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestProperty("User-Agent", "BaseAgent-SlippyTileFetcher/1.0");
                conn.connect();
                int code = conn.getResponseCode();
                if (code >= 400) {
                    conn.disconnect();
                    if (task.callback != null) task.callback.accept(null);
                    pending.remove(key);
                    continue;
                }
                try (InputStream is = conn.getInputStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buf = new byte[8192];
                    int r;
                    while ((r = is.read(buf)) != -1) baos.write(buf, 0, r);
                    byte[] bytes = baos.toByteArray();
                    if (useDiskCache) {
                        try {
                            Files.createDirectories(cachePath.getParent());
                            try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
                                fos.write(bytes);
                            }
                        } catch (Exception ex) {
                            // ignore caching errors
                        }
                    }
                    Image img = new Image(new java.io.ByteArrayInputStream(bytes));
                    synchronized (memoryCache) { memoryCache.put(key, img); }
                    if (task.callback != null) task.callback.accept(img);
                    pending.remove(key);
                 } finally {
                     conn.disconnect();
                 }
             } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // ensure pending is cleaned up on unexpected errors for this task
                try { if (task != null) pending.remove(keyFor(task.z, task.x, task.y)); } catch (Exception ex) { }
                // ignore other exceptions
            }
         }
     }

    // simple fetch task with priority (lower value => higher priority)
    private static class FetchTask implements Comparable<FetchTask> {
        final int z, x, y;
        final int priority;
        final Consumer<Image> callback;
        final String key;
        FetchTask(int z, int x, int y, int priority, Consumer<Image> callback, String key) { this.z = z; this.x = x; this.y = y; this.priority = priority; this.callback = callback; this.key = key; }
        @Override public int compareTo(FetchTask o) { return Integer.compare(this.priority, o.priority); }
    }

    // Simple LRU cache backed by LinkedHashMap
    private static class LRUCache<K, V> extends java.util.LinkedHashMap<K, V> {
        private final int maxEntries;
        LRUCache(int maxEntries) { super(maxEntries + 1, 0.75f, true); this.maxEntries = maxEntries; }
        @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) { return size() > maxEntries; }
    }

    public int getTileSize() { return this.tileSize; }

    public void shutdown() {
        running.set(false);
        for (Thread t : workers) if (t != null) t.interrupt();
    }
}