package jp.hack.minecraft.hideandseek.data;

import jp.hack.minecraft.hideandseek.player.GamePlayer;
import org.bukkit.Effect;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
    }

    public void destroy(){
        armorStand.remove();
    }
}
