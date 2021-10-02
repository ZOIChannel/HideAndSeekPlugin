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

package jp.hack.minecraft.hideandseek.player;

import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.data.EffectType;
import jp.hack.minecraft.hideandseek.data.GameBoard;
import jp.hack.minecraft.hideandseek.system.Messages;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

abstract public class GamePlayer {
    private final UUID playerUuid;
    private final Player player;
    private final Map<EffectType, Game.MyRunnable> effectMap = new HashMap<>();
    private final GameBoard gameBoard;

    public GamePlayer(Player player) {
        this.playerUuid = player.getUniqueId();
        this.player = player;
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setFoodLevel(20);
        this.player.setCollidable(false);
        gameBoard = new GameBoard(this, Messages.message("hide-and-seek"));

        Inventory inventory = getPlayer().getInventory();
        inventory.clear();
    }

    public void sendGreenTitle(int fadeIn, int stay, int fadeOut, String code, Object... args) {
        getPlayer().sendTitle(Messages.greenMessage(code, args), "", fadeIn, stay, fadeOut);
    }

    public void sendGreenTitle(int fadeIn, int stay, int fadeOut, String code, String subtitle) {
        getPlayer().sendTitle(Messages.greenMessage(code), subtitle, fadeIn, stay, fadeOut);
    }

    public void sendRedTitle(int fadeIn, int stay, int fadeOut, String code, Object... args) {
        getPlayer().sendTitle(Messages.redMessage(code, args), "", fadeIn, stay, fadeOut);
    }

    public void sendRedTitle(int fadeIn, int stay, int fadeOut, String code, String subtitle) {
        getPlayer().sendTitle(Messages.redMessage(code), subtitle, fadeIn, stay, fadeOut);
    }

    public void sendTitle(int fadeIn, int stay, int fadeOut, String code, Object... args) {
        getPlayer().sendTitle(Messages.message(code, args), "", fadeIn, stay, fadeOut);
    }

    public void sendTitle(int fadeIn, int stay, int fadeOut, String code, String subtitle) {
        getPlayer().sendTitle(Messages.message(code), subtitle, fadeIn, stay, fadeOut);
    }

    public void sendGreenMessage(String code, Object... args) {
        getPlayer().sendMessage(Messages.greenMessage(code, args));
    }

    public void sendRedMessage(String code, Object... args) {
        getPlayer().sendMessage(Messages.redMessage(code, args));
    }

    public void giveEffect(EffectType type) {
        if (type.getPotionType() == null) return;
        getPlayer().addPotionEffect(new PotionEffect(type.getPotionType(), type.getDuration()*20, type.getLevel()));
    }

    public void allClearEffect() {
        getPlayer().getActivePotionEffects().forEach(effect -> {
            getPlayer().removePotionEffect(effect.getType());
        });
    }

    public void clearEffect(EffectType type) {
        if (!getPlayer().hasPotionEffect(type.getPotionType())) return;
        getPlayer().removePotionEffect(type.getPotionType());
    }

    public void equipItem(ItemStack itemStack) {
        Inventory inventory = getPlayer().getInventory();
        inventory.addItem(itemStack);
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Player getPlayer() {
        return player;
    }

    public Map<EffectType, Game.MyRunnable> getEffectMap() {
        return effectMap;
    }

    public Location getLocation() {
        return getPlayer().getLocation();
    }

    public Boolean isHider() {
        return this instanceof Hider;
    }
    public Boolean isSeeker() {
        return this instanceof Seeker;
    }

    public Boolean isSameRole(Role role) {
        if (isHider() && role == Role.HIDER) {
            return true;
        } else return isSeeker() && role == Role.SEEKER;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }
}
