package jp.hack.minecraft.hideandseek.command.hideandseek.admin;

import jp.hack.minecraft.hideandseek.command.AdminCommandMaster;
import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.CommandMaster;
import jp.hack.minecraft.hideandseek.data.GameState;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StartCommand extends AdminCommandMaster {
    public StartCommand(CommandManager manager) {
        super(manager);
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (manager.game.getCurrentState() == GameState.PLAYING) {
            sender.sendMessage(Messages.error("error.game.alreadyStarted"));
            return true;
        }
        if(manager.game.getGamePlayers().size() < 2){
            sender.sendMessage(Messages.error("error.game.notEnoughPlayer"));
            return true;
        }
        manager.game.start();
        return true;
    }

    @Override
    public boolean getExecutable(CommandSender sender, CommandMaster command){
        return super.getExecutable(sender, this) && manager.game.getCurrentState() == GameState.LOBBY;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
