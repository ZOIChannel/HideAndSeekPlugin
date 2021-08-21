package jp.hack.minecraft.hideandseek.data;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import jp.hack.minecraft.hideandseek.Game;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlockGui {
    private final Game game;
    private final ChestGui gui;
    private final List<ItemStack> itemList = new ArrayList<>();

    public BlockGui(Game game, Player player) {
        this.game = game;
        gui = new ChestGui(5, "ブロックを選択");
        OutlinePane pane = new OutlinePane(0, 0, 9, 5);

        game.getPlayerUsableBlocks(player).forEach(usableBlock -> {
            itemList.add(new ItemStack(usableBlock.getMaterial()));
        });

        itemList.forEach(item -> {
            pane.addItem(new GuiItem(item, event -> {
                game.setHiderMaterial(event.getWhoClicked().getUniqueId(), item.getType());
                event.setCancelled(true);
            }));
        });
        gui.addPane(pane);
    }

    public void openGui(Player player) {
        gui.show(player);
    }
}
