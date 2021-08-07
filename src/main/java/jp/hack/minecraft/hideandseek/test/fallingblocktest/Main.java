package jp.hack.minecraft.hideandseek.test.fallingblocktest;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new EventReceiver(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
