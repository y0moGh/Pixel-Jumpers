package pixel.jumpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class BaseLevel implements Screen {
    protected Main game;
    protected SpriteBatch batch;
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected final int VIRTUAL_WIDTH = 1280;
    protected final int VIRTUAL_HEIGHT = 720;

    protected Texture fullBackgroundTexture;
    
    // Base constants
    protected Player player;
    protected Array<Platform> platforms;
    protected Array<Pinchos> pinchos;
    protected Array<Enemy> enemies;
    protected Array<Estatua> estatuas;
    protected Array<Ground> grounds;
    
    protected Texture platformTexture;
    protected Texture pinchosTexture;
    protected Texture pinchosFlotantesTexture;
    protected Texture enemyTexture;
    protected Texture estatuaTexture;
    protected Texture groundTexture;
    protected Texture platformGreyTexture;
    protected Texture platformSemiGreyTexture;
    protected Texture estatuaFinalTexture;
    
    protected int pincho_img_x = 100;
    protected int pincho_img_y = 30;
    protected int pincho_htb_x = 60;
    protected int pincho_htb_y = 40;
    
    protected int estatua_img_x = 140;
    protected int estatua_img_y = 80;
    protected int estatua_htb_x = 50;
    protected int estatua_htb_y = 70;
    
    protected int platform_size_x = 100;
    protected int platform_size_y = 20;
    
    protected float enemy_speed = 100;
    protected float enemy_size = 50;
    
    protected int ground_img_x = 600;
    protected int ground_img_y = 400;

    protected ShapeRenderer shapeRenderer;

    public BaseLevel(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        this.viewport.apply();

        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        camera.update();
        
        shapeRenderer = new ShapeRenderer();
        platformTexture = new Texture("platform.png");
        platformGreyTexture = new Texture("platform_grey.png");
        platformSemiGreyTexture = new Texture("platform_semi_grey.png");
        pinchosTexture = new Texture("pinchos.png");
        pinchosFlotantesTexture = new Texture("pinchos_flotantes.png");
        enemyTexture = new Texture("enemy_animations/enemy.png");
        estatuaTexture = new Texture("statue.png");
        estatuaFinalTexture = new Texture("Final_statue.png");
        groundTexture = new Texture("ground3.png");
        
        enemies = new Array<>();
        estatuas = new Array<>();
        pinchos = new Array<>();
        platforms = new Array<>();
        grounds = new Array<>();
    }

    protected void drawBackground() {
        float backgroundWidth = fullBackgroundTexture.getWidth();
        float cameraLeftEdge = camera.position.x - VIRTUAL_WIDTH / 2f;
        float startX = cameraLeftEdge - (cameraLeftEdge % backgroundWidth);

        batch.begin();
        for (float x = startX; x < cameraLeftEdge + VIRTUAL_WIDTH; x += backgroundWidth) {
            batch.draw(fullBackgroundTexture, x, 0, backgroundWidth, VIRTUAL_HEIGHT);
        }
        batch.end();
    }

    protected void drawHealthBar(Player player) {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float healthBarX = camera.position.x - VIRTUAL_WIDTH / 2f + 20;
        float healthBarY = camera.position.y + VIRTUAL_HEIGHT / 2f - 40;

        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(healthBarX, healthBarY, 200, 20);

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(healthBarX, healthBarY, player.getHealth() * 2, 20);

        shapeRenderer.end();
        shapeRenderer.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (fullBackgroundTexture != null) {
            fullBackgroundTexture.dispose();
        }
        platformTexture.dispose();
        enemyTexture.dispose();
        player.dispose();
        shapeRenderer.dispose();
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.update(delta, camera, platforms, enemies, pinchos, estatuas, grounds);

        // Verifica si el jugador está atacando y actualiza las estatuas
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.attack(enemies, estatuas);
        }

        for (int i = estatuas.size - 1; i >= 0; i--) {
            if (estatuas.get(i).isDestroyed()) {
                estatuas.removeIndex(i);
            }
        }

        // Actualiza enemigos
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(delta); // Actualizar estado del enemigo
            if (enemy.isReadyToRemove()) {
                enemies.removeIndex(i); // Eliminar enemigo del arreglo si ya terminó su animación de muerte
            }
        }

        // Actualiza la posición de la cámara
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
        for (Pinchos pincho : pinchos) {
            pincho.draw(batch, pincho_img_x, pincho_img_y);
        }
        for (Estatua estatua : estatuas) {
            estatua.draw(batch, estatua_img_x, estatua_img_y); // Tamaño de la estatua
        }
        for (Ground ground : grounds) {
        	ground.draw(batch, ground_img_x, ground_img_y);
        }
        batch.end();
    }
}
