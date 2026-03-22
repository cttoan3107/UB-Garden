package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Level;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.personage.Wasp;
import fr.ubx.poo.ubgarden.game.view.Sprite;
import fr.ubx.poo.ubgarden.game.view.SpriteFactory;

import java.util.List;

public class NestWasp extends Decor {
    private final Timer spawnTimer;
    private Game game;

    public NestWasp(Position position) {
        super(position);
        this.spawnTimer = new Timer(5000); // 5 sec
        this.spawnTimer.start();
    }
    private Position findRandomValidPosition() {
        Level level = (Level) game.world().getGrid();

        List<Position> possible = level.values().stream()
                .map(Decor::getPosition)
                .filter(p -> {
                    Decor d = level.get(p);
                    return d.walkableBy(game.getGardener())
                            && d.getBonus() == null;
                })
                .toList();

        if (possible.isEmpty())
            return null;

        return possible.get((int)(Math.random() * possible.size()));
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
            Wasp wasp = new Wasp(game, this.getPosition());
            game.addWasp(wasp);
            System.out.println("Wasp generated at " + this.getPosition());
            Position bombPos = findRandomValidPosition();
            if (bombPos != null) {
                Decor decor = game.world().getGrid().get(bombPos);
                Insecticide bomb = new Insecticide(bombPos, decor);
                decor.setBonus(bomb);
            }
            Decor decor = game.world().getGrid().get(bombPos);
            if (decor != null) {
                Insecticide bomb = new Insecticide(bombPos, decor);
                decor.setBonus(bomb);

                bomb.setModified(true);
                decor.setModified(true);
                System.out.println("Bomb generated at" + bombPos);
            }
        }
    }
}