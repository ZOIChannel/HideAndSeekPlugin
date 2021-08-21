package jp.hack.minecraft.hideandseek.player;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.data.EffectType;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

abstract public class GamePlayer {
    private final UUID playerUuid;
    private final Player player;
    private final Map<EffectType, Game.MyRunnable> effectMap = new HashMap<>();

    public GamePlayer(Player player) {
        this.playerUuid = player.getUniqueId();
        this.player = player;
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setCollidable(false);
    }

    public void sendGreenTitle(int fadeIn, int stay, int fadeOut, String code, Object... args) {
        getPlayer().sendTitle(Messages.greenMessage(code, args), "", fadeIn, stay, fadeOut);
    }

    public void sendGreenTitle(int fadeIn, int stay, int fadeOut, String code, String subtitle) {
        getPlayer().sendTitle(Messages.greenMessage(code), subtitle, fadeIn, stay, fadeOut);
    }

    public void sendRedTitle(int fadeIn, int stay, int fadeOut, String code, Object... args) {
        getPlayer().sendTitle(Messages.redMessage(code, args), "", fadeIn, stay, fadeOut);
    }

    public void sendRedTitle(int fadeIn, int stay, int fadeOut, String code, String subtitle) {
        getPlayer().sendTitle(Messages.redMessage(code), subtitle, fadeIn, stay, fadeOut);
    }

    public void sendTitle(int fadeIn, int stay, int fadeOut, String code, Object... args) {
        getPlayer().sendTitle(Messages.message(code, args), "", fadeIn, stay, fadeOut);
    }

    public void sendTitle(int fadeIn, int stay, int fadeOut, String code, String subtitle) {
        getPlayer().sendTitle(Messages.message(code), subtitle, fadeIn, stay, fadeOut);
    }

    public void sendGreenMessage(String code, Object... args) {
        getPlayer().sendMessage(Messages.greenMessage(code, args));
    }

    public void sendRedMessage(String code, Object... args) {
        getPlayer().sendMessage(Messages.redMessage(code, args));
    }

    public void giveEffect(EffectType type) {
        getPlayer().addPotionEffect(new PotionEffect(type.getPotionType(), type.getDuration()*20, type.getLevel()));
    }

    public void clearEffect(EffectType type) {
        getPlayer().removePotionEffect(type.getPotionType());
    }

    public void equipItem() {}

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Player getPlayer() {
        return player;
    }

    public Map<EffectType, Game.MyRunnable> getEffectMap() {
        return effectMap;
    }

    public Location getLocation() {
        return getPlayer().getLocation();
    }

    public Boolean isHider() {
        return this instanceof Hider;
    }
    public Boolean isSeeker() {
        return this instanceof Seeker;
    }

    public Boolean isSameRole(Role role) {
        if (isHider() && role == Role.HIDER) {
            return true;
        } else return isSeeker() && role == Role.SEEKER;
    }
}
