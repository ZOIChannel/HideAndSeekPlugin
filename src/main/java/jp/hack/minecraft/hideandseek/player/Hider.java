package jp.hack.minecraft.hideandseek.player;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Random;

public class Hider extends GamePlayer {
    private Material material;
    private Block block;
    private FallingBlock fallingBlock;

    private Long freezeTicks = 0L;
    private Location prevLoc;

    private Boolean isDead = false;
    private Boolean isFrozen = false;
    private Boolean isCalledEvent = false;

    public Hider(Player player) {
        super(player);
        this.material = Material.ACACIA_LOG;
        player.setInvisible(true);
        equipItem();
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

    public Boolean isDead() {
        return isDead;
    }

    public Boolean isFrozen() {
        return isFrozen;
    }

    public Boolean isCalledEvent() {
        return isCalledEvent;
    }

    public void setCalledEvent(Boolean calledEvent) {
        isCalledEvent = calledEvent;
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

    @Override
    public void equipItem() {
        Inventory inventory = getPlayer().getInventory();
    }

    public void damage(int damage) {
        if (isDead) return;
        getPlayer().setGameMode(GameMode.SPECTATOR);
        removeBlock();
        Firework firework = getFirework();
        firework.detonate();
        isDead = true;
        /*
        blockMelt();
        setFreezeTicks(0L);
        getPlayer().damage(damage);
        getPlayer().playSound(getLocation(), Sound.ENTITY_PLAYER_HURT, 1F, 1F);
        */
    }

    public void blockFreeze() {
        if (this.isFrozen || this.isDead) return;
        this.isFrozen = true;
        Player player = getPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        destroyFallingBlock();
        placeBlock();
    }

    public void blockMelt() {
        if (!this.isFrozen || this.isDead) return;
        this.isFrozen = false;
        Player player = getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        removeBlock();
        spawnFallingBlock();
    }

    public void teleportFBToHider() {
        if (!isFBLived() || this.isFrozen || this.isDead) return;
        this.fallingBlock.teleport(getLocation().add(0d, 0.0001, 0d));
        resetFBVelocity();
    }

    public void reduceFBVelocity() {
        if (!isFBLived() || this.isFrozen || this.isDead) return;
        this.fallingBlock.setVelocity(this.getFallingBlock().getVelocity().multiply(0.8));
    }

    public void resetFBVelocity() {
        if (!isFBLived() || this.isFrozen || this.isDead) return;
        this.fallingBlock.setVelocity(new Vector(0d, 0d, 0d));
    }

    public void setFBVelocity(Location from, Location to) {
        if (!isFBLived() || this.isFrozen || this.isDead) return;
        Vector vec = getPlayer().getVelocity();
        if (((LivingEntity) getPlayer()).isOnGround()) vec.setY(0d);
        vec.setX(to.getX() - from.getX());
        vec.setZ(to.getZ() - from.getZ());
        this.fallingBlock.setVelocity(vec);
    }

    public void spawnFallingBlock() {
        if (isFBLived() || this.isFrozen || this.isDead) return;
        MaterialData materialData = new MaterialData(material);
        this.fallingBlock = getPlayer().getWorld().spawnFallingBlock(getPlayer().getLocation().add(0d, 0.0001, 0d), materialData);
        this.fallingBlock.setDropItem(false);
        this.fallingBlock.setHurtEntities(false);
        this.fallingBlock.setGravity(false);
        this.fallingBlock.setPersistent(true);
        this.fallingBlock.setVelocity(new Vector());
    }

    private void destroyFallingBlock() {
        if (!isFBLived() || !this.isFrozen || this.isDead) return;
        this.fallingBlock.remove();
        this.fallingBlock = null;
    }

    private void placeBlock() {
        if (this.block != null || this.isDead) return;
        this.block = getPlayer().getLocation().getBlock();
        this.block.setType(material);
    }

    private void removeBlock() {
        if (this.block == null || this.isDead) return;
        this.block.setType(Material.AIR);
        this.block = null;
    }

    private Firework getFirework() {
        Firework firework = (Firework) getPlayer().getWorld().spawnEntity(getLocation(), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.STAR)
                .withFlicker()
                .withColor(Color.RED, Color.YELLOW)
                .build()
        );
        meta.setPower(1);
        firework.setFireworkMeta(meta);
        return firework;
    }
}
