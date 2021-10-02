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
import jp.hack.minecraft.hideandseek.data.PluginGameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class EnumConfigValue<E extends Enum<E>> extends CastConfigValue<E, String> {

    private final Class<E> enumType;

    public EnumConfigValue(Game game, String key, Class<E> enumType) {
        super(game, key, Enum::name, s -> E.valueOf(enumType, s));
        this.enumType = enumType;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2)
            return EnumSet.allOf(PluginGameMode.class).stream()
                    .map(PluginGameMode::name).filter(name -> name.startsWith(args[1].toUpperCase()))
                    .collect(Collectors.toList());
        return new ArrayList<>();
    }
}