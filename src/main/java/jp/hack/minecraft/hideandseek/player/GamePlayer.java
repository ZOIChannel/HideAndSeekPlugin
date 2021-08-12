package jp.hack.minecraft.hideandseek.player;

import org.bukkit.entity.Player;

import java.util.UUID;

abstract public class GamePlayer {
    private final UUID playerUuid;
    private final Player player;

    public GamePlayer(Player player) {
        this.playerUuid = player.getUniqueId();
        this.player = player;
        this.player.setCollidable(true);
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Player getPlayer() {
        return player;
    }

    public Boolean isHider() {
        return this instanceof Hider;
    }
    public Boolean isSeeker() {
        return this instanceof Seeker;
    }
}
