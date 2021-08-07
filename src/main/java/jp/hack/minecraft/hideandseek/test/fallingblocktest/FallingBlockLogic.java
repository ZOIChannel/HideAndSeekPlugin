package jp.hack.minecraft.hideandseek.test.fallingblocktest;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class FallingBlockLogic {
    private final Player player;
    private FallingBlock fallingBlock;
    private Location cacheLocation;

    public FallingBlockLogic(Player player) {
        this.player = player;
        fallingBlock = createBlock();
        cacheLocation = player.getLocation();
    }

    public FallingBlock createBlock() {
        Location location = player.getLocation();
        Bukkit.getLogger().info(location.toString());
        FallingBlock block = location.getWorld().spawnFallingBlock(location, Bukkit.createBlockData(Material.STONE));
        block.setHurtEntities(false);
        block.setGravity(false);
        block.setDropItem(false);
        Bukkit.getLogger().info("fgd");
        return block;
    }

    public void onMove() {
        Location location = player.getLocation();
        if (cacheLocation.equals(fallingBlock.getLocation())) {
//            location.getBlock()
            fallingBlock = createBlock();
        }
        cacheLocation = fallingBlock.getLocation();
        Bukkit.getLogger().info(fallingBlock.getLocation().toString());
        location.setY(location.getY() + .01);
        fallingBlock.teleport(location);
        Vector velocity = player.getVelocity();
        velocity.setY(velocity.getY() > 0 ? velocity.getY() : 0);
        fallingBlock.setVelocity(velocity);
        fallingBlock.setFallDistance(0);
//        fallingBlock.setRotation(p);
    }

    public BlockData getBlockData() {
        return fallingBlock.getBlockData();
    }
}
