package jp.hack.minecraft.hideandseek.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.Objects;

public class StageData {
    private final Location center;
    private final double radius;
    private final WorldBorder stageBorder;

    public StageData(Location center, double radius){
        this.center = center;
        this.radius = radius;
        stageBorder = Objects.requireNonNull(center.getWorld()).getWorldBorder();
    }

    public void createBorder() {
        stageBorder.setCenter(center);
        stageBorder.setSize(radius);
        stageBorder.setDamageAmount(0);
        stageBorder.setDamageBuffer(0);
        stageBorder.setWarningTime(0);
        stageBorder.setWarningDistance(0);
    }
    public void deleteBorder() {
        stageBorder.reset();
    }
}
