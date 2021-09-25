package jp.hack.minecraft.hideandseek.player;

import jp.hack.minecraft.hideandseek.data.EffectType;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class Seeker extends GamePlayer {
    private int countHiLight = 0;
    private boolean isOriginal;

    public int getCountHiLight() {
        return countHiLight;
    }

    public void addCountHiLight() {
        countHiLight++;
    }

    public Seeker(Player player, boolean isOriginal) {
        super(player);
        this.isOriginal = isOriginal;
        player.setPlayerListName(ChatColor.RED + getPlayer().getName() + ChatColor.RESET);
        player.setInvisible(false);
        giveEffect(EffectType.SEEKER_UP_SPEED);
    }

    public Seeker(Player player) {
        this(player, true);
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void knock(Location loc) {
        System.out.println("----- event:005 -----");
        sendRedMessage("game.block.notPlayer");
        getPlayer().playSound(loc, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0F, 1.0F);
    }

    public void gotcha() {
        System.out.println("----- event:002 -----");
        sendGreenMessage("game.block.gotchaPlayer");
        sendRedTitle(2, 20, 2, "game.block.gotcha", "");
        getPlayer().playSound(getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 0.5F, 1.0F);
    }

    public void hiLight(Boolean boo) {
        if (boo) {
            sendGreenMessage("game.you.highLight");
            getPlayer().playSound(getLocation(), Sound.AMBIENT_CAVE, 1.1F, 1.0F);
            addCountHiLight();
        } else {
            sendRedMessage("game.you.coolTime");
        }
    };
}
