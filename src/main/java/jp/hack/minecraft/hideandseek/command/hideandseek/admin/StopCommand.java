package jp.hack.minecraft.hideandseek.command.hideandseek.admin;

import jp.hack.minecraft.hideandseek.command.AdminCommandMaster;
import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.data.GameState;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StopCommand extends AdminCommandMaster {
    public StopCommand(CommandManager manager) {
        super(manager);
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (manager.game.getCurrentState() != GameState.PLAYING) {
            sender.sendMessage(Messages.error("game.notStarted"));
            return true;
        }
        manager.game.stop();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
