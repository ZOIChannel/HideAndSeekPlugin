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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ActionGui {
    private final Game game;
    private final ChestGui gui;
    private final List<ItemStack> itemList = new ArrayList<>();
    private final OutlinePane pane;

    public ActionGui(Game game, Player player) {
        this.game = game;
        gui = new ChestGui(5, Messages.message("game.action.gui.name"));
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
        game.getActions().forEach(action -> {
            ItemStack item = new ItemStack(action.getMaterial());
            if (Game.getEconomy() != null) {
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setDisplayName(action.getName());
                meta.setLore(new ArrayList<>(Arrays.asList(
                        action.getLore(),
                        ChatColor.WHITE + Messages.message("game.action.reward",ChatColor.YELLOW.toString() + ChatColor.BOLD + action.getPrice())
//                        ChatColor.WHITE + "得られる金額: " + ChatColor.YELLOW + ChatColor.BOLD + "$" + action.getPrice()
                )));
                item.setItemMeta(meta);
            }
            itemList.add(item);
        });
        pane.clear();
        itemList.forEach(item -> {
            pane.addItem(new GuiItem(item, event -> {
                Player clickedPlayer = (Player) event.getWhoClicked();
                Optional<HiderAction> actionOptional = game.getActions().stream().filter(usableAction -> usableAction.getName().equals(item.getItemMeta().getDisplayName())).findFirst();
                if (!actionOptional.isPresent()) return;
                HiderAction action = actionOptional.get();

                action.run(player);

                Economy economy = Game.getEconomy();
                if (economy != null) {
                    economy.depositPlayer(clickedPlayer, action.getPrice());
                }
                game.reloadScoreboard();
                event.setCancelled(true);
                event.getView().close();
            }));
        });
    }
}
