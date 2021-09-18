package jp.hack.minecraft.hideandseek.data.config;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.function.Function;

// F : コード内の型
// T : ファイルに読み書きする型
public class CastConfigValue<F, T> {
    protected final Game game;
    private final String key;
    private final Function<F, T> convert;
    private final Function<T, F> restore;

    public CastConfigValue(Game game, String key, Function<F, T> convert, Function<T, F> restore) {
        this.game = game;
        this.key = key;
        this.convert = convert;
        this.restore = restore;
        if (!game.getConfigLoader().contains(key))
            throw new IllegalArgumentException(Messages.error("error.config.noSetting", key));
    }

    public String getKey() {
        return key;
    }

    public F getData() {
        T rawData = (T) game.getConfigLoader().getData(key);
        return restore.apply(rawData);
    }

    public void setData(F value) {
        T rawValue = convert.apply(value);
        game.getConfigLoader().setData(key, rawValue);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}