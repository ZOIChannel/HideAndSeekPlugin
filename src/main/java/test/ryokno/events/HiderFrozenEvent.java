package test.ryokno.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import test.ryokno.player.Hider;

public class HiderFrozenEvent extends Event {
    public static final HandlerList HANDLERS = new HandlerList();
    private final Hider hider;

    public HiderFrozenEvent(Hider hider) {
        this.hider = hider;
    }

    @Override
    public String getEventName() {
        return "HiderFrozenEvent";
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Hider getHider() {
        return hider;
    }
}
