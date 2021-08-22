package jp.hack.minecraft.hideandseek.system;

import org.bukkit.ChatColor;

import java.util.HashMap;

public class Messages {
    private static final Messages _instance = new Messages();
    private final HashMap<String, String> messages = new HashMap<>();
    private final HashMap<String, String> errors = new HashMap<>();

    {
        // この二つはバニラのメッセージと同じなので変えたほうがいいかもしれない
        messages.put("game.youJoinGame", "ゲームに参加しました");
        messages.put("game.otherJoinGame", "%sがゲームに参加しました");
        messages.put("game.youCancelGame", "ゲームから退出しました");
        messages.put("game.otherCancelGame", "%sがゲームから退出しました");

        messages.put("game.block.notPlayer", "プレイヤーではないようです...");
        messages.put("game.block.player", "プレイヤーを見つけました！！");
        messages.put("game.you.found", "鬼に見つかりました！！");
        messages.put("game.you.runaway", "逃げましょう！");
        messages.put("game.you.cannotHideHere", "ここで隠れることはできません");
        messages.put("game.you.captured", "あなたは捕まりました！");
        messages.put("game.other.captured", "%sは捕まってしまいました！");
        messages.put("game.you.upSpeed", "スピードアップ！");
        messages.put("game.you.coolTime", "クールタイムが明けていません");
        messages.put("game.you.hiLight", "プレイヤーの大まかな位置を表示しました");
        messages.put("game.other.hiLight", "ハイライト");
        messages.put("game.seeker.release", "鬼が放出された");

        messages.put("buy.noMoney", "お金が足りません");

        messages.put("game.start", "ゲーム開始");
        messages.put("game.end", "ゲーム終了");
        messages.put("game.win", "%sの勝利!!!");
        messages.put("game.win.border", "--- %s の勝利 ---");
        messages.put("game.gotMoney", "$%s 獲得しました");

        messages.put("stage.crated", "ステージ「%s」を作成しました");
        messages.put("stage.set", "「%s」をステージとして設定しました");
        messages.put("stage.deleted", "ステージ「%s」を削除しました");
        messages.put("stage.edited", "ステージ「%s」の「%s」の設定を変更しました");

        errors.put("game.alreadyStarted", "すでにゲームは始まっています");
        errors.put("game.alreadyJoined", "すでにゲームに参加しています");
        errors.put("game.notJoined", "あなたはゲームに参加していません");
        errors.put("game.notStarted", "まだゲームが始まっていません");
        errors.put("game.noData", "%sの設定がありません");
        errors.put("game.notEnoughPlayer", "ゲームを開始するのに十分なプレイヤーが参加していません");
        errors.put("game.noPlayer", "そのようなプレイヤーはいません");
        errors.put("command.notPlayer", "あなたはプレイヤーではないため実行できません");
        errors.put("command.illegalArgument","正しい引数を入力してください");
        errors.put("command.tooLongArgument","引数が長すぎます");
        errors.put("config.noSetting","設定「%s」が存在しません");
        errors.put("stage.none", "ステージが設定されていません");
        errors.put("stage.alreadyExist", "「%s」という名前のステージはすでに存在します");
        errors.put("stage.notFoundName", "「%s」という名前のステージは存在しません");

        errors.put("error.notExistKey", "存在しないコードのメッセージを取得しようとしました");
    }

    public static String message(String code, Object... args) {
        if (_instance.messages.containsKey(code)) return String.format(_instance.messages.get(code), args);
        return error("error.notExistKey");
    }

    public static String greenMessage(String code, Object... args) {
        if (_instance.messages.containsKey(code)) return ChatColor.GREEN + String.format(_instance.messages.get(code), args) + ChatColor.RESET;
        return error("error.notExistKey");
    }

    public static String redMessage(String code, Object... args) {
        if (_instance.messages.containsKey(code)) return ChatColor.RED + String.format(_instance.messages.get(code), args) + ChatColor.RESET;
        return error("error.notExistKey");
    }

    public static String error(String code, Object... args) {
        if (_instance.errors.containsKey(code)) return ChatColor.RED + String.format(_instance.errors.get(code), args) + ChatColor.RESET;
        return error("error.notExistKey");
    }
}
