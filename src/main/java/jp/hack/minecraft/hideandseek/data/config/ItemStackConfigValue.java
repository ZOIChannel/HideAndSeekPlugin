package jp.hack.minecraft.hideandseek.data.config;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemStackConfigValue extends ConfigValue<ItemStack> {
    public ItemStackConfigValue(Game game, String key) {
        super(game, key);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Messages.error("command.notEnoughArgument"));
            return true;
        }
        Material material = Material.matchMaterial(args[1]);
        if (material == null) {
            sender.sendMessage(Messages.error("command.illegalArgument"));
            return true;
        }
        ItemStack value = getData();
        value.setType(material);
        setData(value);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2)
            return Arrays.stream(Material.values()).map(material -> material.getKey().toString()).collect(Collectors.toList());
        return new ArrayList<>();
    }
}