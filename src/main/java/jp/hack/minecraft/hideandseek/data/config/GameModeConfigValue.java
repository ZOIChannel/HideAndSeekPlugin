package jp.hack.minecraft.hideandseek.data.config;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.data.PluginGameMode;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class GameModeConfigValue extends EnumConfigValue<PluginGameMode> {

    public GameModeConfigValue(Game game, String key) {
        super(game, key, PluginGameMode.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Messages.error("error.command.noEnoughArgument"));
        }
        String valueName = args[1].toUpperCase();
        if (EnumSet.allOf(PluginGameMode.class).stream().map(PluginGameMode::name).collect(Collectors.toList()).contains(valueName)) {
            PluginGameMode value = PluginGameMode.valueOf(PluginGameMode.class, valueName);
            setData(value);
            sender.sendMessage(Messages.greenMessage("gameMode.changed", value.name()));
        } else {
            sender.sendMessage(Messages.error("error.config.noConfig", valueName));
        }
        return true;
    }
}
