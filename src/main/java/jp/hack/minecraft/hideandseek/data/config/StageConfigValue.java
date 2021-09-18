package jp.hack.minecraft.hideandseek.data.config;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.data.StageData;
import jp.hack.minecraft.hideandseek.data.StageType;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static jp.hack.minecraft.hideandseek.data.StageType.*;

public class StageConfigValue extends ConfigValue<List<StageData>> {
    public StageConfigValue(Game game, String key) {
        super(game, key);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.error("error.command.notPlayer"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(Messages.error("error.command.illegalArgument"));
            return true;
        }
        Player player = (Player) sender;
        switch (args[1]) {
            case "set":
                setStage(player, args);
                break;
            case "delete":
                deleteStage(player, args);
                break;
            case "create":
                createStage(player, args);
                break;
            case "edit":
                editStage(player, args);
                break;
            case "list":
                listStage(player, args);
                break;
            default:
                player.sendMessage(Messages.error("error.command.illegalArgument"));
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 2) return new ArrayList<>();
        if (args.length == 2) {
            return new ArrayList<>(Arrays.asList("set", "delete", "create", "edit", "list")).stream()
                    .filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        }
        if (args[1].equals("create") && args.length == 3) return new ArrayList<>(Arrays.asList("(name)"));
        if ((args[1].equals("set") || args[1].equals("delete") || args[1].equals("edit")) && args.length == 3)
            return game.getStageList().stream().map(StageData::getName).collect(Collectors.toList());
        if (args[1].equals("edit") && args.length == 4)
            return new ArrayList<>(Arrays.asList("lobby", "seekerLobby", "stage", "radius"));
        return new ArrayList<>();
    }

    private void setStage(Player player, String[] args) {
        if (args.length <= 2) {
            player.sendMessage(Messages.error("error.command.illegalArgument"));
            return;
        }
        String name = args[2];
        Optional<StageData> stageDataOptional = game.getStageList().stream().filter(stageData -> stageData.getName().equals(name)).findFirst();
        if (!stageDataOptional.isPresent()) {
            player.sendMessage(Messages.error("error.stage.notFoundName", name));
            return;
        }
        StageData stageData = stageDataOptional.get();
        game.setStage(stageData);
        player.sendMessage(Messages.greenMessage("stage.set", name));
    }

    private void deleteStage(Player player, String[] args) {
        if (args.length <= 2) {
            player.sendMessage(Messages.error("error.command.illegalArgument"));
            return;
        }
        String name = args[2];
        Optional<StageData> stageDataOptional = game.getStageList().stream().filter(stageData -> stageData.getName().equals(name)).findFirst();
        if (!stageDataOptional.isPresent()) {
            player.sendMessage(Messages.error("error.stage.notFoundName", name));
            return;
        }
        StageData stageData = stageDataOptional.get();
        game.deleteStage(stageData);
        player.sendMessage(Messages.greenMessage("stage.deleted", name));
    }

    private void createStage(Player player, String[] args) {
        if (args.length <= 2) {
            player.sendMessage(Messages.error("error.command.illegalArgument"));
            return;
        }
        String name = args[2];
        // 9文字以上は却下
        if (name.length() > 9) {
            player.sendMessage(Messages.error("error.command.tooLongArgument"));
            return;
        }
        StageData stageData = game.createNewStage(name);
        if (stageData == null) {
            player.sendMessage(Messages.error("error.stage.alreadyExist", name));
            return;
        }
        player.sendMessage(Messages.greenMessage("stage.crated", name));
    }

    private void editStage(Player player, String[] args) {
        if (args.length <= 2) {
            player.sendMessage(Messages.error("error.command.illegalArgument"));
            return;
        }
        String name = args[2];
        if (game.getStageList().stream().noneMatch(stageData -> stageData.getName().equals(name))) {
            player.sendMessage(Messages.error("error.stage.notFoundName", name));
            return;
        }
        Optional<StageData> stageDataOptional = game.getStageList().stream().filter(stageData -> stageData.getName().equals(name)).findFirst();
        if (!stageDataOptional.isPresent()) return;
        StageData stageData = stageDataOptional.get();
        String type = args[3];
        StageType stageType = null;
        switch (type) {
            case "lobby":
                stageType = LOBBY;
                stageData.setLocation(stageType, player.getLocation(), game.getConfigLoader());
//                game.getConfigLoader().setData("location.stage", player.getLocation());
                break;
            case "seekerLobby":
                stageType = SEEKER_LOBBY;
                stageData.setLocation(stageType, player.getLocation(), game.getConfigLoader());
//                game.getConfigLoader().setData("location.stage", player.getLocation());
                break;
            case "stage":
                stageType = STAGE;
                break;
            case "radius":
                editStageRadius(player, args);
                return;
            default:
                player.sendMessage(Messages.error("error.command.illegalArgument"));
                break;
        }
        stageData.setLocation(stageType, player.getLocation(), game.getConfigLoader());
        player.sendMessage(Messages.greenMessage("stage.edited", name, type));
    }

    private void editStageRadius(Player player, String[] args) {
        if (args.length <= 3) {
            player.sendMessage(Messages.error("error.command.illegalArgument"));
            return;
        }
        String name = args[2];
        if (game.getStageList().stream().noneMatch(stageData -> stageData.getName().equals(name))) {
            player.sendMessage(Messages.error("error.stage.notFoundName", name));
            return;
        }
        Optional<StageData> stageDataOptional = game.getStageList().stream().filter(stageData -> stageData.getName().equals(name)).findFirst();
        if (!stageDataOptional.isPresent()) return;
        StageData stageData = stageDataOptional.get();
//        String type = args[3];
        double radius = Double.parseDouble(args[4]);
        stageData.setRadius(radius, game.getConfigLoader());
        player.sendMessage(Messages.greenMessage("stage.edited", name, StageType.RADIUS));
    }

    private void listStage(Player player, String[] args) {
        List<String> sendTexts = new ArrayList<>();
        game.getStageList().forEach(stageData -> {
            StringBuilder builder = new StringBuilder();
            builder.append("| [" + stageData.getName() + "] :").append("\n")
                    .append("|     lobby: ").append(getMessageFromBoolean(stageData.getLobby() != null)).append("\n")
                    .append("|     seekerLobby: ").append(getMessageFromBoolean(stageData.getSeekerLobby() != null)).append("\n")
                    .append("|     stage: ").append(getMessageFromBoolean(stageData.getStage() != null)).append("\n")
                    .append("|     radius: ").append(getMessageFromBoolean(stageData.getRadius() != 0)).append("\n");
            sendTexts.add(builder.toString());
        });
        player.sendMessage(sendTexts.toArray(new String[0]));
    }

    private String getMessageFromBoolean(boolean value) {
        if (value) return ChatColor.GREEN + "true" + ChatColor.RESET;
        else return ChatColor.RED + "false" + ChatColor.RESET;
    }
}