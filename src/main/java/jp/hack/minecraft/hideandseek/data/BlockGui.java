package jp.hack.minecraft.hideandseek.data;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import jp.hack.minecraft.hideandseek.Game;
import jp.hack.minecraft.hideandseek.system.Messages;
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
    }

    public void openGui(Player player) {
        reloadItemList(player);
        gui.show(player);
    }

    private void reloadItemList(Player player) {
        itemList.clear();
        game.getPlayerUsableBlocks(player).forEach(usableBlock -> {
            ItemStack item = new ItemStack(usableBlock.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList("価格: " + usableBlock.getPrice() + "円"));
            item.setItemMeta(meta);
            itemList.add(item);
        });
        pane.clear();
        itemList.forEach(item -> {
            pane.addItem(new GuiItem(item, event -> {
                Player clickedPlayer = (Player) event.getWhoClicked();
                Optional<UsableBlock> usableBlockOptional = game.getUsableBlocks().stream().filter(uBlock -> uBlock.getMaterial() == item.getType()).findFirst();
                if (!usableBlockOptional.isPresent()) return;
                UsableBlock usableBlock = usableBlockOptional.get();
//                Game.getEconomy().depositPlayer(clickedPlayer, -1 * Game.getEconomy().getBalance(clickedPlayer));
                if (Game.getEconomy().getBalance(clickedPlayer) < usableBlock.getPrice()) {
                    clickedPlayer.sendMessage(Messages.redMessage("buy.noMoney"));
                    return;
                }
                Game.getEconomy().depositPlayer(clickedPlayer, -1 * usableBlock.getPrice());
                clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.BLOCK_ANVIL_USE, 1F, 1F);
                game.setHiderMaterial(clickedPlayer.getUniqueId(), item.getType());
                game.reloadScoreboard();
                event.setCancelled(true);
                event.getView().close();
            }));
        });
    }
}
