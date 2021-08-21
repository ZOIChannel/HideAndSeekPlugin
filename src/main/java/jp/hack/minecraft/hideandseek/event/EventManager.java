package jp.hack.minecraft.hideandseek.event;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.data.EffectType;
import jp.hack.minecraft.hideandseek.data.GameState;
import jp.hack.minecraft.hideandseek.player.GamePlayer;
import jp.hack.minecraft.hideandseek.player.Role;
import jp.hack.minecraft.hideandseek.player.Seeker;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import jp.hack.minecraft.hideandseek.player.Hider;
import org.bukkit.inventory.ItemStack;

public class EventManager implements Listener {
    private final Game game;

    public EventManager(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onEventWatcher(EventWatcherEvent event) {
        //System.out.println(event.getEventName());
        game.getGamePlayers().values().forEach(gamePlayer -> {
            if (!gamePlayer.isHider()) return;
            Hider hider = (Hider) gamePlayer;
            teleportFBToHider(hider);
        });
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (game.getGamePlayer(player.getUniqueId()) == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (game.getGamePlayer(player.getUniqueId()) == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        System.out.println(event.getEventName());
        if (event.getEntity() instanceof Player) event.setCancelled(true);
    }

    @EventHandler
    public void onHiderFrozen(HiderFrozenEvent event) {
        //System.out.println(event.getEventName());
        Hider hider = event.getHider();

        if (hider.isDead()) return;

        hider.blockFreeze();
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        System.out.println(event.getEventName() + "; Entity:" + event.getRightClicked().getName());

        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();
        Material havingItemType = player.getInventory().getItemInMainHand().getType();
        if (!game.getCaptureType().equals(havingItemType)) return;

        Seeker seeker = game.findSeeker(player.getUniqueId());
        if (seeker == null) return;

        if (!(rightClicked instanceof FallingBlock || rightClicked instanceof Player)) return;

        Hider hider;

        if (rightClicked instanceof FallingBlock) {
            hider = game.findHiderByFallingBlock((FallingBlock) rightClicked);
        } else {
            hider = game.findHider(rightClicked.getUniqueId());
        }
        if (hider == null) return;
        game.damageHider(hider);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        System.out.println(event.getEventName());
        onPlayerClickSign(event);
        onPlayerClickItem(event);

        Player player = event.getPlayer();
        Material havingItemType = player.getInventory().getItemInMainHand().getType();
        if (havingItemType == game.getMeltType()) {

            Seeker seeker = game.findSeeker(player.getUniqueId());
            if (seeker == null) return;

            if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
            Block block = event.getClickedBlock();

            Hider hider = game.findHiderByBlock(block);
            if (hider == null) {
                Location blockLoc = event.getClickedBlock().getLocation();
                seeker.knock(blockLoc);
                return;
            }
            seeker.discover();

            hider.found();
            hider.blockMelt();

        } else if (havingItemType == game.getSpeedType()) {

            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            Hider hider = game.findHider(player.getUniqueId());
            if (hider == null) return;
            hider.upSpeed(game.givePlayerEffect(hider, EffectType.UP_SPEED));

        }
    }


    private void onPlayerClickSign(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.hasBlock()) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) event.getClickedBlock().getState();
        changeStage(sign);
    }

    private void onPlayerClickItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!game.getGamePlayers().containsKey(player.getUniqueId())) return;
        GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());
        if (!gamePlayer.isHider()) return;
        Hider hider = (Hider) gamePlayer;
        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        if (!item.getItemMeta().hasDisplayName()) return;
        if (!item.getItemMeta().getDisplayName().equals("ブロックを選択")) return;
        hider.getBlockGui().openGui(event.getPlayer());
    }

    private void changeStage(Sign sign) {
        if (!sign.getLines()[0].equals("[StageSelector]")) return;
        game.selectNextStage();
        System.out.println("event.getEventName()");
        if (game.getCurrentStage() == null) sign.setLine(2, ">> Stage not set <<");
        sign.setLine(2, ">> " + game.getCurrentStage().getName() + " <<");
        sign.update();
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Hider hider = game.findHider(player.getUniqueId());
        if (hider == null) return;
        if (hider.isDead()) return;

        hider.resetFBVelocity();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;
        if (player.getGameMode() == GameMode.SPECTATOR)
            if (from.getY() != to.getY()) {
                event.setCancelled(true);
                return;
            }

        if (game.isSameLocation(from, to)) return;
        hider.spawnFallingBlock();
        hider.setFBVelocity(from, to);
        teleportFBToHider(hider);

        if (game.isSameBlockLocation(from, to)) return;
        hider.blockMelt();
    }

    private void teleportFBToHider(Hider hider) {
        if (!hider.isFBLived()) return;
        if (game.isDistant(hider.getLocation(), hider.getFallingBlock().getLocation())) {
            hider.teleportFBToHider();
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (game.getCurrentState() != GameState.PLAYING) return;
        GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());
        if (gamePlayer == null) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) return; // killコマンドは多分使える
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (game.getCurrentState() != GameState.PLAYING) return;
        GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());
        if (gamePlayer == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (game.getCurrentState() != GameState.PLAYING) return;
        GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());
        if (gamePlayer == null) return;
        if (!gamePlayer.isSeeker()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = ChatColor.AQUA +
                "Hide And Seek\n" +
                "制作: ZOI, Ryokno\n" +
                "ブロックになってかくれんぼをするミニゲームです。\n" +
                ChatColor.RESET;
        event.getPlayer().sendMessage(message);
    }
}

