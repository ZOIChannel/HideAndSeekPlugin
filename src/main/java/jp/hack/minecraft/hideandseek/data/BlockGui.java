package jp.hack.minecraft.hideandseek.data;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import jp.hack.minecraft.hideandseek.Game;
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

    public BlockGui(Game game, Player player) {
        this.game = game;
        gui = new ChestGui(5, "ブロックを選択");
        OutlinePane pane = new OutlinePane(0, 0, 9, 5);

        System.out.println("game.getPlayerUsableBlocks(player).size()");
        System.out.println(game.getPlayerUsableBlocks(player).size());
        game.getPlayerUsableBlocks(player).forEach(usableBlock -> {
            ItemStack item = new ItemStack(usableBlock.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList("価格: " + usableBlock.getPrice() + "円"));
            item.setItemMeta(meta);
            itemList.add(item);
        });

        itemList.forEach(item -> {
            pane.addItem(new GuiItem(item, event -> {
                Player clickedPlayer = (Player) event.getWhoClicked();
                game.setHiderMaterial(clickedPlayer.getUniqueId(), item.getType());
                Optional<UsableBlock> usableBlockOptional = game.getUsableBlocks().stream().filter(uBlock -> uBlock.getMaterial() == item.getType()).findFirst();
                if (!usableBlockOptional.isPresent()) return;
                UsableBlock usableBlock = usableBlockOptional.get();
                Game.getEconomy().depositPlayer(clickedPlayer, -1 * usableBlock.getPrice());
                clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.BLOCK_ANVIL_USE, 1F, 1F);
                game.reloadScoreboard();
                event.setCancelled(true);
            }));
        });
        gui.addPane(pane);
    }

    public void openGui(Player player) {
        gui.show(player);
    }
}
