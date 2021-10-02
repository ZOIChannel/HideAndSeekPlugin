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

package jp.hack.minecraft.hideandseek.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventWatcherEvent extends Event {
    public static final HandlerList HANDLERS = new HandlerList();

    @Override
    public String getEventName() {
        return "EventWatcherEvent";
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
