package net.pl3x.map.world;

import ca.spottedleaf.starlight.common.light.SWMRNibbleArray;
import com.mojang.datafixers.util.Either;
import io.papermc.paper.util.WorldUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.pl3x.map.image.Image;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.util.ReflectionHelper;

public class ChunkHelper {
    private static final int CHUNK_CACHE_SIZE = (int) Math.ceil((Image.SIZE + 2) * (Image.SIZE + 2) * (4F / 3));

    private final Map<Long, ChunkAccess> chunkCache;
    private final Render render;
    private final Registry<Biome> biomeRegistry;

    private final int minSection;
    private final int totalLightSections;

    public ChunkHelper(Render render) {
        this.render = render;
        ServerLevel level = render.getWorld().getLevel();
        this.biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
        this.minSection = WorldUtil.getMinLightSection(level);
        this.totalLightSections = WorldUtil.getMaxLightSection(level) - this.minSection + 1;
        this.chunkCache = new HashMap<>(CHUNK_CACHE_SIZE);
    }

    public void clear() {
        this.chunkCache.clear();
    }

    public ChunkAccess getChunk(ServerLevel level, int chunkX, int chunkZ) {
        return this.chunkCache.computeIfAbsent(ChunkPos.asLong(chunkX, chunkZ), k -> getChunkFast(level, chunkX, chunkZ));
    }

    @SuppressWarnings("unused")
    public ChunkAccess getChunkSlow(ServerLevel level, int chunkX, int chunkZ) {
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> future =
                level.getChunkSource().getChunkAtAsynchronously(chunkX, chunkZ, false, false);
        while (!future.isDone()) {
            if (this.render.isCancelled()) {
                return null;
            }
        }
        return future.join().left().orElse(null);
    }

    // ChunkSerializer#loadChunk
    public ChunkAccess getChunkFast(ServerLevel level, int chunkX, int chunkZ) {
        // try to get chunk if its already loaded
        ChunkAccess chunk = level.getChunkIfLoadedImmediately(chunkX, chunkZ);
        if (chunk != null) {
            return chunk;
        }

        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

        // load chunk NBT from region file
        CompoundTag nbt = null;
        try {
            RegionFile regionFile = level.chunkSource.chunkMap.regionFileCache.getRegionFile(chunkPos, true, true);
            //noinspection ConstantConditions
            if (regionFile != null) {
                nbt = level.chunkSource.chunkMap.regionFileCache.read(chunkPos, regionFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //noinspection ConstantConditions
        if (nbt == null) {
            return null;
        }

        // we only care about "full" chunks (aka, level chunks)
        if (ChunkSerializer.getChunkTypeFromTag(nbt) != ChunkStatus.ChunkType.LEVELCHUNK) {
            return null;
        }

        SWMRNibbleArray[] blockNibbles = new SWMRNibbleArray[this.totalLightSections];
        SWMRNibbleArray[] skyNibbles = new SWMRNibbleArray[this.totalLightSections];
        ListTag sectionsNBT = nbt.getList("sections", 10);
        LevelChunkSection[] levelChunkSections = new LevelChunkSection[level.getSectionsCount()];

        // build only the required palettes from chunk sections
        for (int j = 0; j < sectionsNBT.size(); ++j) {
            populatePalettesAndLight(level, sectionsNBT, levelChunkSections, j, blockNibbles, skyNibbles, this.minSection);
        }

        // create our chunk
        chunk = new LevelChunk(level.getLevel(), chunkPos, UpgradeData.EMPTY, new LevelChunkTicks<>(), new LevelChunkTicks<>(), nbt.getLong("InhabitedTime"), levelChunkSections, null, null);

        // finish up
        chunk.setBlockNibbles(blockNibbles);
        chunk.setSkyNibbles(skyNibbles);
        populateHeightmaps(chunk, nbt);
        level.getChunkSource().getLightEngine().retainData(chunkPos, true);

        // rejoice
        return chunk;
    }

    private void populatePalettesAndLight(ServerLevel level, ListTag sectionsNBT, LevelChunkSection[] levelChunkSections, int j, SWMRNibbleArray[] blockNibbles, SWMRNibbleArray[] skyNibbles, int minSection) {
        CompoundTag chunkSectionNBT = sectionsNBT.getCompound(j);
        byte chunkYPos = chunkSectionNBT.getByte("Y");
        int index = level.getSectionIndexFromSectionY(chunkYPos);
        if (index >= 0 && index < levelChunkSections.length) {
            levelChunkSections[index] = ReflectionHelper.createLevelChunkSection(minSection, chunkSectionNBT, this.biomeRegistry);
        }
        try {
            byte[] blockBytes = chunkSectionNBT.contains("BlockLight", 7) ? chunkSectionNBT.getByteArray("BlockLight").clone() : null;
            blockNibbles[chunkYPos - minSection] = new SWMRNibbleArray(blockBytes, chunkSectionNBT.getInt("starlight.blocklight_state"));
            if (level.dimensionType().hasSkyLight()) {
                byte[] lightBytes = chunkSectionNBT.contains("SkyLight", 7) ? chunkSectionNBT.getByteArray("SkyLight").clone() : null;
                skyNibbles[chunkYPos - minSection] = new SWMRNibbleArray(lightBytes, chunkSectionNBT.getInt("starlight.skylight_state"));
            }
        } catch (Exception ignore) {
        }
    }

    private void populateHeightmaps(ChunkAccess chunk, CompoundTag nbt) {
        CompoundTag heightmaps = nbt.getCompound("Heightmaps");
        String key = Heightmap.Types.WORLD_SURFACE.getSerializationKey();
        if (heightmaps.contains(key, 12)) {
            chunk.setHeightmap(Heightmap.Types.WORLD_SURFACE, heightmaps.getLongArray(key));
        }
        //Heightmap.primeHeightmaps(chunk, EnumSet.of(Heightmap.Types.WORLD_SURFACE));
    }

    // BiomeManager#getBiome
    public Holder<Biome> getBiome(World world, ChunkAccess chunk, BlockPos pos) {
        int i = pos.getX() - 2;
        int j = pos.getY() - 2;
        int k = pos.getZ() - 2;
        int l = i >> 2;
        int m = j >> 2;
        int n = k >> 2;
        double d = (double) (i & 3) / 4.0D;
        double e = (double) (j & 3) / 4.0D;
        double f = (double) (k & 3) / 4.0D;
        int o = 0;
        double g = Double.POSITIVE_INFINITY;

        for (int p = 0; p < 8; ++p) {
            boolean bl = (p & 4) == 0;
            boolean bl2 = (p & 2) == 0;
            boolean bl3 = (p & 1) == 0;
            int q = bl ? l : l + 1;
            int r = bl2 ? m : m + 1;
            int s = bl3 ? n : n + 1;
            double h = bl ? d : d - 1.0D;
            double t = bl2 ? e : e - 1.0D;
            double u = bl3 ? f : f - 1.0D;
            double v = getFiddledDistance(world.getBiomeSeed(), q, r, s, h, t, u);
            if (g > v) {
                o = p;
                g = v;
            }
        }

        int w = (o & 4) == 0 ? l : l + 1;
        int x = (o & 2) == 0 ? m : m + 1;
        int y = (o & 1) == 0 ? n : n + 1;
        // had to copy this entire method just to change this... :3
        //noinspection SuspiciousNameCombination
        return getNoiseBiome(world.getLevel(), chunk, w, x, y);
    }

    // BiomeManager#getFiddledDistance
    private static double getFiddledDistance(long l, int i, int j, int k, double d, double e, double f) {
        long m = LinearCongruentialGenerator.next(l, i);
        m = LinearCongruentialGenerator.next(m, j);
        m = LinearCongruentialGenerator.next(m, k);
        m = LinearCongruentialGenerator.next(m, i);
        m = LinearCongruentialGenerator.next(m, j);
        m = LinearCongruentialGenerator.next(m, k);
        double g = getFiddle(m);
        m = LinearCongruentialGenerator.next(m, l);
        double h = getFiddle(m);
        m = LinearCongruentialGenerator.next(m, l);
        double n = getFiddle(m);
        return Mth.square(f + n) + Mth.square(e + h) + Mth.square(d + g);
    }

    // BiomeManager#getFiddle
    private static double getFiddle(long l) {
        double d = (double) Math.floorMod(l >> 24, 1024) / 1024.0D;
        return (d - 0.5D) * 0.9D;
    }

    // nifty trick - don't schedule a blocking getChunk call..
    // LevelReader#getNoiseBiome
    private Holder<Biome> getNoiseBiome(ServerLevel level, ChunkAccess chunk, int biomeX, int biomeY, int biomeZ) {
        int cX = QuartPos.toSection(biomeX);
        int cZ = QuartPos.toSection(biomeZ);
        if (chunk.getPos().x != cX || chunk.getPos().z != cZ) {
            ChunkAccess chunkAccess = getChunk(level, cX, cZ);
            if (chunkAccess != null) {
                return chunkAccess.getNoiseBiome(biomeX, biomeY, biomeZ);
            }
        }
        return chunk.getNoiseBiome(biomeX, biomeY, biomeZ);
    }
}
