package jp.hack.minecraft.hideandseek.data;

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

import org.bukkit.potion.PotionEffectType;

public enum EffectType {
    SEEKER_UP_SPEED(PotionEffectType.SPEED, 0, 10000, 1),
    UP_SPEED(PotionEffectType.SPEED, 30, 5, 2),
    HI_LIGHT(null, 45, 15);

    private PotionEffectType potionType;
    private int coolTime;
    private int duration;
    private int level;

    public PotionEffectType getPotionType() {
        return potionType;
    }

    public int getCoolTime() {
        return coolTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getLevel() {
        return level;
    }

    EffectType(PotionEffectType type, int coolTime, int duration) {
        this(type, coolTime, duration, 1);
    }

    EffectType(PotionEffectType type, int coolTime, int duration, int level) {
        this.potionType = type;
        this.coolTime = coolTime;
        this.duration = duration;
        this.level = level;
    }
}
