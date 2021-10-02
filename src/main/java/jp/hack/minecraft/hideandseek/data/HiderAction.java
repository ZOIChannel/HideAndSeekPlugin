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
