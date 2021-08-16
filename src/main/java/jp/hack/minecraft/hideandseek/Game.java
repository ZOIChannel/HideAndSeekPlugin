package jp.hack.minecraft.hideandseek;

import jp.hack.minecraft.hideandseek.event.*;
import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.hideandseek.HideAndSeekCommand;
import jp.hack.minecraft.hideandseek.player.*;
import jp.hack.minecraft.hideandseek.data.*;
import jp.hack.minecraft.hideandseek.system.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public final class Game extends JavaPlugin {

    private List<GamePlayer> playerList;
    private GameState currentState = GameState.LOBBY;
    private GameLogic gameLogic;
    private ConfigLoader configLoader;
    private int currentStageIndex = 0;
    private List<StageData> stageList = new ArrayList<>();
    private CommandManager commandManager;
    private static Economy econ = null;
    private final EventWatcher eventWatcher = new EventWatcher(this);
    private final EventManager eventManager = new EventManager(this);
    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();
    private BukkitTask seekerTeleportTimer;
    private BukkitTask gameOverTimer;

    private Integer attackDamage;
    private final int DEF_ATTACK_DAMAGE = 4;


    @Override
    public void onEnable() {
        // Plugin startup logic
        super.onEnable();
        getServer().getPluginManager().registerEvents(eventManager, this);

        if (!setupEconomy()) {
            getServer().getLogger().info("Vault plugin is not found.");
        }
        commandManager = new CommandManager(this);
        commandManager.addRootCommand(new HideAndSeekCommand(commandManager)); // plugin.ymlへの登録を忘れずに

        configLoader = new ConfigLoader(this);
        eventWatcher.start();
        initializeConst();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        eventWatcher.stop();
        super.onDisable();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    private void initializeConst() {
        attackDamage = configLoader.getInt("attackDamage");
        if (attackDamage == null) attackDamage = DEF_ATTACK_DAMAGE;
        configLoader.setData("attackDamage", attackDamage);
        if (!(configLoader.getData("stage") instanceof List)
                || ((List<?>) configLoader.getData("stage")).stream().noneMatch(Objects::nonNull)) {
            stageList = new ArrayList<>();
            configLoader.setData("stage", stageList);
        } else {
            stageList = (ArrayList<StageData>) configLoader.getData("stage");
        }
    }

    public void start() {
        StageData stageData = getCurrentStage();
        if(stageData == null) {
            Bukkit.getLogger().info("ステージが存在しない");// プレイヤーへのメッセージに変更
            return;
        }
        if(!stageData.isInitialized()){
            Bukkit.getLogger().info("stageの設定が不十分");// プレイヤーへのメッセージに変更
            return;
        }
        // この時点でConfigに値が設定されていなければエラーを出し、処理を中断する
        currentState = GameState.PLAYING;
        int seekerRate = configLoader.getInt("seekerRate"); // nullの場合の場合分けが必要
        int seekerCount = (int) Math.ceil(gamePlayers.size() / (float) seekerRate);
        List<Integer> randomOrderList = new ArrayList<>();
        for (int i = 0; i < gamePlayers.size(); i++) {
            randomOrderList.add(i);
        }
        Collections.shuffle(randomOrderList);
        List<Integer> seekerIndex = randomOrderList.subList(0, seekerCount);
        List<LobbyPlayer> lobbyPlayers = gamePlayers.values().stream()
                .map(gamePlayer -> (LobbyPlayer) gamePlayer)
                .collect(Collectors.toList());
        for (int i = 0; i < lobbyPlayers.size(); i++) {
            LobbyPlayer lobbyPlayer = lobbyPlayers.get(i);
            if (seekerIndex.contains(i)) {
                lobbyPlayer.createSeeker(gamePlayers);
            } else {
                lobbyPlayer.createHider(gamePlayers);
            }
        }
        // ここでPlayerにメッセージなどを送信
        gamePlayers.values().forEach(gamePlayer -> {
            Player player = gamePlayer.getPlayer();
            player.sendTitle("ゲーム開始", "", 10, 20, 10);
        });

        stageData.createBorder();

        Location stage = getCurrentStage().getStage();
        getHiders().forEach(hider -> hider.getPlayer().teleport(stage));

        Location seekerLobby = getCurrentStage().getSeekerLobby();
        getSeekers().forEach(seeker -> seeker.getPlayer().teleport(seekerLobby));

        int gameTime = configLoader.getInt("gameTime");
        int seekerWaitTime = configLoader.getInt("seekerWaitTime");
        Game game = this;
        BukkitRunnable gameOverRunnable = new BukkitRunnable() {
            int gameRemainTime = gameTime;

            @Override
            public void run() {
                if (gameRemainTime > 0) {
                    getGamePlayers().forEach((uuid, gamePlayer) -> gamePlayer.getPlayer().sendMessage("残り " + gameRemainTime + "秒")); // 経験値バーの利用? Messagesクラスへの移植?
                    gameRemainTime -= 1;
                } else {
                    getGamePlayers().forEach((uuid, gamePlayer) -> gamePlayer.getPlayer().sendMessage("ゲーム終了です")); // 経験値バーの利用? Messagesクラスへの移植? Titleの利用?
                    game.gameOver();
                    this.cancel();
                }
            }
        };
        seekerTeleportTimer = new BukkitRunnable() {
            int seekerWaitRemainTime = seekerWaitTime;

            @Override
            public void run() {
                if (seekerWaitRemainTime > 0) {
                    getGamePlayers().forEach((uuid, gamePlayer) -> gamePlayer.getPlayer().setLevel(seekerWaitRemainTime));
                    getSeekers().forEach(seeker -> seeker.getPlayer().sendMessage("残り " + seekerWaitRemainTime + "秒")); // 経験値バーの利用? Messagesクラスへの移植?
                    getHiders().forEach(hider -> hider.getPlayer().sendMessage("残り " + seekerWaitRemainTime + "秒で鬼が放出されます")); // 経験値バーの利用? Messagesクラスへの移植?
                    seekerWaitRemainTime -= 1;
                } else {
                    Objects.requireNonNull(stage.getWorld()).playSound(stage, Sound.ENTITY_ENDER_DRAGON_AMBIENT, SoundCategory.AMBIENT, 1, 1);
                    getSeekers().forEach(seeker -> seeker.getPlayer().teleport(stage));
                    getHiders().forEach(hider -> hider.getPlayer().sendTitle("鬼が放出された", "", 20, 40, 20)); // Messagesクラスへの移植?
                    gameOverTimer = gameOverRunnable.runTaskTimer(game, 20, 20);
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 20, 20);
    }

    public void gameOver() {
        String wonRole = judge();
        getGamePlayers().forEach((uuid, gamePlayer) -> gamePlayer.getPlayer().sendTitle(wonRole + "の勝利!!!", "", 20, 20, 20));
        stop();
    }

    private String judge() {
        if (gamePlayers.values().stream().anyMatch(gamePlayer -> {
            if (!gamePlayer.isHider()) return false;
            Hider hider = (Hider) gamePlayer;
            return !hider.isDead();
        })) return "Hider";
        else return "Seeker";
    }

    public void stop() {
        // プレイヤーをどこかへTPさせる?
        gamePlayers.forEach((uuid, gamePlayer) -> gamePlayer.getPlayer().setGameMode(GameMode.SURVIVAL));
        gamePlayers.clear();

        currentState = GameState.LOBBY;
        seekerTeleportTimer.cancel();
        gameOverTimer.cancel();
        getCurrentStage().deleteBorder();
    }

    public EventWatcher getEventWatcher() {
        return eventWatcher;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public List<StageData> getStageList() {
        return stageList;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public Map<UUID, GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public GamePlayer getGamePlayer(UUID uuid) {
        return gamePlayers.get(uuid);
    }

    public List<Hider> getHiders() {
        return gamePlayers.values().stream().filter(GamePlayer::isHider).map(p -> (Hider) p).collect(Collectors.toList());
    }

    public List<Seeker> getSeekers() {
        return gamePlayers.values().stream().filter(GamePlayer::isSeeker).map(p -> (Seeker) p).collect(Collectors.toList());
    }

    // gamePlayersへのSeekerのputはLobbyPlayerから行う
    public Seeker createSeeker(Player player) {
        Seeker seeker = new Seeker(player);
        gamePlayers.put(seeker.getPlayerUuid(), seeker);
        return seeker;
    }

    // 仮コード・削除予定。gamePlayersへのHiderのputはLobbyPlayerから行う
    public Hider createHider(Player player) {
        Hider hider = new Hider(player);
        gamePlayers.put(hider.getPlayerUuid(), hider);
        return hider;
    }

    public void join(Player player) {
        if (gamePlayers.containsKey(player.getUniqueId())) {
            player.sendMessage(Messages.error("game.alreadyJoined"));
            return;
        }
        Location lobby = getCurrentStage().getLobby(); // nullの場合の場合分けが必要
        player.teleport(lobby);
        gamePlayers.put(player.getUniqueId(), new LobbyPlayer(player));
        // 初期化処理、ゲーム終了後にも呼ぶのでどこかで関数にするほうがいいかもしれない。LobbyPlayerのなか?
        player.setGameMode(GameMode.SURVIVAL);
        player.setInvisible(false);
        // ここでPlayerにメッセージなどを送信
    }

    public void cancel(Player player) {
        if (!gamePlayers.containsKey(player.getUniqueId())) {
            player.sendMessage(Messages.error("game.notJoined"));
            return;
        }
        // playerをどこか(スポーン地?)へTPさせるべきかもしれない

        gamePlayers.remove(player.getUniqueId());
        // 初期化処理、ゲーム終了後にも呼ぶのでどこかで関数にするほうがいいかもしれない。LobbyPlayerのなか?
        player.setGameMode(GameMode.SURVIVAL);
        player.setInvisible(false);
        // ここでPlayerにメッセージなどを送信
    }

    public void damageHider(Hider hider) {
        Bukkit.getLogger().info("damage!!!");
        hider.damage(attackDamage);
    }

    public Hider findHiderByBlock(Block block) {
        if (block == null) return null;
        return getHiders().stream().filter(h -> h.getBlock().equals(block)).findAny().orElse(null);
    }

    public Hider findHiderByFallingBlock(FallingBlock fallingBlock) {
        if (fallingBlock == null) return null;
        return getHiders().stream().filter(h -> h.getFallingBlock().equals(fallingBlock)).findAny().orElse(null);
    }

    public Boolean isSameLocation(Location loc1, Location loc2) {
        return (loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ());
    }

    public Boolean isSameBlockLocation(Location loc1, Location loc2) {
        return (loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ());
    }

    public Boolean isDistant(Location loc1, Location loc2) {
        return (loc1.distance(loc2) > 0.7d);
    }

    public StageData getCurrentStage() {
        if (stageList.size() == 0) return null;
        return stageList.get(currentStageIndex);
    }

    public void selectNextStage() {
        System.out.println(currentStageIndex);
        currentStageIndex++;
        System.out.println(currentStageIndex);
        if (currentStageIndex >= stageList.size()) currentStageIndex = 0;
        System.out.println(currentStageIndex);
    }

    public StageData createNewStage(String name) {
        Optional<StageData> optionalStageData = stageList.stream().filter(stageData -> stageData.getName().equals(name)).findFirst();
        if(optionalStageData.isPresent()) return null; // この二行、普通にstreamとif使って一行で完結させてもいいかも
        StageData stageData = new StageData(name);
        stageList.add(stageData);
        System.out.println(stageList);
        return stageData;
    }
}