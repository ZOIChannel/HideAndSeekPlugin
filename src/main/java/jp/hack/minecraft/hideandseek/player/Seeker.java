package jp.hack.minecraft.hideandseek.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Seeker extends GamePlayer {
    public Seeker(Player player) {
        super(player);
        equipItem();
    }

    @Override
    public void equipItem() {
        Inventory inventory = getPlayer().getInventory();
        inventory.clear();
        {
            ItemStack item = new ItemStack(Material.WOODEN_PICKAXE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("Attack to block");
            item.setItemMeta(meta);
            inventory.addItem(item);
        }
        {
            ItemStack item = new ItemStack(Material.GLASS_BOTTLE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("Kill moving block");
            item.setItemMeta(meta);
            inventory.addItem(item);
        }
    }
}
