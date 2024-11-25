package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;

public class End extends BaseLevel {
    private final float PLAYER_START_X = 0; // Posición inicial del jugador
    private final float PLAYER_START_Y = 0;

    private Button button; // Declarar el botón como atributo de la clase

    public End(Main game) {
        super(game);

        // Configuración inicial
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        fullBackgroundTexture = new Texture("full_background.png");

        // Instanciar el botón con texturas y configuración
        Texture buttonTexture = new Texture("button.png");
        Texture pressedTextureAtlas = new Texture("pressed.png");
        button = new Button(buttonTexture, pressedTextureAtlas, 100, 550, // Coordenadas (x, y)
                32, 32, // Dimensiones de la imagen
                20, 20); // Dimensiones de la hitbox
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Actualiza el estado del botón
        button.update(player.getBounds(), delta);

        batch.begin();
        button.render(batch, 32, 32); // Tamaño de la imagen: 32x32
        batch.end();
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        super.dispose();
        button.dispose(); // Liberar recursos del botón
        fullBackgroundTexture.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
