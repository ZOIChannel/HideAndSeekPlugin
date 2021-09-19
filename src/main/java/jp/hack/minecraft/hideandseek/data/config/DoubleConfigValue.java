package jp.hack.minecraft.hideandseek.data.config;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DoubleConfigValue extends ConfigValue<Double> {
    public DoubleConfigValue(Game game, String key) {
        super(game, key);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Messages.error("error.command.noEnoughArgument"));
            return true;
        }
        double value;
        try {
            value = Double.parseDouble(args[1]);
        } catch (final NumberFormatException e) {
            sender.sendMessage(Messages.error("error.command.illegalArgument"));
            return true;
        }
        setData(value);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) return Collections.singletonList(getData().toString());
        return new ArrayList<>();
    }
}