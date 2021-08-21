package jp.hack.minecraft.hideandseek.player;

import jp.hack.minecraft.hideandseek.data.EffectType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Seeker extends GamePlayer {
    private int countHiLight = 0;

    public int getCountHiLight() {
        return countHiLight;
    }

    public void addCountHiLight() {
        countHiLight++;
    }

    public Seeker(Player player) {
        super(player);
        getPlayer().setPlayerListName(ChatColor.RED + getPlayer().getName() + ChatColor.RESET);
        player.setInvisible(false);
        giveEffect(EffectType.SEEKER_UP_SPEED);

        Inventory inventory = getPlayer().getInventory();
        inventory.clear();
    }

    @Override
    public void equipItem() {
        Inventory inventory = getPlayer().getInventory();
        {
            ItemStack item = new ItemStack(Material.COMPASS);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;

            final String NAME = ChatColor.YELLOW.toString() + "ブロックを鑑定";
            meta.setDisplayName(NAME);
            final List<String> LORE = Arrays.asList(ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "左" + ChatColor.RESET.toString() + ChatColor.WHITE.toString() + "クリックでブロックを鑑定");
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
            final List<String> LORE = Arrays.asList(ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "右" + ChatColor.RESET.toString() + ChatColor.WHITE.toString() + "クリックでプレイヤーを確保");
            meta.setLore(LORE);

            item.setItemMeta(meta);
            inventory.addItem(item);
        }
        {
            ItemStack item = new ItemStack(Material.CLOCK);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;

            final String NAME = ChatColor.GREEN.toString() + "プレイヤーをハイライト";
            meta.setDisplayName(NAME);
            final List<String> LORE = Arrays.asList(
                    ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "右" + ChatColor.RESET.toString() + ChatColor.WHITE.toString() + "クリックでプレイヤーをハイライト",
                    ChatColor.WHITE.toString() + "効果時間は" + ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + EffectType.HI_LIGHT.getDuration() + ChatColor.RESET.toString() + ChatColor.WHITE.toString() +"秒",
                    ChatColor.WHITE.toString() + "クールタイムは" + ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + EffectType.HI_LIGHT.getCoolTime() + ChatColor.RESET.toString() + ChatColor.WHITE.toString() +"秒"
            );
            meta.setLore(LORE);

            item.setItemMeta(meta);
            inventory.addItem(item);
        }
    }

    public void knock(Location loc) {
        sendRedMessage("game.block.notPlayer");
        getPlayer().playSound(loc, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0F, 1.0F);
    }

    public void discover() {
        sendGreenMessage("game.block.player");
        sendRedTitle(2, 20, 2, "game.block.player", "");
        getPlayer().playSound(getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 0.5F, 1.0F);
    }

    public void hiLight(Boolean boo) {
        if (boo) {
            sendGreenMessage("game.you.hiLight");
            getPlayer().playSound(getLocation(), Sound.AMBIENT_CAVE, 1.1F, 1.0F);
            addCountHiLight();
        } else {
            sendRedMessage("game.you.coolTime");
        }
    };
}
