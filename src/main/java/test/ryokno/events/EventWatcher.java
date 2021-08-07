package test.ryokno.events;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import test.ryokno.Game;
import test.ryokno.player.Hider;

public class EventWatcher {
    private final Game game;
    private BukkitTask task;
    private Boolean isStarted = false;
    private Boolean isCalledEvent = false;

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
        private final long DELAY = 5L;
        private final long LIMIT = 60L;

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

                                hider.addFreezeTick(DELAY);
                                System.out.println(hider.getFreezeTicks());

                                    if (hider.getFreezeTicks() >= LIMIT) {
                                            if (!isCalledEvent) {
                                                HiderFrozenEvent hiderFrozenEvent = new HiderFrozenEvent(hider);
                                                game.getServer().getPluginManager().callEvent(hiderFrozenEvent);
                                                isCalledEvent = true;

                                            }
                                    }

                            } else {
                                hider.setFreezeTicks(0L);
                                isCalledEvent = false;
                            }
                    }

                    hider.setPrevLoc(player.getPlayer().getLocation());

            });
            task = new MyRunTask().runTaskLater(game, DELAY);
        }
    }
}
