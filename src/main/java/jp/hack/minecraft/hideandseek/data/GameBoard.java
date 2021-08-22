package jp.hack.minecraft.hideandseek.data;


import jp.hack.minecraft.hideandseek.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameBoard {
    private final GamePlayer gamePlayer;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final List<String> keyList = new ArrayList<>(Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));

    public GameBoard(GamePlayer gamePlayer, String name) {
        this.gamePlayer = gamePlayer;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective(name, "dummy", name);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setRenderType(RenderType.INTEGER);
        gamePlayer.getPlayer().setScoreboard(scoreboard);
        reloadScoreboard();
    }

//    private void initScoreboard() {
//        for (int i = 0; i < 15; i++) {
//            scoreboard.resetScores(keyList.get(i));
//            objective.getScore(keyList.get(i)).setScore(15 - i);
//        }
//    }

    private void reloadScoreboard() {
        for (int i = 0; i < 15; i++) {
            scoreboard.resetScores(keyList.get(i));
            objective.getScore(keyList.get(i)).setScore(15 - i);
        }
    }

    public Objective getObjective() {
        return objective;
    }

    public void setText(int index, String text) {
        if (index >= 14) return;
        scoreboard.resetScores(keyList.get(index));
        keyList.set(index, text);
        objective.getScore(text).setScore(index);
        reloadScoreboard();
    }

    public void resetText() {
        for (int i = 0; i < 15; i++){
            setText(i, "");
        }
        reloadScoreboard();
    }
}
