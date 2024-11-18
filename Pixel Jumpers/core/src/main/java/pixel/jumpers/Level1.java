package pixel.jumpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Level1 extends BaseLevel {
    private Player player;
    private Array<Platform> platforms;
    private Array<Enemy> enemies;
    private Texture platformTexture;
    private Texture enemyTexture;

    private final float PLAYER_START_X = 100;
    private final float PLAYER_START_Y = 100;

    private final float[] PLATFORM_START_X = {300, 600, 900};
    private final float[] PLATFORM_START_Y = {150, 250, 350};

    private final float ENEMY_1_START_X = 150;
    private final float ENEMY_1_START_Y = 50;

    private final float ENEMY_2_START_X = 400;
    private final float ENEMY_2_START_Y = 50;

    public Level1(Main game) {
        super(game);

        player = new Player(PLAYER_START_X, PLAYER_START_Y);

        fullBackgroundTexture = new Texture("full_background_grey.png");

        platformTexture = new Texture("platform.png");
        platforms = new Array<>();
        for (int i = 0; i < PLATFORM_START_X.length; i++) {
            platforms.add(new Platform(platformTexture, PLATFORM_START_X[i], PLATFORM_START_Y[i], 100, 20));
        }

        enemyTexture = new Texture("enemy.png");
        enemies = new Array<>();
        enemies.add(new Enemy(enemyTexture, ENEMY_1_START_X, ENEMY_1_START_Y, 50, 50, 100, 200, 700));
        enemies.add(new Enemy(enemyTexture, ENEMY_2_START_X, ENEMY_2_START_Y, 50, 50, -120, 1200, 1900));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetLevel();
        }

        player.update(delta, camera, platforms, enemies);

        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }

        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).isReadyToRemove()) {
                enemies.removeIndex(i);
            }
        }

        float playerCenterX = player.getX() + player.getWidth() / 2f;
        camera.position.x = Math.max(playerCenterX, VIRTUAL_WIDTH / 2f);

        float backgroundWidth = fullBackgroundTexture.getWidth();
        float minCameraX = VIRTUAL_WIDTH / 2f;
        float maxCameraX = backgroundWidth - VIRTUAL_WIDTH / 2f;
        camera.position.x = MathUtils.clamp(camera.position.x, minCameraX, maxCameraX);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        drawBackground();

        batch.begin();
        player.draw(batch);
        for (Platform platform : platforms) {
            platform.draw(batch);
        }
        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }
        batch.end();

        drawHealthBar(player);
    }

    @Override
    public void dispose() {
        super.dispose();
        platformTexture.dispose();
        enemyTexture.dispose();
        player.dispose();
    }

    private void resetLevel() {
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        player.reset();

        platforms.clear();
        for (int i = 0; i < PLATFORM_START_X.length; i++) {
            platforms.add(new Platform(platformTexture, PLATFORM_START_X[i], PLATFORM_START_Y[i], 100, 20));
        }

        enemies.clear();
        enemies.add(new Enemy(enemyTexture, ENEMY_1_START_X, ENEMY_1_START_Y, 50, 50, 100, 200, 700));
        enemies.add(new Enemy(enemyTexture, ENEMY_2_START_X, ENEMY_2_START_Y, 50, 50, -120, 1200, 1900));
    }
    
    @Override
    public void show() {
        // Se ejecuta al mostrar la pantalla por primera vez
    }

    @Override
    public void hide() {
        // Se ejecuta al ocultar la pantalla
    }

    @Override
    public void pause() {
        // Se ejecuta al pausar el juego
    }

    @Override
    public void resume() {
        // Se ejecuta al reanudar el juego tras una pausa
    }
}
