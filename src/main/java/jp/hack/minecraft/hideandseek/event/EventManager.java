package jp.hack.minecraft.hideandseek.event;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.player.Seeker;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
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
        System.out.println(event.getEventName() +"; Entity:" + event.getRightClicked().getName());

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
        onPlayerClickSign(event);
        onPlayerClickItem(event);
        System.out.println(event.getEventName());

        Player player = event.getPlayer();
        Material havingItemType = player.getInventory().getItemInMainHand().getType();
        if (!game.getMeltType().equals(havingItemType)) return;

        Seeker seeker = game.findSeeker(player.getUniqueId());
        if (seeker == null) return;

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        Hider hider = game.findHiderByBlock(block);
        if (hider == null) return;
        hider.blockMelt();
    }


    private void onPlayerClickSign(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.hasBlock()) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) event.getClickedBlock().getState();
        changeStage(sign);
    }

    private void onPlayerClickItem(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item == null) return;
        if(item.getItemMeta() == null) return;
        if(!item.getItemMeta().hasDisplayName()) return;
        if(!item.getItemMeta().getDisplayName().equals("Select Block")) return;
        game.getBlockGui().openGui(event.getPlayer());
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
    public void onPlayerMove(PlayerMoveEvent event) {
        System.out.println(event.getEventName());

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
}

