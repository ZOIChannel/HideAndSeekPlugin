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

package jp.hack.minecraft.hideandseek.system;

import jp.hack.minecraft.hideandseek.data.StageData;
import jp.hack.minecraft.hideandseek.data.UsableBlock;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

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

    public ItemStack getItemStack(String path) {
        return config.getSerializable(path, ItemStack.class);
    }

    public Integer getInt(String path) {
        return config.getInt(path);
    }

    public Double getDouble(String path) {
        return config.getDouble(path);
    }

    public Boolean contains(String path) {
        return config.contains(path);
    }

    public List<UsableBlock> getUsableBlocks() {
        return config.getMapList("usableBlocks").stream().map(map -> {
            Map<String, Object> usaMap = map.entrySet().stream().collect(Collectors.toMap(String::valueOf, v -> (Object) v));
            return UsableBlock.deserialize(usaMap);
        }).collect(Collectors.toList());
    }
}
