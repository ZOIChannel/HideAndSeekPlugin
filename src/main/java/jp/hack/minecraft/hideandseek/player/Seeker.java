package jp.hack.minecraft.hideandseek.player;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Seeker extends GamePlayer {
    public Seeker(Player player) {
        super(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.setInvisible(false);
        equipItem();
    }

    @Override
    public void equipItem() {
        Inventory inventory = getPlayer().getInventory();
        inventory.clear();
        {
            ItemStack item = new ItemStack(Material.COMPASS);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;

            final String NAME = ChatColor.YELLOW.toString() + "ブロックを鑑定";
            meta.setDisplayName(NAME);
            final List<String> LORE = Arrays.asList(ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "左" + ChatColor.RESET.toString() + "クリックでブロックを鑑定");
            meta.setLore(LORE);

            item.setItemMeta(meta);
            inventory.addItem(item);
        }
        {
            ItemStack item = new ItemStack(Material.GLASS_BOTTLE);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;

            final String NAME = ChatColor.RED.toString() + "プレイヤーを確保";
            meta.setDisplayName(NAME);
            final List<String> LORE = Arrays.asList(ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "右" + ChatColor.RESET.toString() + "クリックでプレイヤーを確保");
            meta.setLore(LORE);

            item.setItemMeta(meta);
            inventory.addItem(item);
        }
    }
}
