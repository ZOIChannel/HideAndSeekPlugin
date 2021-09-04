package jp.hack.minecraft.hideandseek.command.hideandseek.admin;

import jp.hack.minecraft.hideandseek.command.AdminCommandMaster;
import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.data.config.*;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigCommand extends AdminCommandMaster {
    private final List<ConfigValue> editableElements = new ArrayList<>();


    public ConfigCommand(CommandManager manager) {
        super(manager);
        editableElements.add(new StageConfigValue(manager.game, "stage"));
        editableElements.add(new ItemStackConfigValue(manager.game, "captureItem"));
        editableElements.add(new ItemStackConfigValue(manager.game, "speedItem"));
        editableElements.add(new ItemStackConfigValue(manager.game, "hiLightItem"));
        editableElements.add(new IntConfigValue(manager.game, "gameTime"));
        editableElements.add(new IntConfigValue(manager.game, "seekerWaitTime"));
        editableElements.add(new IntConfigValue(manager.game, "seekerRate"));
        editableElements.add(new DoubleConfigValue(manager.game, "reward"));
        editableElements.add(new UsableBlockConfigValue(manager.game, "usableBlocks"));
    }

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage(Messages.error("command.notEnoughArgument"));
            return true;
        }
        String selectedKey = args[1];
        for (ConfigValue configValue :
                editableElements) {
            if (configValue.getKey().equals(selectedKey)) {
                configValue.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }
        sender.sendMessage(Messages.error("config.noSetting"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return editableElements.stream().map(ConfigValue::getKey).collect(Collectors.toList());
        }
        for (ConfigValue configValue :
                editableElements) {
            if (configValue.getKey().equals(args[1])) {
                return configValue.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return Collections.singletonList(Messages.error("config.noSetting"));
    }
}
