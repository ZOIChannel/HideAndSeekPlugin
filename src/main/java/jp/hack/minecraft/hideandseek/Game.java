package jp.hack.minecraft.hideandseek;

import jp.hack.minecraft.hideandseek.event.*;
import jp.hack.minecraft.hideandseek.command.CommandManager;
import jp.hack.minecraft.hideandseek.command.hideandseek.HideAndSeekCommand;
import jp.hack.minecraft.hideandseek.player.*;
import jp.hack.minecraft.hideandseek.data.*;
import jp.hack.minecraft.hideandseek.system.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public final class Game extends JavaPlugin {

    //    private List<GamePlayer> playerList;
    private GameState currentState = GameState.LOBBY;
    private ConfigLoader configLoader;
    private int currentStageIndex = 0;
    private List<StageData> stageList = new ArrayList<>();
    private CommandManager commandManager;
    private static Economy econ = null;
    private final EventWatcher eventWatcher = new EventWatcher(this);
    private final EventManager eventManager = new EventManager(this);
    private final Map<UUID, BlockGui> blockGuiList = new HashMap<>();
    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();
    private final TimeBar timeBar = new TimeBar();
    private BukkitTask seekerTeleportTimer;
    private BukkitTask gameOverTimer;
    private boolean bStop = false;

    private final Material captureType = Material.IRON_AXE;
    private final Material speedType = Material.FEATHER;
    private final Material hiLightType = Material.CLOCK;

    private final int DEFAULT_GAME_TIME = 60;
    private int gameTime;
    private final int DEFAULT_SEEKER_WAIT_TIME = 10;
    private int seekerWaitTime;
    private final Double DEFAULT_REWARD = 60d;
    private Double reward;
    private final List<UsableBlock> DEFAULT_USABLE_BLOCKS = Arrays.asList(
            new UsableBlock(Material.CRAFTING_TABLE, 0),
            new UsableBlock(Material.FURNACE, 0),
            new UsableBlock(Material.BOOKSHELF, 0),
            new UsableBlock(Material.MELON, 0),

            new UsableBlock(Material.FLOWER_POT, 20),
            new UsableBlock(Material.ANVIL, 20),
            new UsableBlock(Material.JUKEBOX, 20),

            new UsableBlock(Material.STONE_BRICKS, 40),
            new UsableBlock(Material.DIRT, 40),

            new UsableBlock(Material.OAK_PLANKS, 60),
            new UsableBlock(Material.STONE, 60),

            new UsableBlock(Material.GRASS_BLOCK, 120)
    );
    private List<UsableBlock> usableBlocks;

    private final Map<UUID, DummyArmorStand> armorStands = new HashMap<>();

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

    public List<StageData> getStageList() {
        return stageList;
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

    public Material getCaptureType() {
        return captureType;
    }

    public Material getSpeedType() {
        return speedType;
    }

    public Material getHiLightType() {
        return hiLightType;
    }

    public List<UsableBlock> getUsableBlocks() {
        return usableBlocks;
    }

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
        initializeConst();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        super.onDisable();
        stop();
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
        if (!(configLoader.getData("stage") instanceof List)
                || ((List<?>) configLoader.getData("stage")).stream().noneMatch(Objects::nonNull)) {
            stageList = new ArrayList<>();
            configLoader.setData("stage", stageList);
        } else {
            stageList = (ArrayList<StageData>) configLoader.getData("stage");
        }

        if (configLoader.contains("gameTime")) {
            gameTime = configLoader.getInt("gameTime");
        } else {
            gameTime = DEFAULT_GAME_TIME;
            configLoader.setData("gameTime", gameTime);
        }

        if (configLoader.contains("seekerWaitTime")) {
            seekerWaitTime = configLoader.getInt("seekerWaitTime");
        } else {
            seekerWaitTime = DEFAULT_SEEKER_WAIT_TIME;
            configLoader.setData("seekerWaitTime", seekerWaitTime);
        }

        if (configLoader.contains("reward")) {
            reward = configLoader.getDouble("reward");
        } else {
            reward = DEFAULT_REWARD;
            configLoader.setData("reward", reward);
        }

        if (configLoader.contains("usableBlocks")) {
            usableBlocks = configLoader.getUsableBlocks();
            if (usableBlocks.size() == 0) {
                usableBlocks = DEFAULT_USABLE_BLOCKS;
                configLoader.setData("usableBlocks", usableBlocks);
            }
        } else {
            usableBlocks = DEFAULT_USABLE_BLOCKS;
            configLoader.setData("usableBlocks", usableBlocks);
        }

    }

    public void start() {
        bStop = false;
        destroyAllDummy();
        StageData stageData = getCurrentStage();
        if (stageData == null) {
            this.gamePlayers.values().forEach(gamePlayer -> {
                if (!gamePlayer.getPlayer().hasPermission("op")) return;
                gamePlayer.getPlayer().sendMessage("ステージが存在しません。設定をしてください。");
            });
            return;
        }
        if (!stageData.isInitialized()) {
            this.gamePlayers.values().forEach(gamePlayer -> {
                if (!gamePlayer.getPlayer().hasPermission("op")) return;
                gamePlayer.getPlayer().sendMessage("ステージの設定が不十分です。設定をしてください。");
            });
            return;
        }

        eventWatcher.start();
        currentState = GameState.PLAYING;
        int seekerRate = configLoader.getInt("seekerRate");
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
                lobbyPlayer.createSeeker(this);
            } else {
                lobbyPlayer.createHider(this);
            }
        }
        gamePlayers.values().forEach(gamePlayer -> {
            gamePlayer.sendTitle(10, 20, 10, "game.start", "");
            timeBar.addPlayer(gamePlayer.getPlayer());
            timeBar.setVisible(true);
        });
        reloadScoreboard();

        stageData.createBorder();

        Location stage = getCurrentStage().getStage();
        getHiders().forEach(hider -> hider.getPlayer().teleport(stage));

        Location seekerLobby = getCurrentStage().getSeekerLobby();
        getSeekers().forEach(seeker -> {
            seeker.getPlayer().teleport(seekerLobby);
//            {
//                ItemStack item = new ItemStack(Material.CARROT_ON_A_STICK);
//                ItemMeta meta = item.getItemMeta();
//                meta.setDisplayName("Select Block");
//                item.setItemMeta(meta);
//                seeker.getPlayer().getInventory().addItem(item);
//            }
        });

        new Thread(new BukkitRunnable() {
            int seekerWaitRemainTime = seekerWaitTime;

            @Override
            public void run() {
                long t = System.currentTimeMillis();
                while (!bStop && seekerWaitRemainTime > 0) {
                    long d = System.currentTimeMillis() - t;
                    if (d > 1000) {
                        //getGamePlayers().values().forEach(gamePlayer -> gamePlayer.getPlayer().setLevel(seekerWaitRemainTime));
                        seekerWaitRemainTime -= 1;
                        timeBar.setProgress((float) seekerWaitRemainTime / (float) seekerWaitTime);
                        t = System.currentTimeMillis();
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!bStop) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Objects.requireNonNull(stage.getWorld()).playSound(stage, Sound.ENTITY_ENDER_DRAGON_AMBIENT, SoundCategory.AMBIENT, 1, 1);
                            getSeekers().forEach(seeker -> seeker.getPlayer().teleport(stage));
                            allSendRedTitle(20, 40, 20, "game.seeker.release", "");
                            equipItems();
                        }
                    }.runTask(Game.this);
                    new Thread(new BukkitRunnable() {
                        int gameRemainTime = gameTime;

                        @Override
                        public void run() {
                            long t = System.currentTimeMillis();
                            while (!bStop && gameRemainTime > 0) {
                                long d = System.currentTimeMillis() - t;
                                if (d > 1000) {
                                    //getGamePlayers().values().forEach(gamePlayer -> gamePlayer.getPlayer().setLevel(gameRemainTime));
                                    gameRemainTime -= 1;
                                    timeBar.setProgress((float) gameRemainTime / (float) gameTime);
                                    t = System.currentTimeMillis();
                                }
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!bStop) new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Game.this.gameOver();
                                }
                            }.runTask(Game.this);
                        }
                    }).start();
                }
            }
        }).start();
    }

    public void gameOver() {
        makeAliveHiderDummy();

        allSendGreenMessage("game.end");
        Role wonRole = judge();
        allSendGreenTitle(20, 40, 20, "game.win", wonRole.toString());
        allSendGreenMessage("game.win.border", wonRole.toString());

        timeBar.setVisible(false);
        giveReward(wonRole);
        reloadScoreboard();

        if (wonRole.equals(Role.HIDER)) {
            List<String> livingHiders = new ArrayList<>();
            getGamePlayers().values().stream().filter(GamePlayer::isHider)
                    .filter(hider -> !((Hider) hider).isDead())
                    .forEach(hider -> livingHiders.add(hider.getPlayer().getDisplayName()));
            getGamePlayers().forEach((uuid, gamePlayer) -> {
                gamePlayer.getPlayer().sendMessage(ChatColor.GREEN + "生存者:" + ChatColor.RESET);
                livingHiders.forEach(name -> {
                    gamePlayer.getPlayer().sendMessage(ChatColor.GREEN + "    " + name + ChatColor.RESET);
                });
                gamePlayer.getPlayer().sendMessage(ChatColor.GREEN + "-----------" + ChatColor.RESET);
            });
        }
        stop();
    }

    private Role judge() {
        if (gamePlayers.values().stream().anyMatch(gamePlayer -> {
            if (!gamePlayer.isHider()) return false;
            Hider hider = (Hider) gamePlayer;
            return !hider.isDead();
        })) return Role.HIDER;
        else return Role.SEEKER;
    }

    public void stop() {
        // プレイヤーをどこかへTPさせる?
        destroyGamePlayers();
        clearHiLightTask();
        currentState = GameState.LOBBY;
        if (getCurrentStage() != null)
            getCurrentStage().deleteBorder();
        eventWatcher.stop();
        bStop = true;
        reloadScoreboard();
    }

//    // gamePlayersへのSeekerのputはLobbyPlayerから行う
//    public Seeker createSeeker(Player player) {
//        Seeker seeker = new Seeker(player);
//        gamePlayers.put(seeker.getPlayerUuid(), seeker);
//        return seeker;
//    }
//
//    // 仮コード・削除予定。gamePlayersへのHiderのputはLobbyPlayerから行う
//    public Hider createHider(Player player) {
//        Hider hider = new Hider(player);
//        gamePlayers.put(hider.getPlayerUuid(), hider);
//        return hider;
//    }

    private void destroyGamePlayer(GamePlayer gamePlayer) {
        clearPlayerEffect(gamePlayer);
        Player player = gamePlayer.getPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        player.teleport(getCurrentStage().getStage());
        if (gamePlayer.isHider()) {
            Hider hider = (Hider) gamePlayer;
            hider.destroy();
        }
        blockGuiList.remove(player.getUniqueId());
    }

    public void destroyOneGamePlayer(GamePlayer gamePlayer) {
        if (gamePlayer == null) return;
        destroyGamePlayer(gamePlayer);
        getGamePlayers().remove(gamePlayer.getPlayerUuid());
    }

    private void destroyGamePlayers() {
        if (gamePlayers.isEmpty()) return;
        gamePlayers.values().forEach(this::destroyGamePlayer);
        gamePlayers.clear();
    }

    public void join(Player player) {
        if (gamePlayers.containsKey(player.getUniqueId())) {
            player.sendMessage(Messages.error("game.alreadyJoined"));
            return;
        }
        Location lobby = getCurrentStage().getLobby();
        if (lobby == null) {
            gamePlayers.values().forEach(gamePlayer -> gamePlayer.getPlayer().sendMessage(Messages.error("stage.none")));
            return;
        }
        player.teleport(lobby);
        gamePlayers.put(player.getUniqueId(), new LobbyPlayer(player));
        reloadScoreboard();
        // 初期化処理、ゲーム終了後にも呼ぶのでどこかで関数にするほうがいいかもしれない。LobbyPlayerのなか?
        resetPlayerState(player);
        gamePlayers.values().forEach(gamePlayer -> {
            if (gamePlayer.getPlayerUuid() == player.getUniqueId()) {
                gamePlayer.sendGreenMessage("game.youJoinGame");
                return;
            }
            gamePlayer.sendGreenMessage("game.otherJoinGame", player.getDisplayName());
        });
    }

    public void cancel(Player player) {
        if (!gamePlayers.containsKey(player.getUniqueId())) {
            player.sendMessage(Messages.error("game.notJoined"));
            return;
        }
        player.setPlayerListName(player.getDisplayName());
//        allSendGreenMessage("game.youCancelGame");
        gamePlayers.values().forEach(gamePlayer -> {
            if (gamePlayer.getPlayerUuid() == player.getUniqueId()) {
                gamePlayer.sendGreenMessage("game.youCancelGame");
                return;
            }
            gamePlayer.sendGreenMessage("game.otherCancelGame", player.getDisplayName());
        });
        gamePlayers.remove(player.getUniqueId());
        // 初期化処理、ゲーム終了後にも呼ぶのでどこかで関数にするほうがいいかもしれない。LobbyPlayerのなか?
        resetPlayerState(player);
    }

    public void resetPlayerState(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setInvisible(false);
    }

    public void damageHider(Hider hider) {
        if (hider.isDead()) return;

        gamePlayers.values().forEach(gamePlayer -> {
            if (hider.getPlayerUuid() == gamePlayer.getPlayerUuid()) {
                gamePlayer.sendRedMessage("game.you.captured", hider.getPlayer().getDisplayName());
                gamePlayer.sendRedTitle(10, 40, 10, "game.you.captured", hider.getPlayer().getDisplayName());
                return;
            }
            gamePlayer.sendGreenMessage("game.other.captured", hider.getPlayer().getDisplayName());
        });
        clearPlayerEffect(hider);
        if (armorStands.containsKey(hider.getPlayerUuid())) {
            armorStands.get(hider.getPlayerUuid()).destroy();
        }
        hider.damage();
        hider.getPlayer().teleport(getCurrentStage().getStage());
        reloadScoreboard();
        confirmGame();
    }

    public void confirmGame() {
        if (isHidersDied()) {
            gameOver();
            return;
        }
        if (getSeekers().isEmpty()) {
            gameOver();
        }
    }

    public boolean isHidersDied() {
        return getHiders().stream().allMatch(Hider::isDead);
    }

    public List<UsableBlock> getPlayerUsableBlocks(Player player) {
        if (econ == null) return usableBlocks;
        System.out.println(usableBlocks);
        double balance = econ.getBalance(player);
        return usableBlocks.stream().filter(v -> v.getPrice() <= balance).collect(Collectors.toList());
    }

    public void giveBounty(GamePlayer gamePlayer) {
        System.out.println("giveBounty");
        if (econ == null) return;
        System.out.println("econ: notNull");
        giveMoney(gamePlayer, reward / 10);
    }

    public void giveReward(Role wonRole) {
        System.out.println("giveReward");
        if (econ == null) return;
        System.out.println("econ: notNull");
        gamePlayers.values().forEach(gamePlayer -> {
            if (gamePlayer.isSameRole(wonRole)) {
                giveMoney(gamePlayer, reward);
            } else {
                giveMoney(gamePlayer, reward / 3);
            }
        });
    }

    public void giveMoney(GamePlayer gamePlayer, double amount) {
        econ.depositPlayer(gamePlayer.getPlayer(), amount);
        gamePlayer.sendGreenMessage("game.gotMoney", amount);
    }

    private Boolean isHiLight = false;
    private MyRunnable myRunnable;

    public Boolean playerHiLight(EffectType type) {
        if (isHiLight) return false;
        makeAliveHiderDummy();
        allSendRedTitle(5, 20, 5, "game.other.hiLight");

        myRunnable = new MyRunnable() {
            @Override
            public void run() {
                destroyAllDummy();
                setRunnable(new BukkitRunnable() {
                    @Override
                    public void run() {
                        isHiLight = false;
                    }
                });
                getRunnable().runTaskLater(Game.this, type.getCoolTime() * 20L);
            }
        };
        isHiLight = true;
        myRunnable.runTaskLater(Game.this, type.getDuration() * 20L);
        return true;
    }

    public Boolean givePlayerEffect(GamePlayer gamePlayer, EffectType type) {
        Map<EffectType, MyRunnable> map = gamePlayer.getEffectMap();
        if (map.containsKey(type)) return false;
        gamePlayer.giveEffect(type);

        final MyRunnable myRunnable = new MyRunnable() {
            @Override
            public void run() {
                gamePlayer.clearEffect(type);
                setRunnable(new BukkitRunnable() {
                    @Override
                    public void run() {
                        map.remove(type);
                    }
                });
                getRunnable().runTaskLater(Game.this, type.getCoolTime() * 20L);
            }

            ;
        };
        map.put(type, myRunnable);
        myRunnable.runTaskLater(Game.this, type.getDuration() * 20L);
        return true;
    }

    public abstract static class MyRunnable extends BukkitRunnable {
        private BukkitRunnable runnable;

        public BukkitRunnable getRunnable() {
            return runnable;
        }

        public void setRunnable(BukkitRunnable runnable) {
            this.runnable = runnable;
        }
    }

    public void clearHiLightTask() {
        if (myRunnable != null) {
            if (!myRunnable.isCancelled()) myRunnable.cancel();
            if (myRunnable.getRunnable() != null)
                if (!myRunnable.getRunnable().isCancelled()) myRunnable.getRunnable().cancel();
        }
        isHiLight = false;
        destroyAllDummy();
    }

    public void clearPlayerEffect(GamePlayer gamePlayer) {
        Map<EffectType, MyRunnable> map = gamePlayer.getEffectMap();
        gamePlayer.allClearEffect();
        if (map == null) return;
        map.values().forEach(v -> {
            if (v != null) {
                if (!v.isCancelled()) v.cancel();
                if (v.getRunnable() != null) {
                    if (!v.getRunnable().isCancelled()) v.getRunnable().cancel();
                }
            }
        });
    }

    public void equipItems() {
        getSeekers().forEach(Seeker::equipItem);
        getHiders().forEach(Hider::equipItem);
    }

    public Seeker findSeeker(UUID uuid) {
        if (uuid == null) return null;
        return getSeekers().stream().filter(s -> s.getPlayerUuid().equals(uuid)).findFirst().orElse(null);
    }

    public Hider findHider(UUID uuid) {
        if (uuid == null) return null;
        return getHiders().stream().filter(h -> h.getPlayerUuid().equals(uuid)).findFirst().orElse(null);
    }

    public Hider findHiderByBlock(Block block) {
        if (block == null) return null;
        for (Hider h : getHiders()) {
            if (h.getBlock() == null) continue;
            if (h.getBlock().equals(block)) return h;
        }
        return null;
//        return getHiders().stream().filter(h -> {
//            if (h.getBlock() == null) return false;
//            return h.getBlock().equals(block);
//        }).findFirst().orElse(null);
    }

    public Hider findHiderByFallingBlock(FallingBlock fallingBlock) {
        if (fallingBlock == null) return null;
        for (Hider h : getHiders()) {
            if (h.getFallingBlock() == null) continue;
            if (h.getFallingBlock().equals(fallingBlock)) return h;
        }
        return null;
//        return getHiders().stream().filter(h -> {
//            if (h.getFallingBlock() == null) return false;
//            return h.getFallingBlock().equals(fallingBlock);
//        }).findFirst().orElse(null);
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

    public void makeAliveHiderDummy() {
        getGamePlayers().values().stream().filter(GamePlayer::isHider)
                .filter(gamePlayer -> !((Hider) gamePlayer).isDead())
                .forEach(hider -> {
                    DummyArmorStand armorStand = new DummyArmorStand(hider);
                    armorStand.create();
                    armorStands.put(hider.getPlayerUuid(), armorStand);
                });
    }

    public void destroyAllDummy() {
        armorStands.values().forEach(DummyArmorStand::destroy);
    }

    public void allSendGreenMessage(String code, Object... args) {
        getGamePlayers().values().forEach(gamePlayer -> gamePlayer.sendGreenMessage(code, args));
    }

    public void allSendGreenTitle(int fadeIn, int stay, int fadeOut, String code, Object... args) {
        getGamePlayers().values().forEach(gamePlayer -> gamePlayer.sendGreenTitle(fadeIn, stay, fadeOut, code, args));
    }

    public void allSendRedTitle(int fadeIn, int stay, int fadeOut, String code, Object... args) {
        getGamePlayers().values().forEach(gamePlayer -> gamePlayer.sendRedTitle(fadeIn, stay, fadeOut, code, args));
    }

    public void setStage(StageData stageData) {
        int index = stageList.indexOf(stageData);
        if (index < 0) return;
        currentStageIndex = index;
    }

    public void setStageIndex(int index) {
        currentStageIndex = index;
    }

    public void deleteStage(StageData stageData) {
        stageList.remove(stageData);
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
        if (optionalStageData.isPresent()) return null;
        StageData stageData = new StageData(name);
        stageList.add(stageData);
        return stageData;
    }

    public void setHiderMaterial(UUID uuid, Material material) {
        Hider hider = findHider(uuid);
        if (hider == null) return;
        hider.setMaterial(material);
        hider.respawnFB();
    }

    public void reloadScoreboard() {
        gamePlayers.values().forEach(gamePlayer -> {
//            gamePlayer.getGameBoard().setText(0, "所持ポイント: " + getEconomy().getBalance(gamePlayer.getPlayer()));
            gamePlayer.getGameBoard().resetText();
            gamePlayer.getGameBoard().setText(0, "所持ポイント: " + getEconomy().getBalance(gamePlayer.getPlayer()));
            if (getCurrentState() != GameState.PLAYING) return;
            gamePlayer.getGameBoard().setText(1, "");
            gamePlayer.getGameBoard().setText(2, "-----");
            List<Hider> livingPlayerList = getGamePlayers().values().stream()
                    .filter(GamePlayer::isHider)
                    .map(gp -> (Hider) gp)
                    .filter(hider -> !hider.isDead())
                    .collect(Collectors.toList());
            gamePlayer.getGameBoard().setText(3, "");
            gamePlayer.getGameBoard().setText(4, "生存プレイヤー : " + getGamePlayers().values().stream()
                    .filter(GamePlayer::isHider)
                    .filter(gp -> !((Hider) gp).isDead()).count() + "人");
            List<String> seekers = new ArrayList<>();
            getGamePlayers().values().stream().filter(GamePlayer::isSeeker)
                    .forEach(gp -> seekers.add(gp.getPlayer().getDisplayName()));
            gamePlayer.getGameBoard().setText(5, "鬼 :");
            for (int i = 0; i < seekers.size(); i++) {
                gamePlayer.getGameBoard().setText(6 + i, "    " + seekers.get(i));
            }
//            for (int i = 0; i < livingPlayerList.size(); i++) {
//                Hider livingPlayer = livingPlayerList.get(i);
//                gamePlayer.getGameBoard().setText(i + 3, "    " + livingPlayer.getPlayer().getDisplayName());
//            }
        });
    }
}