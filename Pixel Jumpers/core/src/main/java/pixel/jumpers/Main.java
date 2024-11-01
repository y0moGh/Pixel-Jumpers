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
    private boolean isDoubleJumping;
    private PlayerAnimation playerAnimation;
    private OrthographicCamera camera;
    private Viewport viewport;
    private final int VIRTUAL_WIDTH = 1280;
    private final int VIRTUAL_HEIGHT = 720;

    private Texture fullBackgroundTexture;
    private float backgroundX;

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
        isDoubleJumping = false;
        
        playerAnimation = new PlayerAnimation();

        fullBackgroundTexture = new Texture("full_background.png");
        backgroundX = 0;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render() {
        // Limpiar pantalla y actualizar la cámara
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Lógica de movimiento del jugador
        boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.D);

        if (movingLeft) {
            playerPosition.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (movingRight) {
            playerPosition.x += 200 * Gdx.graphics.getDeltaTime();
        }

        // Control del salto y salto doble
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (!isJumping) {
                playerVelocity.y = 300;
                isJumping = true;
            } else if (!isDoubleJumping) { // Permitir el doble salto
                playerVelocity.y = 300;     // Reinicia la velocidad en Y para el doble salto
                isDoubleJumping = true;     // Activa el estado de doble salto
            }
        }

        // Aplicar gravedad
        playerVelocity.y -= 10;
        playerPosition.add(0, playerVelocity.y * Gdx.graphics.getDeltaTime());

        int groundLevel = 50;
        if (playerPosition.y <= groundLevel) {
            playerPosition.y = groundLevel;
            playerVelocity.y = 0;
            isJumping = false;
            isDoubleJumping = false; // Restablece el doble salto al tocar el suelo
        }

        // Evitar que el jugador se salga del borde izquierdo de la pantalla
        if (playerPosition.x < 0) {
            playerPosition.x = 0;
        }

        // Hacer que la cámara siga al jugador
        camera.position.x = playerPosition.x + 32; // Ajuste para centrar al personaje
        if (camera.position.x < VIRTUAL_WIDTH / 2f) {
            camera.position.x = VIRTUAL_WIDTH / 2f;
        }
        camera.update();

        // Dibujar el fondo infinito
        batch.begin();
        float cameraLeftEdge = camera.position.x - VIRTUAL_WIDTH / 2f;
        float backgroundWidth = fullBackgroundTexture.getWidth();

        // Calcular la posición de inicio del fondo usando el desplazamiento modular
        float startX = cameraLeftEdge - (cameraLeftEdge % backgroundWidth);

        // Dibujar el fondo repetido para cubrir el ancho de la pantalla
        for (float x = startX; x < cameraLeftEdge + VIRTUAL_WIDTH; x += backgroundWidth) {
            batch.draw(fullBackgroundTexture, x, 0, backgroundWidth, VIRTUAL_HEIGHT);
        }
        batch.end();

        // Dibujar al personaje
        TextureRegion currentFrame = playerAnimation.getCurrentFrame(movingLeft, movingRight, isJumping, isDoubleJumping);
        float characterWidth = 64;
        float characterHeight = 64;

        batch.begin();
        batch.draw(currentFrame, playerPosition.x, playerPosition.y, characterWidth, characterHeight);
        batch.end();
    }



    @Override
    public void dispose() {
        batch.dispose();
        fullBackgroundTexture.dispose();
    }
}