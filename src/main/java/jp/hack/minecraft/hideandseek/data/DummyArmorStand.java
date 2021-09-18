package jp.hack.minecraft.hideandseek.data;

import jp.hack.minecraft.hideandseek.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DummyArmorStand {
    private ArmorStand armorStand;
    private GamePlayer gamePlayer;

    public DummyArmorStand(GamePlayer gamePlayer){
        this.gamePlayer = gamePlayer;
    }
    public void create(){
        armorStand = (ArmorStand) gamePlayer.getLocation().getWorld().spawnEntity(gamePlayer.getLocation(), EntityType.ARMOR_STAND);
        armorStand.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,65536, 16));
        armorStand.setCustomName(gamePlayer.getPlayer().getName());
        armorStand.setCustomNameVisible(true);
        armorStand.setArms(true);

        ItemStack skullStack = new ItemStack(Material.PLAYER_HEAD); // set damage 3 (short)
        SkullMeta skullMeta = (SkullMeta) skullStack.getItemMeta();
        skullMeta.setOwningPlayer(gamePlayer.getPlayer());
        skullStack.setItemMeta(skullMeta);
        armorStand.getEquipment().setHelmet(skullStack);

        armorStand.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        armorStand.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        armorStand.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
    }

    public void destroy(){
        armorStand.getEquipment().clear();
        armorStand.remove();
    }
}
