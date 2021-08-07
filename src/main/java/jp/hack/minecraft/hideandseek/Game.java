package jp.hack.minecraft.hideandseek;

import jp.hack.minecraft.hideandseek.event.EventManager;
import jp.hack.minecraft.hideandseek.event.EventWatcher;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import jp.hack.minecraft.hideandseek.player.GamePlayer;
import jp.hack.minecraft.hideandseek.player.Hider;
import jp.hack.minecraft.hideandseek.player.Seeker;

import java.util.*;

public final class Game extends JavaPlugin {
    private static Economy econ = null;
    private final EventWatcher eventWatcher = new EventWatcher(this);
    private final EventManager eventManager = new EventManager(this);
    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();

    public static Economy getEconomy() {
        return econ;
    }

    public Map<UUID, GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public GamePlayer getGamePlayer(UUID uuid) {
        return gamePlayers.get(uuid);
    }

    public EventWatcher getGameWatcher() {
        return eventWatcher;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public Seeker createSeeker(Player player) {
        Seeker seeker = new Seeker(player);
        gamePlayers.put(seeker.getPlayerUuid(), seeker);
        return seeker;
    }

    public Hider createHider(Player player) {
        Hider hider = new Hider(player);
        gamePlayers.put(hider.getPlayerUuid(), hider);
        return hider;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Boolean isSameBlockLocation(Location loc1, Location loc2) {
        return (loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ());
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        super.onEnable();
        getServer().getPluginManager().registerEvents(eventManager, this);

        if (!setupEconomy() ) {
            getServer().getLogger().info("Vault plugin is not found.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        super.onDisable();
    }
}