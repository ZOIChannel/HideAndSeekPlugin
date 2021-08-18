package jp.hack.minecraft.hideandseek.data;

import jp.hack.minecraft.hideandseek.system.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;
import java.util.stream.Collectors;

public class StageData implements ConfigurationSerializable {
    private final String name;
    private Location lobbyLocation;
    private Location seekerLobbyLocation;
    private Location stageLocation;
    private double radius = 0;
    private WorldBorder stageBorder;

    public StageData(String name) {
        this.name = name;
    }

    private void saveToConfig(ConfigLoader config) {
        List<StageData> stageList;
        if (!(config.getData("stage") instanceof List)
                || ((List<?>) config.getData("stage")).stream().noneMatch(Objects::nonNull)) {
            stageList = new ArrayList<>();
            config.setData("stage", stageList);
        } else {
            stageList = (List<StageData>) config.getData("stage");
        }
        if (stageList.stream().noneMatch(stageData -> stageData.getName().equals(getName()))) stageList.add(this);
//        stageList.put(getName(), this);
        config.setData("stage", stageList);
    }

    public void setLocation(StageType type, Location location, ConfigLoader config) {
        setLocationWithoutSave(type, location);
        saveToConfig(config);
    }

    public void setLocationWithoutSave(StageType type, Location location) {
        switch (type) {
            case LOBBY:
                lobbyLocation = location;
                break;
            case SEEKER_LOBBY:
                seekerLobbyLocation = location;
                break;
            case STAGE:
                stageLocation = location;
                break;
            default:
                // エラー
                break;
        }
    }

    public void setRadius(double radius, ConfigLoader config) {
        setRadiusWithoutSave(radius);
        saveToConfig(config);
    }

    private void setRadiusWithoutSave(double radius) {
        this.radius = radius;
    }

    public boolean isInitialized() {
        return lobbyLocation != null
                && seekerLobbyLocation != null
                && stageLocation != null
                && radius != 0;
    }

    public void createBorder() {
        stageBorder = Objects.requireNonNull(stageLocation.getWorld()).getWorldBorder();
        stageBorder.setCenter(stageLocation);
        stageBorder.setSize(radius);
        stageBorder.setDamageAmount(0);
        stageBorder.setDamageBuffer(0);
        stageBorder.setWarningTime(0);
        stageBorder.setWarningDistance(0);
    }

    public void deleteBorder() {
        if (stageBorder == null) return;
        stageBorder.reset();
    }

    public String getName() {
        return name;
    }

    public Location getLobby() {
        return lobbyLocation;
    }

    public Location getSeekerLobby() {
        return seekerLobbyLocation;
    }

    public Location getStage() {
        return stageLocation;
    }

    public Double getRadius() {
        return radius;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("name", name);
        serialized.put("lobbyLocation", lobbyLocation);
        serialized.put("seekerLobbyLocation", seekerLobbyLocation);
        serialized.put("stageLocation", stageLocation);
        serialized.put("radius", radius);
        return serialized;
    }

    public static StageData deserialize(Map<String, Object> serialized) {
        StageData stageData = new StageData((String) serialized.get("name"));
        stageData.setLocationWithoutSave(StageType.LOBBY, (Location) serialized.get("lobbyLocation"));
        stageData.setLocationWithoutSave(StageType.SEEKER_LOBBY, (Location) serialized.get("seekerLobbyLocation"));
        stageData.setLocationWithoutSave(StageType.STAGE, (Location) serialized.get("stageLocation"));
        stageData.setRadiusWithoutSave((Double) serialized.get("radius"));
        return stageData;
    }
}
