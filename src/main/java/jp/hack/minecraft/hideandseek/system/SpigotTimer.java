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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SpigotTimer {
    private JavaPlugin plugin;
    private Listener listener;
    private BukkitTask task;

    public interface Listener {
        void performed();

    }

    public SpigotTimer(JavaPlugin plugin, Listener listener) {
        this.plugin = plugin;
        this.listener = listener;
    }

    public void start(int delay) {
        if (task == null) {
            //20 ticks = 1 second.
            int delayTick = delay*20;
            task = Bukkit.getScheduler().runTaskLater(plugin, () -> listener.performed(), delayTick);
        }
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
