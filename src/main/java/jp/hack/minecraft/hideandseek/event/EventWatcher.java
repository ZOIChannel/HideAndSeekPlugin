package jp.hack.minecraft.hideandseek.event;

import jp.hack.minecraft.hideandseek.Game;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import jp.hack.minecraft.hideandseek.player.Hider;

public class EventWatcher {
    private final long DELAY = 5L;
    private final long LIMIT = 60L;

    private final Game game;
    private BukkitTask task;
    private Boolean isStarted = false;

    public EventWatcher(Game game) {
        this.game = game;
    }

    public void start() {
        if (!isStarted) {
            isStarted = true;
            task = new MyRunTask().runTask(game);
        }
    }

    public void stop() {
        isStarted = false;
        if (task != null)
            task.cancel();
    }

    public Boolean isStarted() {
        return isStarted;
    }

    class MyRunTask extends BukkitRunnable {
        @Override
        public void run() {
            EventWatcherEvent eventWatcherEvent = new EventWatcherEvent();
            game.getServer().getPluginManager().callEvent(eventWatcherEvent);

            game.getGamePlayers().values().forEach(player -> {
                if (!player.isHider()) return;
                Hider hider = (Hider) player;

                if (hider.getPrevLoc() != null) {

                    Location prevLoc = hider.getPrevLoc();
                    Location loc = hider.getLocation();
                    if (game.isSameBlockLocation(prevLoc, loc)) {

                        callEvent(hider);

                    } else {

                        resetCount(hider);

                    }
                }

                hider.setPrevLoc(player.getPlayer().getLocation());

            });
            task = new MyRunTask().runTaskLater(game, DELAY);
        }
    }



    private void callEvent(Hider hider) {
        hider.addFreezeTick(DELAY);

        if (hider.getFreezeTicks() >= LIMIT) {
            if (!hider.isCalledEvent()) {
                hider.setCalledEvent(true);
                HiderFrozenEvent hiderFrozenEvent = new HiderFrozenEvent(hider);
                game.getServer().getPluginManager().callEvent(hiderFrozenEvent);
            }
        }
    }

    private void resetCount(Hider hider) {
        hider.setFreezeTicks(0L);
        hider.setCalledEvent(false);
    }
}
