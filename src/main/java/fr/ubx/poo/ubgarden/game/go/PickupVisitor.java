package fr.ubx.poo.ubgarden.game.go;

import fr.ubx.poo.ubgarden.game.go.bonus.*;
import fr.ubx.poo.ubgarden.game.go.bonus.Bonus;

public interface PickupVisitor {
    default void pickUp(Apple apple) {
    }
    default void pickUp(PoisonedApple poisonedApple) {
    }
    default void pickUp(Insecticide insecticide) {
    }
    default void pickUp(Carrots carrots) {
    }

}
