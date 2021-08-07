package jp.hack.minecraft.hideandseek.command.hideandseek.admin;

import jp.hack.minecraft.hideandseek.command.AdminCommandMaster;
import jp.hack.minecraft.hideandseek.command.CommandManager;
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
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
