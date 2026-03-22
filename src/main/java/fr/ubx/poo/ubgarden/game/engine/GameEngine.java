    /*
     * Copyright (c) 2020. Laurent Réveillère
     */

    package fr.ubx.poo.ubgarden.game.engine;

    import fr.ubx.poo.ubgarden.game.Direction;
    import fr.ubx.poo.ubgarden.game.Game;
    import fr.ubx.poo.ubgarden.game.Level;
    import fr.ubx.poo.ubgarden.game.Position;
    import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
    import fr.ubx.poo.ubgarden.game.go.decor.*;
    import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
    import fr.ubx.poo.ubgarden.game.go.personage.Hornet;
    import fr.ubx.poo.ubgarden.game.go.personage.Wasp;
    import fr.ubx.poo.ubgarden.game.view.*;
    import javafx.animation.AnimationTimer;
    import javafx.application.Platform;
    import javafx.scene.Group;
    import javafx.scene.Scene;
    import javafx.scene.layout.Pane;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.scene.text.Font;
    import javafx.scene.text.Text;
    import javafx.scene.text.TextAlignment;

    import java.util.*;


    public final class GameEngine {

        private static AnimationTimer gameLoop;
        private final Game game;
        private final Gardener gardener;
        private static List<Wasp> wasp = new ArrayList<>();
        private final List<Sprite> sprites = new LinkedList<>();
        private final Set<Sprite> cleanUpSprites = new HashSet<>();

        private final Scene scene;

        private StatusBar statusBar;

        private final Pane rootPane = new Pane();
        private final Group root = new Group();
        private final Pane layer = new Pane();
        private Input input;

        public GameEngine(Game game, Scene scene) {
            this.game = game;
            this.scene = scene;
            this.gardener = game.getGardener();
            initialize();
            buildAndSetGameLoop();
        }

        public Pane getRoot() {
            return rootPane;
        }

            private void initialize() {
                int height = game.world().getGrid().height();
                int width = game.world().getGrid().width();
                int sceneWidth = width * ImageResource.size;
                int sceneHeight = height * ImageResource.size;
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());
                input = new Input(scene);

                root.getChildren().clear();
                root.getChildren().add(layer);
                statusBar = new StatusBar(root, sceneWidth, sceneHeight);

                rootPane.getChildren().clear();
                rootPane.setPrefSize(sceneWidth, sceneHeight + StatusBar.height);
                rootPane.getChildren().add(root);

                // Create sprites
                int currentLevel = game.world().currentLevel();

                for (var decor : game.world().getGrid().values()) {
                    sprites.add(SpriteFactory.create(layer, decor));
                    decor.setModified(true);
                    var bonus = decor.getBonus();
                    if (bonus != null) {
                        sprites.add(SpriteFactory.create(layer, bonus));
                        bonus.setModified(true);
                    }
                }

                sprites.add(new SpriteGardener(layer, gardener));
                for (Wasp wasp : game.getWasps()) {
                    sprites.add(new SpriteWasp(layer, wasp));
                }
                resizeScene(sceneWidth, sceneHeight);
            }

        void buildAndSetGameLoop() {
            gameLoop = new AnimationTimer() {
                public void handle(long now) {
                    checkLevel();

                    // Check keyboard actions
                    processInput();

                    // Do actions
                    update(now);
                    checkCollision();

                    // Graphic update
                    cleanupSprites();
                    render();
                    statusBar.update(game);
                }
            };
        }


        private void checkLevel() {
            if (!game.isSwitchLevelRequested())
                return;

            sprites.clear();
            layer.getChildren().clear();

            int previousLevel = game.world().currentLevel();
            int targetLevel = game.getSwitchLevel();
            game.world().setCurrentLevel(targetLevel);
            game.getWasps().clear();
            game.getHornets().clear();
            Position newPos = findCorrespondingDoorPosition(previousLevel, targetLevel);
            gardener.setPosition(new Position(targetLevel, newPos.x(), newPos.y()));
            initialize();
            game.clearSwitchLevel();
        }




        private void checkCollision() {
            Position gardenerPos = gardener.getPosition();
            Iterator<Hornet> hornetIt = game.getHornets().iterator();
            while (hornetIt.hasNext()) {
                Hornet hornet = hornetIt.next();
                Position hornetPos = hornet.getPosition();
                Decor decor = game.world().getGrid().get(hornetPos);
                if (decor != null && decor.getBonus() instanceof Insecticide) {
                    hornet.hurt();
                    if (hornet.isDeleted()) {
                        hornetIt.remove();
                    }
                    decor.getBonus().remove();
                }
                if (hornetPos.equals(gardener.getPosition())) {
                    if (gardener.getInsecticideCount() > 0) {
                        gardener.useInsecticide();
                        hornet.hurt();
                        if (hornet.isDeleted()) {
                            hornetIt.remove();
                        }
                    } else {
                        gardener.hurt(30);
                        hornet.hurt();
                        if (hornet.isDeleted()) {
                            hornetIt.remove();
                        }
                    }
                }
            }


            Iterator<Wasp> it = game.getWasps().iterator();
            while (it.hasNext()) {
                Wasp wasp = it.next();
                Position waspPos = wasp.getPosition();
                if (waspPos.equals(gardenerPos)) {
                    if (gardener.getInsecticideCount() > 0) {
                        gardener.useInsecticide();
                        wasp.remove();
                        it.remove();
                    } else {
                        gardener.hurt(20);
                        wasp.remove();
                        it.remove();
                        System.out.println("Ouch !");
                    }
                    continue;
                }
            }
        }
        private Position findCorrespondingDoorPosition(int fromLevel, int toLevel) {
            for (Decor decor : game.world().getGrid().values()) {
                if (toLevel > fromLevel && decor instanceof DoorPrevOpened)
                    return decor.getPosition();
                if (toLevel < fromLevel && decor instanceof DoorNextOpened)
                    return decor.getPosition();
            }
            return gardener.getPosition();
        }




        private void processInput() {
            if (input.isExit()) {
                gameLoop.stop();
                Platform.exit();
                System.exit(0);
            } else if (input.isMoveDown()) {
                gardener.requestMove(Direction.DOWN);
            } else if (input.isMoveLeft()) {
                gardener.requestMove(Direction.LEFT);
            } else if (input.isMoveRight()) {
                gardener.requestMove(Direction.RIGHT);
            } else if (input.isMoveUp()) {
                gardener.requestMove(Direction.UP);
            }
            input.clear();
        }
        public void victory() {
            gameLoop.stop();
            showMessage("Gagné !", Color.GREEN);
        }

        public void defeat() {
            gameLoop.stop();
            showMessage("Perdu !", Color.RED);
        }

        private void showMessage(String msg, Color color) {
            Text message = new Text(msg);
            message.setTextAlignment(TextAlignment.CENTER);
            message.setFont(new Font(60));
            message.setFill(color);

            StackPane pane = new StackPane(message);
            pane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
            rootPane.getChildren().clear();
            rootPane.getChildren().add(pane);

            new AnimationTimer() {
                public void handle(long now) {
                    processInput();
                }
            }.start();
        }

        private void update(long now) {
            Decor decorUnderGardener = game.world().getGrid().get(gardener.getPosition());
            if (game.canSwitchLevel()) {
                Decor decor = game.world().getGrid().get(gardener.getPosition());
                if (decor instanceof DoorNextOpened) {
                    game.requestSwitchLevel(game.world().currentLevel() + 1);
                    game.disableLevelSwitchTemporarily();
                } else if (decor instanceof DoorPrevOpened) {
                    game.requestSwitchLevel(game.world().currentLevel() - 1);
                    game.disableLevelSwitchTemporarily();
                }
            }

            for (Wasp wasp : game.getWasps()) {
                wasp.update(now);
                if (!wasp.isDeleted())
                    sprites.add(new SpriteWasp(layer, wasp));
            }

            for (Hornet hornet : game.getHornets()) {
                hornet.update(now);
                if (!hornet.isDeleted())
                    sprites.add(new SpriteHornet(layer, hornet));
            }

            for (Decor decor : game.world().getGrid().values()) {
                decor.update(now);
                var bonus = decor.getBonus();
                if (bonus instanceof fr.ubx.poo.ubgarden.game.go.bonus.Insecticide) {
                    boolean alreadyDisplayed = sprites.stream()
                            .anyMatch(sprite -> sprite.getGameObject() == bonus);

                    if (!alreadyDisplayed) {
                        sprites.add(SpriteFactory.create(layer, bonus));
                    }
                }
            }
            gardener.update(now);
            Level level = (Level) game.world().getGrid();
            if (level.allCarrotsCollected()) {
                openDoors();
            }

            if (gardener.getEnergy() < 0) {
                defeat();
                return;
            }

            if (decorUnderGardener instanceof Hedgehog) {
                victory();
                return;
            }

            checkLevel();
        }

        private void openDoors() {
            Level level = (Level) game.world().getGrid();

            for (Position pos : new ArrayList<>(level.values().stream()
                    .filter(d -> d instanceof DoorNextClosed)
                    .map(Decor::getPosition)
                    .toList())) {

                Decor d = level.get(pos);
                d.remove();
                level.remove(pos);
                DoorNextOpened door = new DoorNextOpened(pos);
                door.setModified(true);
                level.put(pos, door);
                sprites.add(SpriteFactory.create(layer, door));
            }
        }

        public void cleanupSprites() {
            sprites.forEach(sprite -> {
                if (sprite.getGameObject().isDeleted()) {
                    cleanUpSprites.add(sprite);
                }
            });
            cleanUpSprites.forEach(Sprite::remove);
            sprites.removeAll(cleanUpSprites);
            cleanUpSprites.clear();
        }

        private void render() {
            sprites.forEach(Sprite::render);
        }

        public void start() {
            gameLoop.start();
        }

        private void resizeScene(int width, int height) {
            rootPane.setPrefSize(width, height + StatusBar.height);
            layer.setPrefSize(width, height);
            Platform.runLater(() -> scene.getWindow().sizeToScene());
        }
    }