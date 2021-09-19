package jp.hack.minecraft.hideandseek.command.hideandseek.admin;

import jp.hack.minecraft.hideandseek.command.AdminCommandMaster;
import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.CommandMaster;
import jp.hack.minecraft.hideandseek.data.GameState;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ForceJoinCommand extends AdminCommandMaster {
    public ForceJoinCommand(CommandManager manager) {
        super(manager);
    }

    @Override
    public String getName() {
        return "forcejoin";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (manager.game.getCurrentState() == GameState.PLAYING) {
            sender.sendMessage(Messages.error("error.game.alreadyStarted"));
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(Messages.error("error.command.noEnoughArgument"));
            return true;
        }
        System.out.println(Arrays.toString(args));
        String playerName = args[1];
        Player target = Bukkit.getPlayer(playerName);
        if(target == null) {
            sender.sendMessage(Messages.error("error.game.noPlayer"));
            return true;
        }
        manager.game.forceJoin(sender, target);
        return true;
    }

    @Override
    public boolean getExecutable(CommandSender sender, CommandMaster command) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        return super.getExecutable(sender, this)
                && manager.game.getCurrentState() == GameState.LOBBY
                && !manager.game.getGamePlayers().containsKey(player.getUniqueId());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length != 2) return new ArrayList<>();
        List<UUID> gamePlayers = manager.game.getGamePlayers().values().stream()
                .map(gamePlayer -> gamePlayer.getPlayer().getUniqueId())
                .collect(Collectors.toList());
        List<Player> notJoinedPlayers = Bukkit.getOnlinePlayers().stream()
                .filter(player -> !gamePlayers.contains(player.getUniqueId()))
                .collect(Collectors.toList());
        return notJoinedPlayers.stream().map(HumanEntity::getName).collect(Collectors.toList());
    }
}
