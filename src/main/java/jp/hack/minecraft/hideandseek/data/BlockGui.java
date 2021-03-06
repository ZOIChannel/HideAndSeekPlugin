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

package jp.hack.minecraft.hideandseek.data;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.player.GamePlayer;
import jp.hack.minecraft.hideandseek.player.Hider;
import jp.hack.minecraft.hideandseek.system.Messages;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BlockGui {
    private final Game game;
    private final ChestGui gui;
    private final List<ItemStack> itemList = new ArrayList<>();
    private final OutlinePane pane;

    public BlockGui(Game game, Player player) {
        this.game = game;
        gui = new ChestGui(5, "ブロックを選択");
        pane = new OutlinePane(0, 0, 9, 5);
        reloadItemList(player);
        gui.addPane(pane);
        gui.setOnClose(event -> {
            Player clickedPlayer = (Player) event.getPlayer();
            GamePlayer gamePlayer = game.getGamePlayer(clickedPlayer.getUniqueId());
            if (gamePlayer != null && gamePlayer.isHider()) ((Hider) gamePlayer).setOpeningGui(false);
        });
    }

    public void openGui(Player player) {
        GamePlayer gamePlayer = game.getGamePlayer(player.getUniqueId());
        if (!gamePlayer.isHider()) return;
        ((Hider) gamePlayer).setOpeningGui(true);
        reloadItemList(player);
        gui.show(player);
    }

    private void reloadItemList(Player player) {
        itemList.clear();
        game.getPlayerUsableBlocks(player).forEach(usableBlock -> {
            ItemStack item = new ItemStack(usableBlock.getMaterial());
            if (Game.getEconomy() != null) {
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setLore(Collections.singletonList(
                        Messages.message("game.block.cost", ChatColor.YELLOW.toString() + ChatColor.BOLD + usableBlock.getPrice())));
//                        ChatColor.WHITE.toString() + "価格: " + ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "$" + usableBlock.getPrice()));
                item.setItemMeta(meta);
            }
            itemList.add(item);
        });
        pane.clear();
        itemList.forEach(item -> {
            pane.addItem(new GuiItem(item, event -> {
                Player clickedPlayer = (Player) event.getWhoClicked();
                Optional<UsableBlock> usableBlockOptional = game.getUsableBlocks().stream().filter(uBlock -> uBlock.getMaterial() == item.getType()).findFirst();
                if (!usableBlockOptional.isPresent()) return;
                UsableBlock usableBlock = usableBlockOptional.get();

                Economy economy = Game.getEconomy();
                if (economy != null) {
                    if (economy.getBalance(clickedPlayer) < usableBlock.getPrice()) {
                        clickedPlayer.sendMessage(Messages.redMessage("error.buy.noMoney"));
//                        System.out.println("-- event2 003");
                        return;
                    }

//                    System.out.println("-- event2 004");
                    economy.withdrawPlayer(clickedPlayer, usableBlock.getPrice());
                }
                clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.BLOCK_ANVIL_USE, 1F, 1F);
                game.setHiderMaterial(clickedPlayer.getUniqueId(), item.getType());
                game.reloadScoreboard();
                event.setCancelled(true);
                event.getView().close();
            }));
        });
    }
}
