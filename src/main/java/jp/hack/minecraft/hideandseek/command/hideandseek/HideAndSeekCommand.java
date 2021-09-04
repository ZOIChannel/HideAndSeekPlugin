package jp.hack.minecraft.hideandseek.command.hideandseek;

import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.CommandMaster;
import jp.hack.minecraft.hideandseek.command.PlayerCommandMaster;
import jp.hack.minecraft.hideandseek.command.hideandseek.admin.*;
import jp.hack.minecraft.hideandseek.command.hideandseek.player.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HideAndSeekCommand extends PlayerCommandMaster {
    public HideAndSeekCommand(CommandManager manager) {
        super(manager);
        addSubCommand(new ConfigCommand(manager));
        addSubCommand(new StartCommand(manager));
        addSubCommand(new StopCommand(manager));
        addSubCommand(new CancelCommand(manager));
        addSubCommand(new JoinCommand(manager));
        addSubCommand(new ForceJoinCommand(manager));
//        addSubCommand(new TestCommand(manager));
    }

    @Override
    public String getName() {
        return "hideandseek";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (String s : args) System.out.println(s);
        if (args.length < 2) return false;
        if (!subCommands.containsKey(args[1].toLowerCase())) return false;
        return subCommands.get(args[1]).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Map<String, CommandMaster> executableCommands = subCommands.entrySet().stream()
                .filter(entry -> entry.getValue().getExecutable(sender, entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (args.length <= 1)
            return Stream.of(getName()).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        if (args.length <= 2)
            return executableCommands.keySet().stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        if(!executableCommands.containsKey(args[1])) return new ArrayList<>();
        return executableCommands.get(args[1]).onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
    }
}
