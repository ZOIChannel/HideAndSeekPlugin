package jp.hack.minecraft.hideandseek.data;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import jp.hack.minecraft.hideandseek.Game;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlockGui {
    private final Game game;
    private final ChestGui gui;
    private final List<ItemStack> itemList = new ArrayList<>();

    public BlockGui(Game game) {
        this.game = game;
        gui = new ChestGui(5, "Select Block");
        OutlinePane pane = new OutlinePane(0, 0, 9, 5);

        itemList.add(new ItemStack(Material.ICE));
        itemList.add(new ItemStack(Material.FLOWER_POT));
        itemList.add(new ItemStack(Material.OAK_WOOD));

        itemList.forEach(item -> {
            pane.addItem(new GuiItem(item, event -> {
                game.setBlock(event.getWhoClicked().getUniqueId(), item.getType());
                System.out.println("test");
                event.setCancelled(true);
            }));
        });
        gui.addPane(pane);
    }

    public void openGui(Player player) {
        gui.show(player);
    }
}
