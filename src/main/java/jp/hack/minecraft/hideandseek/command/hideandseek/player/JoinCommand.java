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

package jp.hack.minecraft.hideandseek.command.hideandseek.player;

import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.CommandMaster;
import jp.hack.minecraft.hideandseek.command.PlayerCommandMaster;
import jp.hack.minecraft.hideandseek.data.GameState;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class JoinCommand extends PlayerCommandMaster {
    public JoinCommand(CommandManager manager) {
        super(manager);
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (manager.game.getCurrentState() == GameState.PLAYING) {
            sender.sendMessage(Messages.error("error.game.alreadyStarted"));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.error("error.command.notPlayer"));
            return true;
        }
        Player player = (Player)sender;
        manager.game.join(player);
        return true;
    }

    @Override
    public boolean getExecutable(CommandSender sender, CommandMaster command) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        return super.getExecutable(sender, this)
                && manager.game.getCurrentState() == GameState.LOBBY
                && !manager.game.getGamePlayers().containsKey(player.getUniqueId());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
