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

package jp.hack.minecraft.hideandseek.command.hideandseek.admin;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.command.AdminCommandMaster;
import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.CommandMaster;
import jp.hack.minecraft.hideandseek.data.GameState;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StartCommand extends AdminCommandMaster {
    public StartCommand(CommandManager manager) {
        super(manager);
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Game game = manager.game;
        if (game.getCurrentState() == GameState.PLAYING) {
            sender.sendMessage(Messages.error("error.game.alreadyStarted"));
            return true;
        }
        if (game.getGamePlayers().size() < 2){
            sender.sendMessage(Messages.error("error.game.noEnoughPlayer"));
            return true;
        }
        if (!game.getCurrentStage().isPresent()) {
            sender.sendMessage(Messages.error("error.stage.none"));
            return true;
        }
        if (!game.getCurrentStage().get().isInitialized()) {
            sender.sendMessage(Messages.error("error.stage.noEnoughData"));
            return true;
        }
        game.start();
        return true;
    }

    @Override
    public boolean getExecutable(CommandSender sender, CommandMaster command){
        return super.getExecutable(sender, this) && manager.game.getCurrentState() == GameState.LOBBY;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
