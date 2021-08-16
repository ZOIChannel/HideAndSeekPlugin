package jp.hack.minecraft.hideandseek.system;

import jp.hack.minecraft.hideandseek.data.StageData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigLoader {
    private JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigLoader(JavaPlugin plugin) {
        this.plugin = plugin;
        ConfigurationSerialization.registerClass(StageData.class);
        plugin.saveDefaultConfig(); // 未定義の要素があっても、configファイルが空でなければ追加しない
        config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public void setData(String path, Object value) {
        config.set(path, value);
        plugin.saveConfig();
    }

    public Object getData(String path) {
        return config.get(path);
    }

    public Integer getInt(String path) {
        return config.getInt(path);
    }
}
