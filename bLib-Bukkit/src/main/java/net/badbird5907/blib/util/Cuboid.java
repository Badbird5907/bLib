package net.badbird5907.blib.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

import java.util.*;

public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable {
    protected final String worldName;
    protected final int x1;
    protected final int y1;
    protected final int z1;
    protected final int x2;
    protected final int y2;
    protected final int z2;

    public Cuboid(final Location l1, final Location l2) {
        if (!l1.getWorld().equals(l2.getWorld())) {
            throw new IllegalArgumentException("Locations must be on the same world");
        }
        this.worldName = l1.getWorld().getName();
        this.x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        this.y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        this.z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        this.x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        this.y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        this.z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
    }

    public Cuboid(final Location l1) {
        this(l1, l1);
    }

    public Cuboid(final Cuboid other) {
        this(other.getWorld().getName(), other.x1, other.y1, other.z1, other.x2, other.y2, other.z2);
    }

    public Cuboid(final World world, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this.worldName = world.getName();
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    private Cuboid(final String worldName, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this.worldName = worldName;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public Cuboid(final Map<String, Object> map) {
        this.worldName = (String) map.get("worldName");
        this.x1 = (int) map.get("x1");
        this.x2 = (int) map.get("x2");
        this.y1 = (int) map.get("y1");
        this.y2 = (int) map.get("y2");
        this.z1 = (int) map.get("z1");
        this.z2 = (int) map.get("z2");
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("worldName", this.worldName);
        map.put("x1", this.x1);
        map.put("y1", this.y1);
        map.put("z1", this.z1);
        map.put("x2", this.x2);
        map.put("y2", this.y2);
        map.put("z2", this.z2);
        return map;
    }

    public Location getLowerNE() {
        return new Location(this.getWorld(), this.x1, this.y1, this.z1);
    }

    public Location getUpperSW() {
        return new Location(this.getWorld(), this.x2, this.y2, this.z2);
    }

    public List<Block> getBlocks() {
        final Iterator<Block> blockI = this.iterator();
        final List<Block> copy = new ArrayList<Block>();
        while (blockI.hasNext()) {
            copy.add(blockI.next());
        }
        return copy;
    }

    public Location getCenter() {
        final int x1 = this.getUpperX() + 1;
        final int y1 = this.getUpperY() + 1;
        final int z1 = this.getUpperZ() + 1;
        return new Location(this.getWorld(), this.getLowerX() + (x1 - this.getLowerX()) / 2.0, this.getLowerY() + (y1 - this.getLowerY()) / 2.0, this.getLowerZ() + (z1 - this.getLowerZ()) / 2.0);
    }

    public World getWorld() {
        final World world = Bukkit.getServer().getWorld(this.worldName);
        if (world == null) {
            throw new IllegalStateException("World '" + this.worldName + "' is not loaded");
        }
        return world;
    }

    public int getSizeX() {
        return this.x2 - this.x1 + 1;
    }

    public int getSizeY() {
        return this.y2 - this.y1 + 1;
    }

    public int getSizeZ() {
        return this.z2 - this.z1 + 1;
    }

    public int getLowerX() {
        return this.x1;
    }

    public int getLowerY() {
        return this.y1;
    }

    public int getLowerZ() {
        return this.z1;
    }

    public int getUpperX() {
        return this.x2;
    }

    public int getUpperY() {
        return this.y2;
    }

    public int getUpperZ() {
        return this.z2;
    }

    public Block[] corners() {
        final Block[] res = new Block[8];
        final World w = this.getWorld();
        res[0] = w.getBlockAt(this.x1, this.y1, this.z1);
        res[1] = w.getBlockAt(this.x1, this.y1, this.z2);
        res[2] = w.getBlockAt(this.x1, this.y2, this.z1);
        res[3] = w.getBlockAt(this.x1, this.y2, this.z2);
        res[4] = w.getBlockAt(this.x2, this.y1, this.z1);
        res[5] = w.getBlockAt(this.x2, this.y1, this.z2);
        res[6] = w.getBlockAt(this.x2, this.y2, this.z1);
        res[7] = w.getBlockAt(this.x2, this.y2, this.z2);
        return res;
    }

    public Block[] minCorners() {
        final Block[] res = new Block[4];
        final World w = this.getWorld();
        res[0] = w.getBlockAt(this.x1, this.y1, this.z1);
        return res;
    }

    public Cuboid expand(final CuboidDirection dir, final int amount) {
        switch (dir) {
            case NORTH: {
                return new Cuboid(this.worldName, this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case SOUTH: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2);
            }
            case EAST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2);
            }
            case WEST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount);
            }
            case DOWN: {
                return new Cuboid(this.worldName, this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2);
            }
            case UP: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + dir);
            }
        }
    }

    public Cuboid shift(final CuboidDirection dir, final int amount) {
        return this.expand(dir, amount).expand(dir.opposite(), -amount);
    }

    public Cuboid outset(final CuboidDirection dir, final int amount) {
        Cuboid c = null;
        switch (dir) {
            case HORIZONTAL: {
                c = this.expand(CuboidDirection.NORTH, amount).expand(CuboidDirection.SOUTH, amount).expand(CuboidDirection.EAST, amount).expand(CuboidDirection.WEST, amount);
                break;
            }
            case VERTICAL: {
                c = this.expand(CuboidDirection.DOWN, amount).expand(CuboidDirection.UP, amount);
                break;
            }
            case BOTH: {
                c = this.outset(CuboidDirection.HORIZONTAL, amount).outset(CuboidDirection.VERTICAL, amount);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + dir);
            }
        }
        return c;
    }

    public Cuboid inset(final CuboidDirection dir, final int amount) {
        return this.outset(dir, -amount);
    }

    public boolean contains(final int x, final int y, final int z) {
        return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
    }

    public boolean contains(final Block b) {
        return this.contains(b.getLocation());
    }

    public boolean contains(final Location l) {
        return this.worldName.equals(l.getWorld().getName()) && this.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public boolean contains(final Entity e) {
        return this.contains(e.getLocation());
    }

    public Cuboid grow(final int i) {
        return this.expand(CuboidDirection.NORTH, i).expand(CuboidDirection.SOUTH, i).expand(CuboidDirection.EAST, i).expand(CuboidDirection.WEST, i);
    }

    public int getVolume() {
        return this.getSizeX() * this.getSizeY() * this.getSizeZ();
    }

    public byte getAverageLightLevel() {
        long total = 0L;
        int n = 0;
        for (final Block b : this) {
            if (b.isEmpty()) {
                total += b.getLightLevel();
                ++n;
            }
        }
        return (byte) ((n > 0) ? ((byte) (total / n)) : 0);
    }

    public Cuboid contract() {
        return this.contract(CuboidDirection.DOWN).contract(CuboidDirection.SOUTH).contract(CuboidDirection.EAST).contract(CuboidDirection.UP).contract(CuboidDirection.NORTH).contract(CuboidDirection.WEST);
    }

    public Cuboid contract(final CuboidDirection dir) {
        Cuboid face = this.getFace(dir.opposite());
        switch (dir) {
            case DOWN: {
                while (face.containsOnly(Material.AIR) && face.getLowerY() > this.getLowerY()) {
                    face = face.shift(CuboidDirection.DOWN, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, face.getUpperY(), this.z2);
            }
            case UP: {
                while (face.containsOnly(Material.AIR) && face.getUpperY() < this.getUpperY()) {
                    face = face.shift(CuboidDirection.UP, 1);
                }
                return new Cuboid(this.worldName, this.x1, face.getLowerY(), this.z1, this.x2, this.y2, this.z2);
            }
            case NORTH: {
                while (face.containsOnly(Material.AIR) && face.getLowerX() > this.getLowerX()) {
                    face = face.shift(CuboidDirection.NORTH, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, face.getUpperX(), this.y2, this.z2);
            }
            case SOUTH: {
                while (face.containsOnly(Material.AIR) && face.getUpperX() < this.getUpperX()) {
                    face = face.shift(CuboidDirection.SOUTH, 1);
                }
                return new Cuboid(this.worldName, face.getLowerX(), this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case EAST: {
                while (face.containsOnly(Material.AIR) && face.getLowerZ() > this.getLowerZ()) {
                    face = face.shift(CuboidDirection.EAST, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, face.getUpperZ());
            }
            case WEST: {
                while (face.containsOnly(Material.AIR) && face.getUpperZ() < this.getUpperZ()) {
                    face = face.shift(CuboidDirection.WEST, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, face.getLowerZ(), this.x2, this.y2, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + dir);
            }
        }
    }

    public Cuboid getFace(final CuboidDirection dir) {
        switch (dir) {
            case DOWN: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);
            }
            case UP: {
                return new Cuboid(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
            }
            case NORTH: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);
            }
            case SOUTH: {
                return new Cuboid(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case EAST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);
            }
            case WEST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + dir);
            }
        }
    }

    public boolean containsOnly(final Material material) {
        for (final Block b : this) {
            if (b.getType() != material) {
                return false;
            }
        }
        return true;
    }
    public boolean containsOnly(final Block block) {
        for (final Block b : this) {
            if (b.getType() != block.getType()) {
                return false;
            }
        }
        return true;
    }

    public Cuboid getBoundingCuboid(final Cuboid other) {
        if (other == null) {
            return this;
        }
        final int xMin = Math.min(this.getLowerX(), other.getLowerX());
        final int yMin = Math.min(this.getLowerY(), other.getLowerY());
        final int zMin = Math.min(this.getLowerZ(), other.getLowerZ());
        final int xMax = Math.max(this.getUpperX(), other.getUpperX());
        final int yMax = Math.max(this.getUpperY(), other.getUpperY());
        final int zMax = Math.max(this.getUpperZ(), other.getUpperZ());
        return new Cuboid(this.worldName, xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public Block getRelativeBlock(final int x, final int y, final int z) {
        return this.getWorld().getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public Block getRelativeBlock(final World w, final int x, final int y, final int z) {
        return w.getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public List<Chunk> getChunks() {
        final List<Chunk> res = new ArrayList<Chunk>();
        final World w = this.getWorld();
        final int x1 = this.getLowerX() & 0xFFFFFFF0;
        final int x2 = this.getUpperX() & 0xFFFFFFF0;
        final int z1 = this.getLowerZ() & 0xFFFFFFF0;
        final int z2 = this.getUpperZ() & 0xFFFFFFF0;
        for (int x3 = x1; x3 <= x2; x3 += 16) {
            for (int z3 = z1; z3 <= z2; z3 += 16) {
                res.add(w.getChunkAt(x3 >> 4, z3 >> 4));
            }
        }
        return res;
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public Cuboid clone() {
        return new Cuboid(this);
    }

    @Override
    public String toString() {
        return "Cuboid: " + this.worldName + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2;
    }

    public List<Block> getWalls() {
        final List<Block> blocks = new ArrayList<Block>();
        final Location min = new Location(this.getWorld(), this.x1, this.y1, this.z1);
        final Location max = new Location(this.getWorld(), this.x2, this.y2, this.z2);
        final int minX = min.getBlockX();
        final int minY = min.getBlockY();
        final int minZ = min.getBlockZ();
        final int maxX = max.getBlockX();
        final int maxY = max.getBlockY();
        final int maxZ = max.getBlockZ();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                final Location minLoc = new Location(this.getWorld(), x, y, minZ);
                final Location maxLoc = new Location(this.getWorld(), x, y, maxZ);
                blocks.add(minLoc.getBlock());
                blocks.add(maxLoc.getBlock());
            }
        }
        for (int y2 = minY; y2 <= maxY; ++y2) {
            for (int z = minZ; z <= maxZ; ++z) {
                final Location minLoc = new Location(this.getWorld(), minX, y2, z);
                final Location maxLoc = new Location(this.getWorld(), maxX, y2, z);
                blocks.add(minLoc.getBlock());
                blocks.add(maxLoc.getBlock());
            }
        }
        return blocks;
    }

    public List<Block> getFaces() {
        final List<Block> blocks = new ArrayList<Block>();
        final Location min = new Location(this.getWorld(), this.x1, this.y1, this.z1);
        final Location max = new Location(this.getWorld(), this.x2, this.y2, this.z2);
        final int minX = min.getBlockX();
        final int minY = min.getBlockY();
        final int minZ = min.getBlockZ();
        final int maxX = max.getBlockX();
        final int maxY = max.getBlockY();
        final int maxZ = max.getBlockZ();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                blocks.add(new Location(this.getWorld(), x, y, minZ).getBlock());
                blocks.add(new Location(this.getWorld(), x, y, maxZ).getBlock());
            }
        }
        for (int y2 = minY; y2 <= maxY; ++y2) {
            for (int z = minZ; z <= maxZ; ++z) {
                blocks.add(new Location(this.getWorld(), minX, y2, z).getBlock());
                blocks.add(new Location(this.getWorld(), maxX, y2, z).getBlock());
            }
        }
        for (int z2 = minZ; z2 <= maxZ; ++z2) {
            for (int x2 = minX; x2 <= maxX; ++x2) {
                blocks.add(new Location(this.getWorld(), x2, minY, z2).getBlock());
                blocks.add(new Location(this.getWorld(), x2, maxY, z2).getBlock());
            }
        }
        return blocks;
    }

    public enum CuboidDirection {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        UP,
        DOWN,
        HORIZONTAL,
        VERTICAL,
        BOTH,
        UNKNOWN;

        public CuboidDirection opposite() {
            switch (this) {
                case NORTH: {
                    return CuboidDirection.SOUTH;
                }
                case EAST: {
                    return CuboidDirection.WEST;
                }
                case SOUTH: {
                    return CuboidDirection.NORTH;
                }
                case WEST: {
                    return CuboidDirection.EAST;
                }
                case HORIZONTAL: {
                    return CuboidDirection.VERTICAL;
                }
                case VERTICAL: {
                    return CuboidDirection.HORIZONTAL;
                }
                case UP: {
                    return CuboidDirection.DOWN;
                }
                case DOWN: {
                    return CuboidDirection.UP;
                }
                case BOTH: {
                    return CuboidDirection.BOTH;
                }
                default: {
                    return CuboidDirection.UNKNOWN;
                }
            }
        }
    }

    public class CuboidIterator implements Iterator<Block> {
        private final World w;
        private final int baseX;
        private final int baseY;
        private final int baseZ;
        private int x;
        private int y;
        private int z;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;

        public CuboidIterator(final World w, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
            this.w = w;
            this.baseX = Math.min(x1, x2);
            this.baseY = Math.min(y1, y2);
            this.baseZ = Math.min(z1, z2);
            this.sizeX = Math.abs(x2 - x1) + 1;
            this.sizeY = Math.abs(y2 - y1) + 1;
            this.sizeZ = Math.abs(z2 - z1) + 1;
            final int x3 = 0;
            this.z = x3;
            this.y = x3;
            this.x = x3;
        }

        @Override
        public boolean hasNext() {
            return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
        }

        @Override
        public Block next() {
            final Block b = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
            if (++this.x >= this.sizeX) {
                this.x = 0;
                if (++this.y >= this.sizeY) {
                    this.y = 0;
                    ++this.z;
                }
            }
            return b;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
