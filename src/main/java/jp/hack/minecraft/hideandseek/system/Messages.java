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

package jp.hack.minecraft.hideandseek.system;

import org.bukkit.ChatColor;

import java.util.HashMap;

public class Messages {
    private static final Messages _instance = new Messages();
    private final HashMap<String, String> messages = new HashMap<>();
    private final HashMap<String, String> errors = new HashMap<>();

    {
        // この二つはバニラのメッセージと同じなので変えたほうがいいかもしれない
        messages.put("game.you.join", "ゲームに参加しました");
        messages.put("game.other.join", "%sがゲームに参加しました");
        messages.put("game.you.left", "ゲームから退出しました");
        messages.put("game.other.left", "%sがゲームから退出しました");

        messages.put("game.block.notPlayer", "プレイヤーではないようです...");
        messages.put("game.block.gotcha", "プレイヤーを見つけました！！");
        messages.put("game.you.found", "鬼に見つかりました！！");
        messages.put("game.you.runaway", "逃げましょう！");
        messages.put("game.you.cannotHideHere", "ここで隠れることはできません");
        messages.put("game.you.captured", "あなたは捕まりました！");
        messages.put("game.other.captured", "%sは捕まってしまいました！");
        messages.put("game.you.upSpeed", "スピードアップ！");
        messages.put("game.you.coolTime", "クールタイムが明けていません");
        messages.put("game.you.highLight", "プレイヤーの大まかな位置を表示しました");
        messages.put("game.other.highLight", "ハイライト");
        messages.put("game.seeker.release", "鬼が放出された");

        messages.put("error.buy.noMoney", "お金が足りません");

        messages.put("game.start", "ゲーム開始");
        messages.put("game.end", "ゲーム終了");
        messages.put("game.win", "%sの勝利!!!");
        messages.put("game.win.border", "--- %s の勝利 ---");
        messages.put("game.gotMoney", "$%s 獲得しました");

        messages.put("game.gameMode.changed", "ゲームモードが「%s」に変更されました");

        messages.put("stage.created", "ステージ「%s」を作成しました");
        messages.put("stage.set", "「%s」をステージとして設定しました");
        messages.put("stage.deleted", "ステージ「%s」を削除しました");
        messages.put("stage.edited", "ステージ「%s」の「%s」の設定を変更しました");

        errors.put("error.game.alreadyStarted", "すでにゲームは始まっています");
        errors.put("error.game.alreadyJoined", "すでにゲームに参加しています");
        errors.put("error.game.otherAlreadyJoined", "%sはすでにゲームに参加しています");
        errors.put("error.game.notJoined", "あなたはゲームに参加していません");
        errors.put("error.game.notStarted", "まだゲームが始まっていません");
        errors.put("error.game.noData", "%sの設定がありません");
        errors.put("error.game.noEnoughPlayer", "ゲームを開始するのに十分なプレイヤーが参加していません");
        errors.put("error.game.noPlayer", "そのようなプレイヤーはいません");
        errors.put("error.command.notPlayer", "あなたはプレイヤーではないため実行できません");
        errors.put("error.command.illegalArgument","正しい引数を入力してください");
        errors.put("error.command.noEnoughArgument","引数が不足しています");
        errors.put("error.command.tooLongArgument","引数が長すぎます");
        errors.put("error.config.noConfig","設定「%s」が存在しません");
        errors.put("error.config.usableBlock.alreadyExist","ブロック「%s」は既に存在します");
        errors.put("error.config.usableBlock.notFound","ブロック「%s」が見つかりませんでした");
        errors.put("error.stage.none", "ステージが設定されていません");
        errors.put("error.stage.alreadyExist", "「%s」という名前のステージはすでに存在します");
        errors.put("error.stage.notFoundName", "「%s」という名前のステージは存在しません");
        errors.put("error.plugin.missing.vault", " [%s], - Vaultのプラグインあるいは対応するエコノミープラグインがみつかりません！プラグインを無効にします");

        errors.put("error.notExistKey", "存在しないコードのメッセージを取得しようとしました");
    }

    public static String message(String code, Object... args) {
        String s = I18n.tl(code, args);
        if (!s.equals("")) return s;
        return error("error.notExistKey");
    }

    public static String greenMessage(String code, Object... args) {
        String s = I18n.tl(code, args);
        if (!s.equals("")) return ChatColor.GREEN + s + ChatColor.RESET;
        return error("error.notExistKey");
    }

    public static String redMessage(String code, Object... args) {
        String s = I18n.tl(code, args);
        if (!s.equals("")) return ChatColor.RED + s + ChatColor.RESET;
        return error("error.notExistKey");
    }

    public static String error(String code, Object... args) {
        String s = I18n.tl(code, args);
        if (!s.equals("")) return ChatColor.RED + s + ChatColor.RESET;
        return ChatColor.RED + "ERROR MESSAGE NOT FOUND"+ ChatColor.RESET;
    }
}
