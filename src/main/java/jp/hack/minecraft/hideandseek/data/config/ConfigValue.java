package jp.hack.minecraft.hideandseek.data.config;

import jp.hack.minecraft.hideandseek.Game;

public class ConfigValue<T> extends CastConfigValue<T, T> {
    public ConfigValue(Game game, String key) {
        super(game, key, value -> value, data -> data);
    }

    @Override
    public T getData() {
        return super.getData();
    }

    @Override
    public void setData(T value) {
        super.setData(value);
    }
}