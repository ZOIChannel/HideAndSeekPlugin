package jp.hack.minecraft.hideandseek.data.config;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ConfigValue<T> {
    protected final Game game;
    private final String key;

    public ConfigValue(Game game, String key) {
        this.game = game;
        this.key = key;
        if(!game.getConfigLoader().contains(key)) throw new IllegalArgumentException(Messages.error("error.config.noSetting", key));
    }

    public String getKey(){
        return key;
    }

    public T getData() {
        return (T) game.getConfigLoader().getData(key);
    }

    public void setData(T value) {
        game.getConfigLoader().setData(key, value);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}