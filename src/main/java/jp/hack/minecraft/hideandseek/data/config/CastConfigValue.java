/*
 * Copyright 2021 ZOI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * */

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
            throw new IllegalArgumentException(Messages.error("error.config.noConfig", key));
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