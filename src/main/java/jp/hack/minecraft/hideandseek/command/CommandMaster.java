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

package jp.hack.minecraft.hideandseek.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CommandMaster {
    protected CommandManager manager;

    public CommandMaster(CommandManager manager) {
        this.manager = manager;

        //Nameを実装していない場合はAssertを発生させる
        assert getName() != null;
    }

    protected Map<String, CommandMaster> subCommands = new HashMap<>();

    public abstract String getName();

    public abstract String getPermission();

    protected void addSubCommand(CommandMaster childCommand) {
        subCommands.put(childCommand.getName(), childCommand);
    }

    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    public boolean getExecutable(CommandSender sender, CommandMaster command){
        if(command.getPermission() == null) return true;
        return sender.hasPermission(command.getPermission());
    }

    public abstract List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);
    // return new ArrayList<>(subCommands.keySet());
}
