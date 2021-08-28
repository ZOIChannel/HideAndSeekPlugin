package jp.hack.minecraft.hideandseek.player;

import jp.hack.minecraft.hideandseek.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LobbyPlayer extends GamePlayer{
    public LobbyPlayer(Player player) {
        super(player);
        getPlayer().setPlayerListName(ChatColor.GREEN + getPlayer().getName() + ChatColor.RESET);
    }
}
