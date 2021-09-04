package jp.hack.minecraft.hideandseek.data.config;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.data.StageData;
import jp.hack.minecraft.hideandseek.data.StageType;
import jp.hack.minecraft.hideandseek.data.UsableBlock;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static jp.hack.minecraft.hideandseek.data.StageType.*;

public class UsableBlockConfigValue extends ConfigValue<List<UsableBlock>> {
    public UsableBlockConfigValue(Game game, String key) {
        super(game, key);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.error("command.notPlayer"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(Messages.error("command.illegalArgument"));
            return true;
        }
        Player player = (Player) sender;
        switch (args[1]) {
            case "add":
                addBlock(player, args);
                break;
            case "setPrice":
                setBlockPrice(player, args);
                break;
            case "delete":
                deleteBlock(player, args);
                break;
            case "list":
                listBlock(player, args);
                break;
            default:
                player.sendMessage(Messages.error("command.illegalArgument"));
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 2) return new ArrayList<>();
        if (args.length == 2) {
            return new ArrayList<>(Arrays.asList("add", "setPrice", "delete", "list")).stream()
                    .filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        }
        if (args[1].equals("add") && args.length == 3) return Arrays.stream(Material.values()).map(material -> material.getKey().toString()).collect(Collectors.toList());
        if (args[1].equals("add") && args.length == 4) return Collections.singletonList("(price)");
        if ((args[1].equals("setPrice") || args[1].equals("delete")) && args.length == 3)
            return getData().stream().map(usableBlock -> usableBlock.getMaterial().getKey().toString()).collect(Collectors.toList());
        if (args[1].equals("setPrice") && args.length == 4) {
            Material material = Material.matchMaterial(args[2]);
            if (material != null) {
                for (UsableBlock usableBlock : getData()) {
                    if (usableBlock.getMaterial() == material) {
                        return Collections.singletonList(usableBlock.getPrice().toString());
                    }
                }
            }
            return Collections.singletonList("(price)");
        }
        return new ArrayList<>();
    }


    private void addBlock(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        Material material = Material.matchMaterial(args[2]);
        if (material == null) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        int price;
        try {
            price = Integer.parseInt(args[3]);
        } catch (final NumberFormatException e) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        for (UsableBlock usableBlock : getData()) {
            if (usableBlock.getMaterial() == material) {
                player.sendMessage(Messages.error("config.usableBlock.alreadyExist"));
                return;
            }
        }
        List<UsableBlock> blockList = getData();
        blockList.add(new UsableBlock(material, price));
        setData(blockList);
    }

    private void setBlockPrice(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        Material material = Material.matchMaterial(args[2]);
        if (material == null) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        int price;
        try {
            price = Integer.parseInt(args[3]);
        } catch (final NumberFormatException e) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        List<UsableBlock> blockList = getData();
        for (UsableBlock usableBlock : blockList) {
            if (usableBlock.getMaterial() == material) {
                usableBlock.setPrice(price);
                setData(blockList);
                return;
            }
        }
        player.sendMessage(Messages.error("config.usableBlock.notFound"));
    }

    private void deleteBlock(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        Material material = Material.matchMaterial(args[2]);
        if (material == null) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        List<UsableBlock> blockList = getData();
        for (UsableBlock usableBlock : blockList) {
            if (usableBlock.getMaterial() == material) {
                blockList.remove(usableBlock);
                setData(blockList);
                return;
            }
        }
        player.sendMessage(Messages.error("config.usableBlock.notFound"));
    }

    private void listBlock(Player player, String[] args) {
        StringBuilder message = new StringBuilder();
        message.append("-----\n");
        for (UsableBlock usableBlock : getData()) {
            message.append(usableBlock.getMaterial().getKey()).append(" : ").append(usableBlock.getPrice()).append("円\n");
        }
        message.append("-----");
        player.sendMessage(message.toString());
    }
}