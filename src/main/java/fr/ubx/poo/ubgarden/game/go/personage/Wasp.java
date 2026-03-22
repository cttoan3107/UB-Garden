package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;

public class Wasp extends GameObject implements Movable {

    private Direction direction;
    private final Timer moveTimer;

    public Wasp(Game game, Position position) {
        super(game, position);
        this.direction = Direction.random();
        long delay = 2000L / game.configuration().waspMoveFrequency();
        this.moveTimer = new Timer(delay);
    }

    @Override
    public void update(long now) {
        moveTimer.update(now);
        if (!moveTimer.isRunning()) {
            moveRandomly();
            moveTimer.start();
        }
    }
    public Direction getDirection() {
        return direction;
    }
    @Override
    public Position move(Direction direction) {
        if (canMove(direction)) {
            Position next = direction.nextPosition(getPosition());
            setPosition(next);
            setModified(true);
            return next;
        }
        return getPosition();
    }

    private void moveRandomly() {
        Direction[] dirs = Direction.values();
        Direction dir = dirs[(int)(Math.random() * dirs.length)];

        if (canMove(dir)) {
            move(dir);
            this.direction = dir;
            setModified(true);
        }
    }
    public void hurt() {
        this.remove();
    }
    @Override
    public boolean canMove(Direction direction) {
        Position next = direction.nextPosition(getPosition());
        return game.world().getGrid().inside(next);
    }
}
