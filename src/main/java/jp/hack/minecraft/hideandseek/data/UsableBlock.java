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
