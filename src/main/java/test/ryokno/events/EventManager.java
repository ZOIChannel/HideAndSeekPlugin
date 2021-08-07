package test.ryokno.events;

import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import test.ryokno.Game;
import test.ryokno.player.GamePlayer;
import test.ryokno.player.Hider;

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
            if (hider.isFrozen()) return;
            if (! game.isSameBlockLocation(hider.getLocation(), hider.getFallingBlock().getLocation())) {
                hider.getFallingBlock().teleport(hider.getLocation());
                hider.getFallingBlock().setVelocity(new Vector(0, 0, 0));
            }
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
        Hider hider = game.createHider(player);

        player.setInvisible(true);
        player.setCollidable(true);
        hider.spawnFallingBlock();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        System.out.println(event.getEventName());
        game.getGameWatcher().start();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        //System.out.println(event.getEventName());
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getX() == to.getX() && from.getZ() == to.getZ() && from.getY() == to.getY()) return;
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());
        if (!gamePlayer.isHider()) return;
        Hider hider = (Hider) gamePlayer;
        hider.setFBVelocity(from, to);
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ() && from.getBlockY() == to.getBlockY()) return;
        if (hider.isFrozen()) hider.blockMelt();
        if (hider.getFallingBlock().isDead()) hider.spawnFallingBlock();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) event.setCancelled(true);
        if (event.getEntity() instanceof FallingBlock) event.setCancelled(true);
    }
}

