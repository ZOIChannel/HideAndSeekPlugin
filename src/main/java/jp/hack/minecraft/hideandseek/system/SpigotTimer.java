package jp.hack.minecraft.hideandseek.system;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SpigotTimer {
    private JavaPlugin plugin;
    private Listener listener;
    private BukkitTask task;

    public interface Listener {
        void performed();

    }

    public SpigotTimer(JavaPlugin plugin, Listener listener) {
        this.plugin = plugin;
        this.listener = listener;
    }

    public void start(int delay) {
        if (task == null) {
            //20 ticks = 1 second.
            int delayTick = delay*20;
            task = Bukkit.getScheduler().runTaskLater(plugin, () -> listener.performed(), delayTick);
        }
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
