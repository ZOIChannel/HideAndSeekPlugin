package jp.hack.minecraft.hideandseek;

import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.hideandseek.HideAndSeekCommand;
import jp.hack.minecraft.hideandseek.data.GamePlayer;
import jp.hack.minecraft.hideandseek.data.GameState;
import jp.hack.minecraft.hideandseek.data.StageData;
import jp.hack.minecraft.hideandseek.system.GameLogic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Game extends JavaPlugin {

    private List<GamePlayer> playerList;
    private GameState currentState;
    private GameLogic gameLogic;
    private FileConfiguration config;
    private StageData stageData;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        commandManager = new CommandManager(this);
        commandManager.addRootCommand(new HideAndSeekCommand(commandManager)); // plugin.ymlへの登録を忘れずに
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
