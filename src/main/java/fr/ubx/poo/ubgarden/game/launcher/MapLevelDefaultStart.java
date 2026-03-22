package fr.ubx.poo.ubgarden.game.launcher;


import static fr.ubx.poo.ubgarden.game.launcher.MapEntity.*;

public class MapLevelDefaultStart extends MapLevel {


    private final static int width = 18;
    private final static int height = 8;
    private final MapEntity[][] level1 = {
            {Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, DoorNextClosed},
            {Grass, Gardener, Grass, Land, Carrots, Grass, Flowers, Grass, Grass, Tree, Grass, Grass, Tree, Tree, Grass, Grass, Grass, Grass},
            {Grass, Land, Land, Land, Grass, Grass, Grass, Grass, PoisonedApple, Grass, PoisonedApple, Grass, Grass, Tree, Grass, Grass, Grass, Grass},
            {Grass, Grass, Grass, Grass, Grass, Insecticide, Grass, Grass, Grass, NestHornet, NestWasp, Grass, Grass, Tree, Grass, Grass, Grass, Grass},
            {Grass, Tree, Grass, Flowers, Grass, Grass, Grass, Grass, PoisonedApple, Flowers, Land, Grass, Grass, Tree, Grass, Grass, Apple, Grass},
            {Grass, Tree, Tree, Tree, Grass, Grass, Land, Grass, Land, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass},
            {Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, PoisonedApple, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass},
            {Grass, Tree, Grass, Tree, Grass, Flowers, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Hedgehog, Grass}
    };


    public MapLevelDefaultStart() {
        super(width, height);
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                set(i, j, level1[j][i]);
    }


}
