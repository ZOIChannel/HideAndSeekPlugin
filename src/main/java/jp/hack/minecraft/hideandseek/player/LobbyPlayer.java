package jp.hack.minecraft.hideandseek.player;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class LobbyPlayer extends GamePlayer{
    public LobbyPlayer(Player player) {
        super(player);
    }
    public void createHider(Map<UUID, GamePlayer> gamePlayers){
        gamePlayers.put(this.getPlayerUuid(), new Hider(this.getPlayer()));
    }
    public void createSeeker(Map<UUID, GamePlayer> gamePlayers){
        gamePlayers.put(this.getPlayerUuid(), new Seeker(this.getPlayer()));
    }
}
