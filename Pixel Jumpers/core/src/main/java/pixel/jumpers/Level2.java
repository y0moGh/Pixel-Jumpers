package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Level2 extends BaseLevel {
    private final float PLAYER_START_X = 100;
    private final float PLAYER_START_Y = 100;

    private final float[] PLATFORM_START_X = {200, 500, 800};
    private final float[] PLATFORM_START_Y = {150, 250, 350};

    private final float ENEMY_1_START_X = 150;
    private final float ENEMY_1_START_Y = 50;

    private final float ESTATUA_START_X = 700;
    private final float ESTATUA_START_Y = 40;
    
    private final float[] PINCHO_START_X = {300};
    private final float[] PINCHO_START_Y = {30};

    public Level2(Main game) {
        super(game);
        shapeRenderer = new ShapeRenderer();
        player = new Player(PLAYER_START_X, PLAYER_START_Y);

        fullBackgroundTexture = new Texture("full_background_grey.png");

        for (int i = 0; i < PLATFORM_START_X.length; i++) {
            platforms.add(new Platform(platformTexture, PLATFORM_START_X[i], PLATFORM_START_Y[i], platform_size_x, platform_size_y));
        }
        
        grounds.add(new Ground(groundTexture, 900, -10, ground_img_x, ground_img_y, 150, 320));

        enemies.add(new Enemy(enemyTexture, ENEMY_1_START_X, ENEMY_1_START_Y, enemy_size, enemy_size, enemy_speed, 200, 700));


        estatuas.add(new Estatua(estatuaTexture, ESTATUA_START_X, ESTATUA_START_Y, estatua_img_x, estatua_img_y, estatua_htb_x, estatua_htb_y));
        
        for (int i = 0; i < PINCHO_START_X.length; i++) {
            pinchos.add(new Pinchos(pinchosTexture, PINCHO_START_X[i], PINCHO_START_Y[i], pincho_img_x, pincho_img_y, pincho_htb_x, pincho_htb_y));
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Lógica para Level2
        // Dibuja las hitboxes
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1); // Rojo para las hitboxes
        for (Ground ground : grounds) {
            Rectangle bounds = ground.getBounds();
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        shapeRenderer.end();
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

