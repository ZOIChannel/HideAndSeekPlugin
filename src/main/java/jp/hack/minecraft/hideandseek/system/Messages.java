package jp.hack.minecraft.hideandseek.system;

import org.bukkit.ChatColor;

import java.util.HashMap;

public class Messages {
    private static final Messages _instance = new Messages();
    private final HashMap<String, String> messages = new HashMap<>();
    private final HashMap<String, String> errors = new HashMap<>();

    {
        // この二つはバニラのメッセージと同じなので変えたほうがいいかもしれない
        messages.put("you.joinGame", "ゲームに参加しました");
        messages.put("other.joinGame", "%sがゲームに参加しました");

        messages.put("you.found", "あなたは見つかってしまいました");

        errors.put("game.alreadyStarted", "すでにゲームは始まっています");
        errors.put("game.noData", "%sの設定がありません");
        errors.put("game.noPlayers", "プレイヤーがいません");
        errors.put("command.illegalArgument","正しい引数を入力してください");
        errors.put("messages.notExistKey", "存在しないコードのメッセージを取得しようとしました");
    }

    public static String message(String code, Object... args) {
        if (_instance.messages.containsKey(code)) return ChatColor.GREEN + String.format(_instance.messages.get(code), args);
        return error("messages.error.notExistKey");
    }

    public static String error(String code, Object... args) {
        if (_instance.errors.containsKey(code)) return ChatColor.RED + String.format(_instance.errors.get(code), args);
        return error("messages.error.notExistKey");
    }
}
