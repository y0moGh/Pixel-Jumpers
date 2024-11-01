package pixel.jumpers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Vector2 playerPosition;
    private Vector2 playerVelocity;
    private boolean isJumping;
    private PlayerAnimation playerAnimation;
    private OrthographicCamera camera;
    private Viewport viewport;
    private final int VIRTUAL_WIDTH = 1280; 
    private final int VIRTUAL_HEIGHT = 720; 
    
	// Dentro de la clase Main
	private Texture fullBackgroundTexture;
	
	@Override
	public void create() {
	    batch = new SpriteBatch();
	    camera = new OrthographicCamera();
	    viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	    viewport.apply();
	    
	    camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
	    camera.update();

	    playerPosition = new Vector2(100, 100);
	    playerVelocity = new Vector2(0, 0);
	    isJumping = false;

	    playerAnimation = new PlayerAnimation();
	    
	    fullBackgroundTexture = new Texture("full_background.png");
	}
	
	@Override
	public void resize(int width, int height) {
	    viewport.update(width, height);
	}

	@Override
	public void render() {
	    // Limpia la pantalla y configura la cámara
	    Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    camera.update();
	    batch.setProjectionMatrix(camera.combined);

	    // Dibujar el fondo completo ajustado al tamaño de la pantalla
	    batch.begin();
	    batch.draw(fullBackgroundTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
	    batch.end();

	    // Lógica de movimiento del jugador
	    boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.A);
	    boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.D);

	    if (movingLeft) {
	        playerPosition.x -= 200 * Gdx.graphics.getDeltaTime();
	    }
	    if (movingRight) {
	        playerPosition.x += 200 * Gdx.graphics.getDeltaTime();
	    }

	    if (Gdx.input.isKeyJustPressed(Input.Keys.W) && !isJumping) {
	        playerVelocity.y = 300;
	        isJumping = true;
	    }

	    playerVelocity.y -= 10;
	    playerPosition.add(0, playerVelocity.y * Gdx.graphics.getDeltaTime());

	    int groundLevel = 50;
	    if (playerPosition.y <= groundLevel) {
	        playerPosition.y = groundLevel;
	        playerVelocity.y = 0;
	        isJumping = false;
	    }

	    TextureRegion currentFrame = playerAnimation.getCurrentFrame(movingLeft, movingRight, isJumping);

	    batch.begin();
	    batch.draw(currentFrame, playerPosition.x, playerPosition.y);
	    batch.end();
	}

	@Override
	public void dispose() {
	    batch.dispose();
	    fullBackgroundTexture.dispose();
	}

}
