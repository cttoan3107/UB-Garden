package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GameLauncher {

    private GameLauncher() {
    }

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int waspMoveFrequency = integerProperty(properties, "waspMoveFrequency", 2);
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 1);

        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        long energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1_000);
        long diseaseDuration = integerProperty(properties, "diseaseDuration", 5_000);

        return new Configuration(gardenerEnergy, energyBoost, energyRecoverDuration, diseaseDuration, waspMoveFrequency, hornetMoveFrequency);
    }

    public Game load(File file) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties", e);
        }

        int curr_lvl = Integer.parseInt(properties.getProperty("curr_lvl", "1"));
        int nb_levels = Integer.parseInt(properties.getProperty("levels", "1"));
        boolean compressed = Boolean.parseBoolean(properties.getProperty("compression", "false"));

        TextMapRepoWithCompression repo = new TextMapRepoWithCompression();
        MapLevel[] mapLevels = new MapLevel[nb_levels];

        for (int i = 0; i < nb_levels; i++) {
            String data = properties.getProperty("level" + (i + 1));
            mapLevels[i] = repo.loadMapLevel(data, compressed);
        }

        Position gardenerPos = mapLevels[curr_lvl - 1].getGardenerPosition();
        Configuration config = getConfiguration(properties);
        World world = new World(curr_lvl);
        Game game = new Game(world, config, gardenerPos);

        for (int i = 0; i < nb_levels; i++) {
            world.put(i + 1, new Level(game, i + 1, mapLevels[i]));
        }

        return game;
    }


    public Game load() {
        Properties emptyConfig = new Properties();
        MapLevel mapLevel = new MapLevelDefaultStart();
        Position gardenerPosition = mapLevel.getGardenerPosition();
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        Configuration configuration = getConfiguration(emptyConfig);
        World world = new World(1);
        Game game = new Game(world, configuration, gardenerPosition);
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);
        return game;
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }

}
