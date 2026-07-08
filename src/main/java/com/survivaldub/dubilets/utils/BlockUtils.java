package com.survivaldub.dubilets.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BlockUtils {

    public static BlockFace parseBlockFace(float yaw) {
        double rotation = yaw % 360.0f;
        if (rotation < 0.0) {
            rotation += 360.0;
        }
        if ((0.0 <= rotation && rotation < 45.0) || (315.0 <= rotation && rotation < 360.0)) {
            return BlockFace.NORTH;
        }
        if (45.0 <= rotation && rotation < 135.0) {
            return BlockFace.WEST;
        }
        if (135.0 <= rotation && rotation < 225.0) {
            return BlockFace.SOUTH;
        }
        if (225.0 <= rotation && rotation < 315.0) {
            return BlockFace.EAST;
        }
        return null;
    }

    public static int getRotationsByFacing(float yaw, BlockFace facing) {
        BlockFace playerFacing = BlockUtils.parseBlockFace(yaw);
        if (facing == playerFacing) {
            return 0;
        }
        int facingValue = BlockUtils.parseInt(facing);
        int playerFacingValue = BlockUtils.parseInt(playerFacing);
        if (playerFacingValue == 0) {
            playerFacingValue = 4;
        }
        return playerFacingValue - facingValue;
    }

    private static int parseInt(BlockFace facing) {
        if (facing == BlockFace.WEST) return 1;
        if (facing == BlockFace.SOUTH) return 2;
        if (facing == BlockFace.EAST) return 3;
        return 0;
    }

    public static BlockFace rotateBlockFace(BlockFace blockFace, int rotations) {
        if (rotations == 3 || rotations == -1) {
            if (blockFace == BlockFace.EAST) return BlockFace.SOUTH;
            if (blockFace == BlockFace.SOUTH) return BlockFace.WEST;
            if (blockFace == BlockFace.WEST) return BlockFace.NORTH;
            return BlockFace.EAST;
        }
        if (rotations == 2 || rotations == -2) {
            return blockFace.getOppositeFace();
        }
        if (rotations == 1 || rotations == -3) {
            if (blockFace == BlockFace.EAST) return BlockFace.NORTH;
            if (blockFace == BlockFace.SOUTH) return BlockFace.EAST;
            if (blockFace == BlockFace.WEST) return BlockFace.SOUTH;
            return BlockFace.WEST;
        }
        return blockFace;
    }

    public static Location getRandomSpawnLocation(Location center, double min, double max) {
        double x = MathUtils.getRandomNumber(min, max) + center.getX();
        double z = MathUtils.getRandomNumber(min, max) + center.getZ();
        double y = center.getY() + 10.0;
        for (int i = 10; i > -10 && !center.getWorld().getBlockAt((int) x, (int) y, (int) z).getType().isSolid(); --i) {
            y -= i;
        }
        return new Location(center.getWorld(), x, y, z);
    }

    public static int getDistanceFromGround(Location loc) {
        double y = loc.getBlockY();
        int distance = 0;
        for (double i = y; i >= 0.0; i -= 1.0) {
            loc.setY(i);
            if (loc.getBlock().getType().isSolid()) break;
            ++distance;
        }
        return distance;
    }

    public static String parseString(Location loc) {
        return "[" + loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "]";
    }

    public static void fill(Block block1, Block block2, Material material) {
        BlockUtils.fill(block1.getLocation(), block2.getLocation(), material);
    }

    public static void fill(Location loc1, Location loc2, Material material) {
        int lowestX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int lowestY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int lowestZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int highestX = lowestX == loc1.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int highestY = lowestY == loc1.getBlockY() ? loc2.getBlockY() : loc1.getBlockY();
        int highestZ = lowestZ == loc1.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();
        for (int x = lowestX; x <= highestX; ++x) {
            for (int y = lowestY; y <= highestY; ++y) {
                for (int z = lowestZ; z <= highestZ; ++z) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    block.setType(material);
                }
            }
        }
    }

    public static List<Block> getCube(Location loc1, Location loc2, boolean outlines) {
        LinkedList<Block> blocks = new LinkedList<>();
        int lowestX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int lowestY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int lowestZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int highestX = lowestX == loc1.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int highestY = lowestY == loc1.getBlockY() ? loc2.getBlockY() : loc1.getBlockY();
        int highestZ = lowestZ == loc1.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();
        for (int x = lowestX; x <= highestX; ++x) {
            for (int y = lowestY; y <= highestY; ++y) {
                for (int z = lowestZ; z <= highestZ; ++z) {
                    boolean isLowestX = x == lowestX;
                    boolean isHighestX = x == highestX;
                    boolean isLowestY = y == lowestY;
                    boolean isHighestY = y == highestY;
                    boolean isLowestZ = z == lowestZ;
                    boolean isHighestZ = z == highestZ;
                    if (!outlines && (!isLowestX && !isHighestX && !isLowestY && !isHighestY && !isLowestZ && !isHighestZ)) continue;
                    if (isLowestX || isHighestX || isLowestY || isHighestY || isLowestZ || isHighestZ) {
                        blocks.add(loc1.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getSphere(Location loc, double radius) {
        LinkedList<Block> blocks = new LinkedList<>();
        int bx = loc.getBlockX();
        int by = loc.getBlockY();
        int bz = loc.getBlockZ();
        double rSquared = radius * radius;
        double rSquared2 = (radius - 1.0) * (radius - 1.0);
        for (double x = bx - radius; x <= bx + radius; x += 1.0) {
            for (double y = by - radius; y <= by + radius; y += 1.0) {
                for (double z = bz - radius; z <= bz + radius; z += 1.0) {
                    double distance = (bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y);
                    if (distance < rSquared && distance >= rSquared2) {
                        blocks.add(loc.getWorld().getBlockAt((int) x, (int) y, (int) z));
                    }
                }
            }
        }
        return blocks;
    }

    public static Firework spawnFirework(Location loc, int power, FireworkEffect.Type type) {
        Random random = MathUtils.getRandom();
        return BlockUtils.spawnFirework(loc, power, type, Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
    }

    public static Firework spawnFirework(Location loc, int power, FireworkEffect.Type type, Color color) {
        Random random = MathUtils.getRandom();
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fwm = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
                .flicker(random.nextBoolean())
                .withColor(color)
                .withFade(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .with(type)
                .trail(random.nextBoolean())
                .build();
        fwm.addEffect(effect);
        fwm.setPower(power);
        firework.setFireworkMeta(fwm);
        return firework;
    }

    public static void detonateFirework(Plugin plugin, long delay, final Firework firework) {
        new BukkitRunnable() {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(plugin, delay);
    }

    public static BlockFace getBlockFace(float yaw) {
        double rotation = yaw % 360.0f;
        if (rotation < 0.0) {
            rotation += 360.0;
        }
        if ((0.0 <= rotation && rotation < 45.0) || (315.0 <= rotation && rotation < 360.0)) {
            return BlockFace.SOUTH;
        }
        if (45.0 <= rotation && rotation < 135.0) {
            return BlockFace.WEST;
        }
        if (135.0 <= rotation && rotation < 225.0) {
            return BlockFace.NORTH;
        }
        if (225.0 <= rotation && rotation < 315.0) {
            return BlockFace.EAST;
        }
        return null;
    }
}
