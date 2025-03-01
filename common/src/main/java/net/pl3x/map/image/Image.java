package net.pl3x.map.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.pl3x.map.Key;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.image.io.IO;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.util.Mathf;
import net.pl3x.map.world.World;

public class Image {
    private static final Map<Path, ReadWriteLock> FILE_LOCKS = new ConcurrentHashMap<>();

    public static final int SIZE = 512;
    public static final String DIR_PATH = "%d/%s/";
    public static final String FILE_PATH = "%d_%d.%s";

    private final Key id;
    private final World world;
    private final int regionX;
    private final int regionZ;

    private final int[] pixels = new int[SIZE * SIZE];

    private final IO.Type io;

    public Image(Key id, World world, int regionX, int regionZ) {
        this.id = id;
        this.world = world;
        this.regionX = regionX;
        this.regionZ = regionZ;

        this.io = IO.get(Config.WEB_TILE_FORMAT);
    }

    public int getIndex(int x, int z) {
        return z * SIZE + x;
    }

    public int getPixel(int x, int z) {
        return this.pixels[getIndex(x, z)];
    }

    public void setPixel(int x, int z, int color) {
        this.pixels[getIndex(x, z)] = color;
    }

    public void saveToDisk() {
        Path worldDir = this.world.getTilesDir();
        for (int zoom = 0; zoom <= this.world.getConfig().ZOOM_MAX_OUT; zoom++) {
            Path dirPath = worldDir.resolve(String.format(DIR_PATH, zoom, this.id));

            // create directories if they don't exist
            FileUtil.createDirs(dirPath);

            // calculate correct sizes for this zoom level
            int step = Mathf.pow2(zoom);
            int size = SIZE / step;

            Path filePath = dirPath.resolve(String.format(FILE_PATH,
                    (int) Math.floor((double) this.regionX / step),
                    (int) Math.floor((double) this.regionZ / step),
                    this.io.extension()));

            ReadWriteLock lock = FILE_LOCKS.computeIfAbsent(filePath, k -> new ReentrantReadWriteLock(true));
            lock.writeLock().lock();

            // read existing image from disk
            BufferedImage buffer = getBuffer(filePath);

            // write new pixels
            writePixels(buffer, size, step);

            // finally, save buffer to disk
            this.io.write(filePath, buffer);

            lock.writeLock().unlock();
        }
    }

    private BufferedImage getBuffer(Path path) {
        BufferedImage buffer = null;
        try {
            if (Files.exists(path) && Files.size(path) > 0) {
                buffer = this.io.read(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // if not file was loaded, create a new image
        if (buffer == null) {
            buffer = this.io.createBuffer();
        }

        return buffer;
    }

    private void writePixels(BufferedImage buffer, int size, int step) {
        int baseX = (this.regionX * size) & (SIZE - 1);
        int baseZ = (this.regionZ * size) & (SIZE - 1);
        for (int x = 0; x < SIZE; x += step) {
            for (int z = 0; z < SIZE; z += step) {
                int rgb = getPixel(x, z);
                if (rgb == 0) {
                    // skipping 0 prevents overwrite existing
                    // parts of the buffer of existing images
                    continue;
                }
                if (step > 1) {
                    // merge pixel colors instead of skipping them
                    rgb = downSample(x, z, rgb, step);
                }
                buffer.setRGB(baseX + (x / step), baseZ + (z / step), rgb);
            }
        }
    }

    private int downSample(int x, int z, int rgb, int step) {
        int a = 0, r = 0, g = 0, b = 0, count = 0;
        for (int i = 0; i < step; i++) {
            for (int j = 0; j < step; j++) {
                if (i != 0 && j != 0) {
                    rgb = getPixel(x + i, z + j);
                }
                a += Colors.alpha(rgb);
                r += Colors.red(rgb);
                g += Colors.green(rgb);
                b += Colors.blue(rgb);
                count++;
            }
        }
        return Colors.argb(a / count, r / count, g / count, b / count);
    }

    public static class Holder {
        private final World world;
        private final RegionCoordinate region;
        private final Image image;

        public Holder(Key id, World world, RegionCoordinate region) {
            this.world = world;
            int regionX = region.getRegionX();
            int regionZ = region.getRegionZ();
            this.region = new RegionCoordinate(regionX, regionZ);
            this.image = new Image(id, world, regionX, regionZ);
        }

        public Image getImage() {
            return this.image;
        }

        public void save() {
            getImage().saveToDisk();

            // mark this region as done
            this.world.setScannedRegion(this.region);
        }
    }
}
