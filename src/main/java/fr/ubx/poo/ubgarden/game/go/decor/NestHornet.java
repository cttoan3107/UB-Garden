package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Level;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Hornet;

import java.util.List;

public class NestHornet extends Decor {
    private final Timer spawnTimer;
    private Game game;

    public NestHornet(Position position) {
        super(position);
        this.spawnTimer = new Timer(10000);// 10 sec
        this.spawnTimer.start();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public boolean walkableBy(Gardener gardener) {
        return gardener.canWalkOn(this);
    }

    @Override
    public void update(long now) {
        spawnTimer.update(now);
        if (!spawnTimer.isRunning()) {
            spawnTimer.start();

            Hornet hornet = new Hornet(game, this.getPosition());
            game.addHornet(hornet);
            System.out.println("Hornet generated at " + this.getPosition());

            for (int i = 0; i < 2; i++) {
                Position bombPos = findRandomValidPosition();
                if (bombPos != null) {
                    Decor decor = game.world().getGrid().get(bombPos);
                    Insecticide bomb = new Insecticide(bombPos, decor);
                    decor.setBonus(bomb);
                    bomb.setModified(true);
                    System.out.println("Bomb generated at" + bombPos);
                }
            }
        }
    }

    private Position findRandomValidPosition() {
        Level level = (Level) game.world().getGrid();
        List<Position> possible = level.values().stream()
                .map(Decor::getPosition)
                .filter(p -> {
                    Decor d = level.get(p);
                    return d.walkableBy(game.getGardener()) && d.getBonus() == null;
                })
                .toList();

        if (possible.isEmpty()) return null;
        return possible.get((int)(Math.random() * possible.size()));
    }
}

