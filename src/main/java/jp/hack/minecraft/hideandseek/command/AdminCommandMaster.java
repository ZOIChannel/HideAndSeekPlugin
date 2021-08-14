package jp.hack.minecraft.hideandseek.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class AdminCommandMaster extends CommandMaster{

    public AdminCommandMaster(CommandManager manager) {
        super(manager);
    }

    @Override
    public abstract String getName();

    @Override
    public String getPermission() {
        return "hideandseek.admin";
    }

    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    @Override
    public abstract List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);
}
