package jp.hack.minecraft.hideandseek.command.hideandseek.player;

import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.PlayerCommandMaster;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class JoinCommand extends PlayerCommandMaster {
    public JoinCommand(CommandManager manager) {
        super(manager);
    }

    @Override
    public String getName() {
        return "join";
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
