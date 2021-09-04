package jp.hack.minecraft.hideandseek.data;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UsableBlock implements ConfigurationSerializable {
    private Material material;
    private Integer price;

    public Material getMaterial() {
        return material;
    }

    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }

    public UsableBlock(Material material, Integer price) {
        this.material = material;
        this.price = price;
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("material", material.getKey().getKey().toUpperCase(Locale.ROOT));
        map.put("price", price);
        return map;
    }

    public static UsableBlock deserialize(Map<String, Object> map) {
        return new UsableBlock(Material.getMaterial(String.valueOf(map.get("material"))), (Integer) map.get("price"));
    }

}
