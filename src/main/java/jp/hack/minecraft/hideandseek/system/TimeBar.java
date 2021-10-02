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

package jp.hack.minecraft.hideandseek.system;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;


public class TimeBar {
    private final String title = ChatColor.RED + "残り時間";
    private final BarColor color = BarColor.RED;
    private final BarStyle style = BarStyle.SOLID;
    private final BossBar bossBar;

    public TimeBar() {
        bossBar = Bukkit.createBossBar(title, color, style);
        setVisible(false);
    }

    public void addPlayer(Player player) {
        bossBar.addPlayer(player);
    }

    public Boolean isVisible() {
        return bossBar.isVisible();
    }

    public void setVisible(Boolean visible) {
        bossBar.setVisible(visible);
    }

    public void setProgress(float percent) {
        if (percent < 0.0) return;
        if (percent > 1.0) return;
        bossBar.setProgress(percent);
    }
}
