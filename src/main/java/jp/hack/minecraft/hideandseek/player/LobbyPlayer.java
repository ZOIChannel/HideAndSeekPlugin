package jp.hack.minecraft.hideandseek.player;

import jp.hack.minecraft.hideandseek.Game;
import org.bukkit.entity.Player;

public class LobbyPlayer extends GamePlayer{
    public LobbyPlayer(Player player) {
        super(player);
    }
    public void createHider(Game game){
        game.getGamePlayers().put(this.getPlayerUuid(), new Hider(game, this.getPlayer()));
    }
    public void createSeeker(Game game){
        game.getGamePlayers().put(this.getPlayerUuid(), new Seeker(this.getPlayer()));
    }
}
