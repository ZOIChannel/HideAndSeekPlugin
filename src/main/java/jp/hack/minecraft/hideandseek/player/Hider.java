package jp.hack.minecraft.hideandseek.player;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.data.BlockGui;
import jp.hack.minecraft.hideandseek.data.EffectType;
import jp.hack.minecraft.hideandseek.data.PluginGameMode;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class Hider extends GamePlayer {
    private Material material;
    private Block block;
    private FallingBlock fallingBlock;

    private Long freezeTicks = 0L;
    private Location prevLoc;

    private Boolean isDead = false;
    private Boolean isFrozen = false;
    private Boolean isCalledEvent = false;

    private boolean isOpeningGui = false;

    public Hider(Player player) {
        super(player);
        player.setInvisible(true);
        player.setPlayerListName(ChatColor.BLUE + player.getName() + ChatColor.RESET);
        equipBlockList();

        this.material = Material.MELON;
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

    public boolean isOpeningGui() {
        return isOpeningGui;
    }

    public void setOpeningGui(boolean openingGui) {
        isOpeningGui = openingGui;
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

    public Location getPrevLoc() {
        return prevLoc;
    }

    public void setPrevLoc(Location prevLoc) {
        this.prevLoc = prevLoc;
    }

    public void equipBlockList() {
        Inventory inventory = getPlayer().getInventory();
        inventory.clear();

        {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;

            final String NAME = ChatColor.YELLOW + "ブロックを選択";
            meta.setDisplayName(NAME);
            final List<String> LORE = Arrays.asList(ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "右" + ChatColor.RESET.toString() + ChatColor.WHITE.toString() + "クリックでブロックを選択");
            meta.setLore(LORE);

            item.setItemMeta(meta);
            inventory.addItem(item);
        }
    }

    public void respawnFB() {
        setFreezeTicks(0L);
        blockMelt();
        destroyFallingBlock();
        spawnFallingBlock();
    }

    public void upSpeed(Boolean boo) {
        if (boo) {
            sendGreenMessage("game.you.upSpeed");
            getPlayer().playSound(getLocation(), Sound.ENTITY_HORSE_STEP, 1.0F, 1.0F);
        } else {
            sendRedMessage("game.you.coolTime");
        }
    }

    public void found() {
        System.out.println("----- event:003 -----");
        sendRedTitle(5, 10, 5, "game.you.found", Messages.message("game.you.runaway"));
        getPlayer().playSound(getLocation(), Sound.ENTITY_GHAST_WARN, SoundCategory.MASTER, 1.0F, 1.0F);
    }

    public void destroy() {
        removeBlock();
        destroyFallingBlock();
    }

    public void damage(PluginGameMode currentGameMode) {
        if (isDead) return;
        if (currentGameMode == PluginGameMode.NORMAL) {
            getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        destroy();
        getPlayer().setPlayerListName(ChatColor.BLACK + getPlayer().getName() + ChatColor.RESET);
        getPlayer().sendMessage(Messages.redMessage("game.you.found"));
        getPlayer().playSound(getLocation(), Sound.ENTITY_PLAYER_HURT, 1F, 1F);
        Firework firework = getFirework();
        firework.detonate();
        isDead = true;
    }

    public void blockFreeze() {
        if (this.isFrozen || this.isDead) return;
        if (getPlayer().getLocation().getBlock().getType() != Material.AIR) {
            sendRedMessage("game.you.cannotHideHere");
            return;
        }
        this.isFrozen = true;
        Player player = getPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        destroyFallingBlock();
        placeBlock();
    }

    public void blockMelt() {
        System.out.println("----- event:004 -----");
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
        if (!isFBLived()) return;
        this.fallingBlock.remove();

        this.fallingBlock = null;
    }

    private void placeBlock() {
        if (this.block != null || this.isDead) return;
        this.block = getPlayer().getLocation().getBlock();
        this.block.setType(material);
    }

    private void removeBlock() {
        if (this.block == null) return;
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

    public Seeker createSeeker() {
        return new Seeker(getPlayer());
    }
}
