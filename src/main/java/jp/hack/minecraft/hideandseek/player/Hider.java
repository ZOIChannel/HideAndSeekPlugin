package jp.hack.minecraft.hideandseek.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class Hider extends GamePlayer {
    private Material material;
    private Block block;
    private FallingBlock fallingBlock;
    private Boolean isFrozen = false;
    private Long freezeTicks = 0L;
    private Location prevLoc;

    public Hider(Player player) {
        super(player);
        this.material = Material.ACACIA_LOG;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Block getBlock() {
        return block;
    }

    public org.bukkit.entity.FallingBlock getFallingBlock() {
        return fallingBlock;
    }

    public Boolean isFrozen() {
        return isFrozen;
    }

    public Boolean isFBLived() {
        if (this.fallingBlock == null) return false;
        return !this.fallingBlock.isDead();
    }

    public Long getFreezeTicks() {
        return freezeTicks;
    }

    public void setFreezeTicks(Long tick) {
        this.freezeTicks = tick;
    }

    public void addFreezeTick(Long increase) {
        this.freezeTicks += increase;
    }

    public Location getLocation() {
        return getPlayer().getLocation();
    }

    public Location getPrevLoc() {
        return prevLoc;
    }

    public void setPrevLoc(Location prevLoc) {
        this.prevLoc = prevLoc;
    }


    public void blockFreeze() {
        if (this.isFrozen) return;
        this.isFrozen = true;
        Player player = getPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        destroyFallingBlock();
        placeBlock();
    }

    public void blockMelt() {
        if (!this.isFrozen) return;
        this.isFrozen = false;
        Player player = getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        removeBlock();
        spawnFallingBlock();
    }

    public void teleportFBToHider() {
        if (!isFBLived() || this.isFrozen) return;
        this.fallingBlock.teleport(getLocation().add(0d, 0.05, 0d));
        resetFBVelocity();
    }

    public void reduceFBVelocity() {
        if (!isFBLived() || this.isFrozen) return;
        this.fallingBlock.setVelocity(this.getFallingBlock().getVelocity().multiply(0.8));
    }

    public void resetFBVelocity() {
        if (!isFBLived() || this.isFrozen) return;
        this.fallingBlock.setVelocity(new Vector(0d, 0d, 0d));
    }

    public void setFBVelocity(Location from, Location to) {
        if (!isFBLived() || this.isFrozen) return;
        Vector vec = getPlayer().getVelocity();
        if (((LivingEntity) getPlayer()).isOnGround()) vec.setY(0d);
        vec.setX(to.getX() - from.getX());
        vec.setZ(to.getZ() - from.getZ());
        this.fallingBlock.setVelocity(vec);
    }

    public void spawnFallingBlock() {
        if (isFBLived() || this.isFrozen) return;
        MaterialData materialData = new MaterialData(material);
        this.fallingBlock = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0d, 0.05, 0d), materialData);
        this.fallingBlock.setDropItem(false);
        this.fallingBlock.setHurtEntities(false);
        this.fallingBlock.setGravity(false);
        this.fallingBlock.setInvulnerable(true);
        this.fallingBlock.setPersistent(true);
        this.fallingBlock.setVelocity(new Vector());
    }

    private void destroyFallingBlock() {
        if (!isFBLived() || !this.isFrozen) return;
        this.fallingBlock.remove();
        this.fallingBlock = null;
    }

    private void placeBlock() {
        if (this.block != null) return;
        this.block = getPlayer().getLocation().getBlock();
        this.block.setType(material);
    }

    private void removeBlock() {
        if (this.block == null) return;
        this.block.setType(Material.AIR);
        this.block = null;
    }
}
