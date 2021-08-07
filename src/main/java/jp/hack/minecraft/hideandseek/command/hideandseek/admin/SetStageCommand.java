package jp.hack.minecraft.hideandseek.command.hideandseek.admin;

import jp.hack.minecraft.hideandseek.command.AdminCommandMaster;
import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetStageCommand extends AdminCommandMaster {
    public SetStageCommand(CommandManager manager) {
        super(manager);
    }

    @Override
    public String getName() {
        return "setStage";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(!(sender instanceof Player)) sender.sendMessage(Messages.error("command.notPlayer"));
        manager.game.getConfigLoader().setData("game.location.stage", sender);
        return null;
    }
}