package jp.hack.minecraft.hideandseek.command.hideandseek.player;

import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.CommandMaster;
import jp.hack.minecraft.hideandseek.command.PlayerCommandMaster;
import jp.hack.minecraft.hideandseek.data.GameState;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CancelCommand extends PlayerCommandMaster {
    public CancelCommand(CommandManager manager) {
        super(manager);
    }

    @Override
    public String getName() {
        return "cancel";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (manager.game.getCurrentState() == GameState.PLAYING) {
            sender.sendMessage(Messages.error("game.alreadyStarted"));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.error("command.notPlayer"));
            return true;
        }
        Player player = (Player)sender;
        manager.game.cancel(player);
        return true;
    }

    @Override
    public boolean getExecutable(CommandSender sender, CommandMaster command) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        return super.getExecutable(sender, this)
                && manager.game.getCurrentState() == GameState.LOBBY
                && manager.game.getGamePlayers().containsKey(player.getUniqueId());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
