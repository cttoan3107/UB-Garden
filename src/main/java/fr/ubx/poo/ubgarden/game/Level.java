package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.bonus.*;
import fr.ubx.poo.ubgarden.game.go.decor.*;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Land;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.launcher.MapEntity;
import fr.ubx.poo.ubgarden.game.launcher.MapLevel;

import java.util.Collection;
import java.util.HashMap;

public class Level implements Map {

    private final int level;
    private final int width;

    private final int height;
    private int carrotsTotal = 0;
    private int carrotsCollected = 0;

    private final java.util.Map<Position, Decor> decors = new HashMap<>();

    public Level(Game game, int level, MapLevel entities) {
        this.level = level;
        this.width = entities.width();
        this.height = entities.height();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Position position = new Position(level, i, j);
                MapEntity mapEntity = entities.get(i, j);
                switch (mapEntity) {
                    case Grass:
                        decors.put(position, new Grass(position));
                        break;
                    case Tree:
                        decors.put(position, new Tree(position));
                        break;
                    case Apple: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new Apple(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case Carrots: {
                        carrotsTotal++;
                        Decor grass = new Grass(position);
                        grass.setBonus(new Carrots(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case PoisonedApple: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new PoisonedApple(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case Insecticide: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new Insecticide(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case Hedgehog: {
                        decors.put(position, new Hedgehog(position));
                        break;
                    }
                    case Land:
                        decors.put(position, new Land(position));
                        break;
                    case Flowers:
                        decors.put(position, new Flowers(position));
                        break;
                    case DoorNextClosed:
                        decors.put(position, new DoorNextClosed(position));
                        break;
                    case DoorNextOpened:
                        decors.put(position, new DoorNextOpened(position));
                        break;
                    case DoorPrevOpened:
                        decors.put(position, new DoorPrevOpened(position));
                        break;
                    case NestWasp:
                        NestWasp nestWasp = new NestWasp(position);
                        nestWasp.setGame(game);
                        decors.put(position, nestWasp);
                        break;
                    case NestHornet:
                        NestHornet nestHornet = new NestHornet(position);
                        nestHornet.setGame(game);
                        decors.put(position, nestHornet);
                        break;


                    default:
                        throw new RuntimeException("EntityCode " + mapEntity.name() + " not processed");
                }
            }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    public Decor get(Position position) {
        return decors.get(position);
    }

    public Collection<Decor> values() {
        return decors.values();
    }



    @Override
    public boolean inside(Position position) {
        return position.x() >= 0 && position.x() < width &&
                position.y() >= 0 && position.y() < height;
    }
    public boolean canMoveTo(Position position, Gardener gardener) {
        if (!inside(position))
            return false;
        Decor decor = decors.get(position);
        return decor == null || decor.walkableBy(gardener);
    }
    public void collectCarrot() {
        carrotsCollected++;
    }

    public boolean allCarrotsCollected() {
        return carrotsCollected >= carrotsTotal;
    }
    public void remove(Position position) {
        decors.remove(position);
    }
    public void put(Position position, Decor decor) {
        decors.put(position, decor);
    }


}
