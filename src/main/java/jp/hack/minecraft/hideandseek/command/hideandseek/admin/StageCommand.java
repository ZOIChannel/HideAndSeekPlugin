package jp.hack.minecraft.hideandseek.command.hideandseek.admin;

import jp.hack.minecraft.hideandseek.command.AdminCommandMaster;
import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.data.StageData;
import jp.hack.minecraft.hideandseek.data.StageType;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static jp.hack.minecraft.hideandseek.data.StageType.*;

public class StageCommand extends AdminCommandMaster {
    public StageCommand(CommandManager manager) {
        super(manager);
    }

    @Override
    public String getName() {
        return "stage";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.error("command.notPlayer"));
            return true;
        }
        if (args.length <= 1) {
            sender.sendMessage(Messages.error("command.illegalArgument"));
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
                player.sendMessage(Messages.error("command.illegalArgument"));
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 2) return new ArrayList<>();
        if (args.length == 2) {
            return new ArrayList<>(Arrays.asList("create", "list", "edit")).stream()
                    .filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        }
        if (args[1].equals("create") && args.length == 3) return new ArrayList<>(Arrays.asList("name"));
        if (args[1].equals("edit") && args.length == 3)
            return manager.game.getStageList().stream().map(StageData::getName).collect(Collectors.toList());
        if (args[1].equals("edit") && args.length == 4)
            return new ArrayList<>(Arrays.asList("lobby", "seekerLobby", "stage", "radius"));
        return new ArrayList<>();
    }

    private void setStage(Player player, String[] args) {
        if (args.length <= 2) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        String name = args[2];
        Optional<StageData> stageDataOptional = manager.game.getStageList().stream().filter(stageData -> stageData.getName().equals(name)).findFirst();
        if (!stageDataOptional.isPresent()) {
            player.sendMessage(Messages.error("stage.notFoundName", name));
            return;
        }
        StageData stageData = stageDataOptional.get();
        manager.game.setStage(stageData);
        player.sendMessage(Messages.greenMessage("stage.crated", name));
    }

    private void deleteStage(Player player, String[] args) {
        if (args.length <= 2) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        String name = args[2];
        Optional<StageData> stageDataOptional = manager.game.getStageList().stream().filter(stageData -> stageData.getName().equals(name)).findFirst();
        if (!stageDataOptional.isPresent()) {
            player.sendMessage(Messages.error("stage.notFoundName", name));
            return;
        }
        StageData stageData = stageDataOptional.get();
        manager.game.deleteStage(stageData);
        player.sendMessage(Messages.greenMessage("stage.deleted", name));
    }

    private void createStage(Player player, String[] args) {
        if (args.length <= 2) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        String name = args[2];
        // 9文字以上は却下
        if (name.length() > 9) {
            player.sendMessage(Messages.error("command.tooLongArgument"));
            return;
        }
        StageData stageData = manager.game.createNewStage(name);
        if (stageData == null) {
            player.sendMessage(Messages.error("stage.alreadyExist", name));
            return;
        }
        stageData.setLocation(StageType.STAGE, player.getLocation(), manager.game.getConfigLoader());
        player.sendMessage(Messages.greenMessage("stage.crated", name));
    }

    private void editStage(Player player, String[] args) {
        if (args.length <= 2) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        String name = args[2];
        if (manager.game.getStageList().stream().noneMatch(stageData -> stageData.getName().equals(name))) {
            player.sendMessage(Messages.error("stage.notFoundName", name));
            return;
        }
        Optional<StageData> stageDataOptional = manager.game.getStageList().stream().filter(stageData -> stageData.getName().equals(name)).findFirst();
        if (!stageDataOptional.isPresent()) return;
        StageData stageData = stageDataOptional.get();
        String type = args[3];
        StageType stageType = null;
        switch (type) {
            case "lobby":
                stageType = LOBBY;
                stageData.setLocation(stageType, player.getLocation(), manager.game.getConfigLoader());
                manager.game.getConfigLoader().setData("location.stage", player.getLocation());
                player.sendMessage(Messages.greenMessage("stage.edited", name, type));
                break;
            case "seekerLobby":
                stageType = SEEKER_LOBBY;
                stageData.setLocation(stageType, player.getLocation(), manager.game.getConfigLoader());
                manager.game.getConfigLoader().setData("location.stage", player.getLocation());
                player.sendMessage(Messages.greenMessage("stage.edited", name, type));
                break;
            case "stage":
                stageType = STAGE;
                break;
            case "radius":
                editStageRadius(player, args);
                return;
            default:
                player.sendMessage(Messages.error("command.illegalArgument"));
                break;
        }
        stageData.setLocation(stageType, player.getLocation(), manager.game.getConfigLoader());
//        manager.game.getConfigLoader().setData("location.stage", player.getLocation());
        player.sendMessage(Messages.greenMessage("stage.edited", name, type));
    }

    private void editStageRadius(Player player, String[] args) {
        if (args.length <= 3) {
            player.sendMessage(Messages.error("command.illegalArgument"));
            return;
        }
        String name = args[2];
        if (manager.game.getStageList().stream().noneMatch(stageData -> stageData.getName().equals(name))) {
            player.sendMessage(Messages.error("stage.notFoundName", name));
            return;
        }
        Optional<StageData> stageDataOptional = manager.game.getStageList().stream().filter(stageData -> stageData.getName().equals(name)).findFirst();
        if (!stageDataOptional.isPresent()) return;
        StageData stageData = stageDataOptional.get();
//        String type = args[3];
        double radius = Double.parseDouble(args[4]);
        stageData.setRadius(radius, manager.game.getConfigLoader());
        player.sendMessage(Messages.greenMessage("stage.edited", name, StageType.RADIUS));
    }

    private void listStage(Player player, String[] args) {
        List<String> sendTexts = new ArrayList<>(Arrays.asList("|--- name ---|-- lobby --|-- seekerLobby --|-- stage --|-- radius --|"));
        manager.game.getStageList().forEach(stageData -> {
            StringBuilder builder = new StringBuilder();
            builder.append("| ").append(String.format("%-14s", stageData.getName()));
            builder.append("| ").append(String.format("%-12s", (stageData.getLobby() != null)));
            builder.append("| ").append(String.format("%-22s", (stageData.getSeekerLobby() != null)));
            builder.append("| ").append(String.format("%-12s", (stageData.getStage() != null)));
            builder.append("| ").append(String.format("%-14s", (stageData.getRadius() != 0)));
            builder.append("|");
            sendTexts.add(builder.toString());
        });
        sendTexts.add("|-----------|----------|----------------|----------|");
        player.sendMessage(sendTexts.toArray(new String[0]));
    }
}
