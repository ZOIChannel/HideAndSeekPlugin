//package jp.hack.minecraft.hideandseek.command.hideandseek.admin;
//
//import jp.hack.minecraft.hideandseek.Game;
//import jp.hack.minecraft.hideandseek.command.AdminCommandMaster;
//import jp.hack.minecraft.hideandseek.command.CommandManager;
//import jp.hack.minecraft.hideandseek.command.CommandMaster;
//import jp.hack.minecraft.hideandseek.data.GameState;
//import jp.hack.minecraft.hideandseek.system.Messages;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class TestCommand extends AdminCommandMaster {
//    public TestCommand(CommandManager manager) {
//        super(manager);
//    }
//
//    @Override
//    public String getName() {
//        return "test";
//    }
//
//    @Override
//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        Game game = manager.game;
//        game.destroyGamePlayers();
//        List<Player> players = new ArrayList<>(game.getServer().getOnlinePlayers());
//        players.forEach(game::createHider);
//        int rnd = new Random().nextInt(players.size());
//        game.createSeeker(players.get(rnd));
//        game.getEventWatcher().start(); // Game.onEnableへ移動(reloadしたとき反映されないから)
//        return true;
//    }
//
//    @Override
//    public boolean getExecutable(CommandSender sender, CommandMaster command){
//        return super.getExecutable(sender, this);
//    }
//
//    @Override
//    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
//        return null;
//    }
//}