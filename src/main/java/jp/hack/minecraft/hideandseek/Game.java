package jp.hack.minecraft.hideandseek;

import jp.hack.minecraft.hideandseek.event.*;
import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.hideandseek.HideAndSeekCommand;
import jp.hack.minecraft.hideandseek.player.*;
import jp.hack.minecraft.hideandseek.data.*;
import jp.hack.minecraft.hideandseek.system.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
    private GameState currentState;
    private GameLogic gameLogic;
    private ConfigLoader configLoader;
    private StageData stageData;
    private CommandManager commandManager;
    private static Economy econ = null;
    private final EventWatcher eventWatcher = new EventWatcher(this);
    private final EventManager eventManager = new EventManager(this);
    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();
    private BukkitTask seekerTeleportTimer;

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
    }

    public void start() {
        stageData = new StageData((Location) configLoader.getData("location.stage"), configLoader.getInt("borderRadius"));
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

        Location stage = (Location) configLoader.getData("location.stage");
        getHiders().forEach(hider -> hider.getPlayer().teleport(stage));

        Location seekerLobby = (Location) configLoader.getData("location.seekerLobby");
        getSeekers().forEach(seeker -> seeker.getPlayer().teleport(seekerLobby));

        int seekerWaitTime = configLoader.getInt("seekerWaitTime");
        seekerTeleportTimer = new BukkitRunnable() {
            int seekerWaitRemainTime = seekerWaitTime;

            @Override
            public void run() {
                if (seekerWaitRemainTime > 0) {
                    getSeekers().forEach(seeker -> seeker.getPlayer().sendMessage("残り " + seekerWaitRemainTime + "秒")); // 経験値バーの利用? Messagesクラスへの移植?
                    getHiders().forEach(hider -> hider.getPlayer().sendMessage("残り " + seekerWaitRemainTime + "秒で鬼が放出されます")); // 経験値バーの利用? Messagesクラスへの移植?
                    seekerWaitRemainTime -= 1;
                } else {
                    getSeekers().forEach(seeker -> seeker.getPlayer().teleport(stage));
                    getHiders().forEach(hider -> hider.getPlayer().sendMessage("鬼が放出されました")); // 経験値バーの利用? Messagesクラスへの移植? Titleの利用?
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 20, 20);
    }

    public void stop() {
        gamePlayers.clear();

        currentState = GameState.LOBBY;
        seekerTeleportTimer.cancel();
        stageData.deleteBorder();
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
        Location lobby = (Location) configLoader.getData("location.lobby"); // nullの場合の場合分けが必要
        player.teleport(lobby);
        gamePlayers.put(player.getUniqueId(), new LobbyPlayer(player));
        // 初期化処理、ゲーム終了後にも呼ぶのでどこかで関数にするほうがいいかもしれない。LobbyPlayerのなか?
        player.setGameMode(GameMode.SURVIVAL);
        player.setInvisible(false);
        // ここでPlayerにメッセージなどを送信
    }

    public void damageHider(Hider hider) {
        hider.damage(attackDamage);
    }

    public Hider findHiderByBlock(Block block) {
        return getHiders().stream().filter(p -> p.getBlock().equals(block)).findFirst().orElse(null);
    }

    public Hider findHiderByFallingBlock(FallingBlock fallingBlock) {
        return getHiders().stream().filter(p -> p.getFallingBlock().equals(fallingBlock)).findFirst().orElse(null);
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
}