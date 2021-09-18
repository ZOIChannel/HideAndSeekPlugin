package jp.hack.minecraft.hideandseek.data.config;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.data.PluginGameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class EnumConfigValue<E extends Enum<E>> extends CastConfigValue<E, String> {

    private final Class<E> enumType;

    public EnumConfigValue(Game game, String key, Class<E> enumType) {
        super(game, key, Enum::name, s -> E.valueOf(enumType, s));
        this.enumType = enumType;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2)
            return EnumSet.allOf(PluginGameMode.class).stream()
                    .map(PluginGameMode::name).filter(name -> name.startsWith(args[1].toUpperCase()))
                    .collect(Collectors.toList());
        return new ArrayList<>();
    }
}