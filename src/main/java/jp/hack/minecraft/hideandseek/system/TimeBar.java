package jp.hack.minecraft.hideandseek.system;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;


public class TimeBar {
    private final String title = ChatColor.RED + "残り時間";
    private final BarColor color = BarColor.RED;
    private final BarStyle style = BarStyle.SOLID;
    private final BossBar bossBar;

    public TimeBar() {
        bossBar = Bukkit.createBossBar(title, color, style);
        setVisible(false);
    }

    public void addPlayer(Player player) {
        bossBar.addPlayer(player);
    }

    public Boolean isVisible() {
        return bossBar.isVisible();
    }

    public void setVisible(Boolean visible) {
        bossBar.setVisible(visible);
    }

    public void setProgress(float percent) {
        if (percent < 0.0) return;
        if (percent > 1.0) return;
        bossBar.setProgress(percent);
    }
}
