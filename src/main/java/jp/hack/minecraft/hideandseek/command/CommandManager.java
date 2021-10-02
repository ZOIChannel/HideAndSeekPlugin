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

import jp.hack.minecraft.hideandseek.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CommandManager implements TabExecutor {
    public Game game;

    public CommandManager(Game game) {
        this.game = game;
    }

    private final Map<String, CommandMaster> rootCommands = new HashMap<>();

    public void addRootCommand(CommandMaster command) {
        rootCommands.remove(command.getName());
        rootCommands.put(command.getName(), command);
        Objects.requireNonNull(game.getCommand(command.getName())).setExecutor(this);
        Objects.requireNonNull(game.getCommand(command.getName())).setTabCompleter(this);
        Objects.requireNonNull(game.getCommand(command.getName())).setPermission(command.getPermission());
    }

    private boolean onCommandImpl(CommandSender sender, Command command, String label, String[] args) {
        if (rootCommands.get(command.getName()).subCommands.containsKey("help")) {
            return rootCommands.get(command.getName()).subCommands.get("help").onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> compiledArgs = new ArrayList<>(Arrays.asList(args));
        compiledArgs.add(0, label);
        args = compiledArgs.toArray(new String[0]);
        /*if (args.length == 0 || !rootCommands.containsKey(args[0])) {
            return onCommandImpl(sender, command, label, args);
        }

         */

        // if(args.length <= 1){
        CommandMaster rootCommannd = rootCommands.get(command.getName());
        // rootCommannd.onCommand(sender, command, label, args);
        // }

        // SubCommand subCommand = subCommands.get(args[0]);
/*
        if (rootCommannd.getPermission() != null && !sender.hasPermission(rootCommannd.getPermission())) {
            sender.sendMessage(I18n.tl("error.command.permission"));
            return false;
        }
        */
        if (!rootCommannd.onCommand(sender, command, label, args)) return onCommandImpl(sender, command, label, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> compiledArgs = new ArrayList<>(Arrays.asList(args));
        compiledArgs.add(0, alias);
        args = compiledArgs.toArray(new String[0]);

        return rootCommands.get(command.getName()).onTabComplete(sender, command, alias, args);
    }
}
