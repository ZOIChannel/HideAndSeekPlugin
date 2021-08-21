package jp.hack.minecraft.hideandseek.data;

import org.bukkit.potion.PotionEffectType;

public enum EffectType {
    UP_SPEED(PotionEffectType.SPEED, 30, 5, 1),
    HI_LIGHT(null, 20, 10);

    private PotionEffectType potionType;
    private int coolTime;
    private int duration;
    private int level;

    public PotionEffectType getPotionType() {
        return potionType;
    }

    public int getCoolTime() {
        return coolTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getLevel() {
        return level;
    }

    private EffectType(PotionEffectType type, int coolTime, int duration) {
        this(type, coolTime, duration, 1);
    }

    private EffectType(PotionEffectType type, int coolTime, int duration, int level) {
        this.potionType = type;
        this.coolTime = coolTime;
        this.duration = duration;
        this.level = level;
    }
}
