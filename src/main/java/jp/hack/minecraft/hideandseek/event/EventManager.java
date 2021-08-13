package jp.hack.minecraft.hideandseek.event;

import jp.hack.minecraft.hideandseek.Game;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import jp.hack.minecraft.hideandseek.player.GamePlayer;
import jp.hack.minecraft.hideandseek.player.Hider;

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
        System.out.println(event.getEventName());
        event.setCancelled(true);
    }

    @EventHandler
    public void onHiderFrozen(HiderFrozenEvent event) {
        //System.out.println(event.getEventName());
        Hider hider = event.getHider();
        hider.blockFreeze();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //System.out.println(event.getEventName());
        Player player = event.getPlayer();
//        game.createHider(player); // 一旦コメントアウト
//        game.getEventWatcher().start(); // Game.onEnableへ移動(reloadしたとき反映されないから)

//        player.setInvisible(true); // Hiderへ移動
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        System.out.println(event.getEventName());
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
            return;
        }
        if (!(event.getEntity() instanceof FallingBlock)) return;
        event.setCancelled(true);
        FallingBlock fallingBlock = (FallingBlock) event.getEntity();
        Hider hider = game.findHiderByFallingBlock(fallingBlock);
        if (hider == null) return;
        game.damageHider(hider);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        System.out.println(event.getEventName());
        Player player = event.getPlayer();
        if(!game.getGamePlayers().containsKey(player.getUniqueId())) return;
        GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());

        if (gamePlayer.isHider()) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        Hider hider = game.findHiderByBlock(block);
        if (hider == null) return;
        game.damageHider(hider);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        System.out.println(event.getEventName());

        Player player = event.getPlayer();
        if(!game.getGamePlayers().containsKey(player.getUniqueId())) return;
        GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());
        if (!gamePlayer.isHider()) return;
        Hider hider = (Hider) gamePlayer;

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

