/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.*;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.bonus.*;
import fr.ubx.poo.ubgarden.game.go.decor.*;
import fr.ubx.poo.ubgarden.game.go.decor.Flowers;
import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import fr.ubx.poo.ubgarden.game.go.decor.Tree;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Land;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private int energy;
    private Direction direction;
    private boolean moveRequested = false;

    private int diseaseLevel = 1;
    private int insecticideCount = 0;
    private int carrotsCollected = 0;
    private long lastMoveTime = System.currentTimeMillis();
    private boolean hasMoved = false;

    private final List<Long> diseaseTimers = new ArrayList<>();

    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();
    }
    public int getDiseaseLevel() {
        return diseaseLevel;
    }

    public int getInsecticideCount() {
        return insecticideCount;
    }
    public int getCarrotsCollected() {
        return carrotsCollected;
    }


    public int getEnergy() {
        return this.energy;
    }


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }


    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Map map = game.world().getGrid();
        if (!map.inside(nextPos))
            return false;

        Decor decor = map.get(nextPos);
        if (decor != null && !decor.walkableBy(this))
            return false;

        int cost = diseaseLevel;
        if (decor instanceof Land)
            cost = 2 * diseaseLevel;

        return energy >= cost;
    }
    public boolean canWalkOn(Tree tree) {
        return false;
    }

    public boolean canWalkOn(Flowers flowers) {
        return false;
    }
    public boolean canWalkOn(DoorNextClosed door) {
        return false;
    }

    @Override
    public void pickUp(Apple apple) {
        this.energy = Math.min(energy + game.configuration().energyBoost(), game.configuration().gardenerEnergy());
        this.diseaseLevel = 1;
        System.out.println("Apple picked: energy=" + energy + ", diseaseLevel=1");
    }

    @Override
    public void pickUp(PoisonedApple poisoned) {
        diseaseLevel++;
        long endTime = System.currentTimeMillis() + game.configuration().diseaseDuration();
        diseaseTimers.add(endTime);
        System.out.println("Disease +1 (level=" + diseaseLevel + ")");
    }

    @Override
    public void pickUp(Insecticide insecticide) {
        this.insecticideCount++;
        System.out.println("Insecticide picked: total=" + insecticideCount);
    }

    @Override
    public void pickUp(Carrots carrots) {
        ((Level) game.world().getGrid()).collectCarrot();
        this.carrotsCollected++;
        System.out.println("Carrots = " + carrotsCollected);
    }

    @Override
    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = game.world().getGrid().get(nextPos);
        int cost = diseaseLevel;

        if (decor instanceof Land)
            cost = 2 * diseaseLevel;
        if (energy < cost) {
            if (moveRequested) {
                System.out.println("Tired !");
            }
            return getPosition();
        }
        energy -= cost;
        game.enableLevelSwitch();
        setPosition(nextPos);

        if (decor != null)
            decor.pickUpBy(this);

        return nextPos;
    }

    public void update(long now) {
        if (moveRequested) {
            if (energy > 0 && canMove(direction)) {
                move(direction);
            }
            moveRequested = false;
        }
        else {
            long nowMs = System.currentTimeMillis();
            if (nowMs - lastMoveTime >= game.configuration().energyRecoverDuration()) {
                if (energy < game.configuration().gardenerEnergy()){
                    energy++;
                    lastMoveTime = nowMs;
                    setModified(true);
                }

            }
        }
        long currentTime = System.currentTimeMillis();
        Iterator<Long> it = diseaseTimers.iterator();
        while (it.hasNext()) {
            if (currentTime >= it.next()) {
                diseaseLevel = Math.max(1, diseaseLevel - 1);
                it.remove();
                System.out.println("Disease -1 (level=" + diseaseLevel + ")");
            }
        }
        hasMoved = false;
    }

    public void hurt(int damage) {
        this.energy -= damage;
        System.out.println("Gardener hurt! Energy = " + energy);
    }
    public void useInsecticide() {
        if (insecticideCount > 0)
            insecticideCount--;
    }

    public void hurt() {
        hurt(1);
    }

    public Direction getDirection() {
        return direction;
    }


}
