package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.*;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;

public class Hornet extends GameObject implements Movable {

    private final Timer moveTimer;
    private Direction direction;
    private int life = 2;

    public Hornet(Game game, Position position) {
        super(game, position);
        this.direction = Direction.random();
        long delay = 1000L / game.configuration().hornetMoveFrequency();
        this.moveTimer = new Timer(delay);
        setModified(true);
    }

    @Override
    public void update(long now) {
        moveTimer.update(now);
        if (!moveTimer.isRunning()) {
            moveRandomly();
            moveTimer.start();
        }
    }

    private void moveRandomly() {
        Direction dir = Direction.random();
        if (canMove(dir)) {
            move(dir);
            this.direction = dir;
            setModified(true);
        }
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean canMove(Direction direction) {
        Position next = direction.nextPosition(getPosition());
        return game.world().getGrid().inside(next);
    }

    @Override
    public Position move(Direction direction) {
        Position next = direction.nextPosition(getPosition());
        setPosition(next);
        return next;
    }

    public void hurt() {
        life--;
        if (life <= 0)
            remove();
    }
/*
    public boolean isDead() {
        return life <= 0;
    }

 */
}
