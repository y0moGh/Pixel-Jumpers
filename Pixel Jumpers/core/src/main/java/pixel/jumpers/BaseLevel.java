package pixel.jumpers;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

    public BaseLevel(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        this.viewport.apply();

        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        camera.update();
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
    }
}
