package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Hornet;
import fr.ubx.poo.ubgarden.game.go.personage.Wasp;

import java.util.ArrayList;
import java.util.List;


public class Game {

    private final Configuration configuration;
    private final World world;
    private final Gardener gardener;
    private boolean switchLevelRequested = false;
    private boolean canSwitchLevel = true;

    private int switchLevel;
    private final List<Wasp> wasps = new ArrayList<>();
    private final List<Hornet> hornets = new ArrayList<>();

    public Game(World world, Configuration configuration, Position gardenerPosition) {
        this.configuration = configuration;
        this.world = world;
        gardener = new Gardener(this, gardenerPosition);
    }

    public Configuration configuration() {
        return configuration;
    }

    public Gardener getGardener() {
        return this.gardener;
    }

    public World world() {
        return world;
    }

    public boolean isSwitchLevelRequested() {
        return switchLevelRequested;
    }

    public int getSwitchLevel() {
        return switchLevel;
    }

    public void requestSwitchLevel(int level) {
        this.switchLevel = level;
        switchLevelRequested = true;
    }

    public void clearSwitchLevel() {
        switchLevelRequested = false;
    }


    public void addWasp(Wasp wasp) {
        wasps.add(wasp);
    }
    public List<Wasp> getWasps() {
        return wasps;
    }
    public void addHornet(Hornet h) {
        hornets.add(h);
    }

    public List<Hornet> getHornets() {
        return hornets;
    }

    public boolean canSwitchLevel() {
        return canSwitchLevel;
    }

    public void disableLevelSwitchTemporarily() {
        this.canSwitchLevel = false;
    }

    public void enableLevelSwitch() {
        this.canSwitchLevel = true;
    }



}
