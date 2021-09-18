package jp.hack.minecraft.hideandseek.data;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class HiderAction {
    private final String name;
    private final String lore;
    private final int price;
    private final Material material;
    private final Consumer<Player> action;

    public HiderAction(String name, String lore, int price, Material material, Consumer<Player> action) {
        this.name = name;
        this.lore = lore;
        this.price = price;
        this.material = material;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public String getLore() {
        return lore;
    }

    public Integer getPrice() {
        return price;
    }

    public Material getMaterial() {
        return material;
    }

    public void run(Player player) {
        action.accept(player);
    }
}
