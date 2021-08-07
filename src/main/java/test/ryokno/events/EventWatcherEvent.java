package test.ryokno.events;

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
