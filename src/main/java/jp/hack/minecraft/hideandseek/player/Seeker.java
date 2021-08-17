package jp.hack.minecraft.hideandseek.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Seeker extends GamePlayer {
    public Seeker(Player player) {
        super(player);
        equipItem();
    }

    @Override
    public void equipItem() {
        Inventory inventory = getPlayer().getInventory();
        inventory.clear();
        inventory.addItem(new ItemStack(Material.WOODEN_PICKAXE));
        inventory.addItem(new ItemStack(Material.GLASS_BOTTLE));
    }
}
