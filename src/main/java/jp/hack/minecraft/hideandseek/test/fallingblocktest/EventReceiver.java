package jp.hack.minecraft.hideandseek.test.fallingblocktest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EventReceiver implements Listener {
    private final JavaPlugin plugin;
    private final HashMap<UUID, FallingBlockLogic> logics = new HashMap<>();

    public EventReceiver(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockEvent(BlockPhysicsEvent event) {
//        Bukkit.getLogger().info(event.getEventName());
        if (logics.values().stream().anyMatch(obj -> event.getSourceBlock().getBlockData().matches(obj.getBlockData()))) {
            Bukkit.getLogger().info("canselled");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        logics.put(event.getPlayer().getUniqueId(), new FallingBlockLogic(event.getPlayer()));
        Bukkit.getLogger().info("areughbeiaorhg");
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        logics.remove(event.getPlayer().getUniqueId());
        Bukkit.getLogger().info("uhdis");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        FallingBlockLogic logic = logics.get(event.getPlayer().getUniqueId());
        if (logic == null) return;
        logic.onMove();
    }

    @EventHandler
    public void onMove(EntityChangeBlockEvent event) {
        Bukkit.getLogger().info("event!!!");
    }

    @EventHandler
    public void onFade(BlockDamageEvent event) {
        Bukkit.getLogger().info("---FadeEvent---");
    }

}
