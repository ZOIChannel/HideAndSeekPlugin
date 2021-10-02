/*
 * Copyright 2021 ZOI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * */

package jp.hack.minecraft.hideandseek.data;

import jp.hack.minecraft.hideandseek.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

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

    public UUID getUuid(){
        return armorStand.getUniqueId();
    }
}
