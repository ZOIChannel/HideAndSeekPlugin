package test.ryokno.player;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
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
        destroyFallingBlock();
        placeBlock();
        this.isFrozen = true;
    }

    public void blockMelt() {
        if (!this.isFrozen) return;
        removeBlock();
        spawnFallingBlock();
        this.isFrozen = false;
    }

    public void setFBVelocity(Location from, Location to) {
        if (this.fallingBlock == null) return;
        Vector vec = getPlayer().getVelocity();
        vec.setX(to.getX() - from.getX());
        vec.setZ(to.getZ() - from.getZ());
        this.fallingBlock.setVelocity(vec);
    }

    public void spawnFallingBlock() {
        if (this.fallingBlock != null && !this.fallingBlock.isDead()) return;
        MaterialData materialData = new MaterialData(material);
        this.fallingBlock = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation(), materialData);
        this.fallingBlock.setDropItem(false);
        this.fallingBlock.setHurtEntities(false);
        this.fallingBlock.setGravity(false);
        this.fallingBlock.setInvulnerable(true);
        this.fallingBlock.setPersistent(true);
        Vector vec = new Vector(0.0, 0.001, 0.0);
        this.fallingBlock.setVelocity(vec);
    }

    private void destroyFallingBlock() {
        if (this.fallingBlock == null) return;
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
