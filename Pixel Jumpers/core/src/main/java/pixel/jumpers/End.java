package pixel.jumpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class End extends BaseLevel {
    private final float PLAYER_START_X = 0; // Posición inicial del jugador
    private final float PLAYER_START_Y = 0;
    private Texture enemySprite; // Sprite para el enemigo
    private BitmapFont font; // Fuente para los textos
    private GlyphLayout layout; // Para calcular el ancho/alto del texto
    private String[] messages = {
        "Gracias por todo!",
        "Eres un héroe!",
        "Nos salvaste!",
        "Nunca te olvidaremos!",
        "Eres nuestra inspiración!"
    };

    private Music goodEndingMusic;
    
    public End(Main game) {
        super(game);

        // Configuración inicial
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        fullBackgroundTexture = new Texture("full_background.png");
        
        goodEndingMusic = Gdx.audio.newMusic(Gdx.files.internal("music/win_music.MP3"));
        goodEndingMusic.setLooping(true);
        
        // Inicializar recursos
        enemySprite = new Texture("enemy_animations/enemy.png");
        font = new BitmapFont(); // Usa la fuente por defecto
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        goodEndingMusic.play();

        // Obtener batch y dimensiones de la ventana
        SpriteBatch batch = this.batch; // batch está en BaseLevel
        float viewportWidth = Gdx.graphics.getWidth();
        float viewportHeight = Gdx.graphics.getHeight();

        batch.begin();

        // Dibujar cartel gigante
        font.getData().setScale(3); // Escalar la fuente para texto grande
        String title = "Gracias por jugar Pixel Jumpers!";
        layout.setText(font, title);
        font.draw(batch, title, camera.position.x - 300, camera.position.y + 300);

        // Dibujar nombres más pequeños debajo del título
        font.getData().setScale(1.5f); // Escalar la fuente para los nombres
        String names = " Ulises De Lio Cameroni, Juan Ignacio Ferrando Villoslada y Sofia Brussa";
        layout.setText(font, names);
        font.draw(batch, names, camera.position.x - 340, camera.position.y + 240);

        // Dibujar sprite inicial y su mensaje
        font.getData().setScale(1f); // Escalar fuente para texto más pequeño
        String initialMessage = "Presiona R para volver a jugar";
        layout.setText(font, initialMessage);
        font.draw(batch, initialMessage, camera.position.x - 70, camera.position.y + 200);

        // Dibujar múltiples sprites y mensajes adicionales
        float spriteX = 200; // Posición inicial en X
        for (String message : messages) {
            // Dibujar sprite más grande
            batch.draw(enemySprite, spriteX, 50, 50, 50);

            // Dibujar mensaje encima del sprite
            layout.setText(font, message);
            float messageX = spriteX + (64 - layout.width) / 2; // Centrar el texto sobre el sprite
            float messageY = 120; // Justo encima del sprite
            font.draw(batch, message, messageX, messageY);

            // Incrementar posición X para el próximo sprite
            spriteX += 64 + 350; // Separación de 50 píxeles
        }

        batch.end();
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
        	game.setScreen(new Level1(game));
        	goodEndingMusic.stop();
        }
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        super.dispose();
        fullBackgroundTexture.dispose();
        enemySprite.dispose();
        font.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
