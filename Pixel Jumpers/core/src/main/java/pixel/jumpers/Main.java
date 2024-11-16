package pixel.jumpers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private final int VIRTUAL_WIDTH = 1280;
    private final int VIRTUAL_HEIGHT = 720;

    private Texture fullBackgroundTexture;
    private float backgroundX;

    private Array<Platform> platforms;
    private Texture platformTexture;

    private Array<Enemy> enemies;
    private Texture enemyTexture;

    private Player player;
    private boolean showHitboxes;
    
    // Posiciones originales de los objetos
    private final float PLAYER_START_X = 100;
    private final float PLAYER_START_Y = 100;
    
    private final float[] PLATFORM_START_X = {300, 600, 900};
    private final float[] PLATFORM_START_Y = {150, 250, 350};
    
    // Diferentes posiciones de spawn para los enemigos
    private final float ENEMY_1_START_X = 150;
    private final float ENEMY_1_START_Y = 50;
    
    private final float ENEMY_2_START_X = 400;
    private final float ENEMY_2_START_Y = 50;
    
    // Mostrar la barra de vida
    private void drawHealthBar(SpriteBatch batch) {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Posición de la barra relativa a la cámara
        float healthBarX = camera.position.x - VIRTUAL_WIDTH / 2f + 20;  // 20 píxeles desde el borde izquierdo
        float healthBarY = camera.position.y + VIRTUAL_HEIGHT / 2f - 40; // 40 píxeles desde el borde superior

        // Dibujar la barra de fondo (gris)
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(healthBarX, healthBarY, 200, 20);  // Barra más ancha

        // Dibujar la barra de vida (verde), ajustada según la salud
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(healthBarX, healthBarY, player.getHealth() * 2, 20);  // Ajustar longitud según vida

        shapeRenderer.end();
        shapeRenderer.dispose();
    }

    
    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();

        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        camera.update();

        player = new Player(100, 100);

        fullBackgroundTexture = new Texture("full_background_grey.png");
        backgroundX = 0;

        platformTexture = new Texture("platform.png");
        platforms = new Array<>();
        platforms.add(new Platform(platformTexture, 300, 150, 100, 20));
        platforms.add(new Platform(platformTexture, 600, 250, 100, 20));
        platforms.add(new Platform(platformTexture, 900, 350, 100, 20));

        enemyTexture = new Texture("enemy.png");
        enemies = new Array<>();
        enemies.add(new Enemy(enemyTexture, 100, 50, 50, 50, 100, 200, 700));
        enemies.add(new Enemy(enemyTexture, 100, 50, 50, 50, -120, 1200, 1900));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render() {
        // Limpiar la pantalla y actualizar la cámara
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Detectar si la tecla 'r' es presionada para reiniciar el nivel
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetLevel();
        }

        // Actualizar lógica del jugador
        player.update(Gdx.graphics.getDeltaTime(), camera, platforms, enemies);

        // Actualizar lógica de los enemigos
        for (Enemy enemy : enemies) {
            enemy.update(Gdx.graphics.getDeltaTime());
        }

        // Remover enemigos muertos
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).getHealth() <= 0) {
                enemies.removeIndex(i);
            }
        }

        // Actualizar la posición de la cámara para que siga al jugador
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        camera.position.x = Math.max(playerCenterX, VIRTUAL_WIDTH / 2f);

        // Limitar la cámara para que no salga del fondo
        float backgroundWidth = fullBackgroundTexture.getWidth();
        float minCameraX = VIRTUAL_WIDTH / 2f;
        float maxCameraX = backgroundWidth - VIRTUAL_WIDTH / 2f;
        camera.position.x = MathUtils.clamp(camera.position.x, minCameraX, maxCameraX);

        camera.update();

        // Configurar la proyección y comenzar a dibujar
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Dibujar el fondo infinito
        float cameraLeftEdge = camera.position.x - VIRTUAL_WIDTH / 2f;
        float startX = cameraLeftEdge - (cameraLeftEdge % backgroundWidth);
        for (float x = startX; x < cameraLeftEdge + VIRTUAL_WIDTH; x += backgroundWidth) {
            batch.draw(fullBackgroundTexture, x, 0, backgroundWidth, VIRTUAL_HEIGHT);
        }

        // Dibujar al jugador y otros elementos
        player.draw(batch);
        for (Platform platform : platforms) {
            platform.draw(batch);
        }
        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }

        // Dibujar la barra de vida
        batch.end();
        drawHealthBar(batch); // `drawHealthBar` usa un `ShapeRenderer` separado
    }


    @Override
    public void dispose() {
        batch.dispose();
        fullBackgroundTexture.dispose();
        enemyTexture.dispose();
        player.dispose();
    }
    
    // Método para reiniciar el nivel
    private void resetLevel() {
        // Reiniciar jugador
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        player.reset();

        // Reiniciar plataformas
        platforms.clear();
        for (int i = 0; i < PLATFORM_START_X.length; i++) {
            platforms.add(new Platform(platformTexture, PLATFORM_START_X[i], PLATFORM_START_Y[i], 100, 20));
        }

        // Reiniciar enemigos con sus posiciones específicas
        enemies.clear();
        enemies.add(new Enemy(enemyTexture, ENEMY_1_START_X, ENEMY_1_START_Y, 50, 50, 100, 200, 700));
        enemies.add(new Enemy(enemyTexture, ENEMY_2_START_X, ENEMY_2_START_Y, 50, 50, -120, 1200, 1900));
    }
}
