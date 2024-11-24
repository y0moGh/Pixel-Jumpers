package pixel.jumpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;


public class Level1 extends BaseLevel {
	// Inicializacion de las posiciones iniciales de los objetos
    private final float PLAYER_START_X = 0;
    private final float PLAYER_START_Y = 0;

    private final float[] PLATFORM_START_X = {
    	    200, 300, 600, 900, 1200, 1500, 1700, 2000, 2100
    	};
    	private final float[] PLATFORM_START_Y = {
    	    100, 150, 200, 250, 300, 200, 150, 250, 100
    	};

    	// Pinchos (más obstáculos en áreas críticas)
    	private final float[] PINCHO_START_X = {
    	    400, 450, 500, 550, 600, 650, 1000, 1050, 1100, 1500, 1550, 1600, 1650
    	};
    	private final float[] PINCHO_START_Y = {
    	    30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30
    	};

    	// Enemigos (ubicados en zonas estratégicas)
    	private final float ENEMY_1_START_X = 1200;
    	private final float ENEMY_1_END_X = 1500;
    	private final float ENEMY_1_START_Y = 50;

    	private final float ENEMY_2_START_X = 1700;
    	private final float ENEMY_2_END_X = 1800;
    	private final float ENEMY_2_START_Y = 170;

    	private final float ENEMY_3_START_X = 2100;
    	private final float ENEMY_3_END_X = 2200;
    	private final float ENEMY_3_START_Y = 120;

    	// Estatua (final del nivel)
    	private final float ESTATUA_START_X = 1990;
    	private final float ESTATUA_START_Y = 260;
    
    	private BitmapFont font; // Inicializacion de la fuente para el texto
    
        private boolean isMenuActive = true; // Estado inicial en modo menú
        private Stage stage; // Para manejar la UI del menú   
        
        // Nueva textura para el título
        private Texture tituloTexture;
        private Texture playTexture;

        // Musicas
        private Music menuMusic;
        private Music level1Music;
        private boolean isLevelMusicFadingOut = false;
        
    public Level1(Main game) {
        super(game); // Heredamos el constructor de la clase padre
        
        // Instanciamos todos los objetos a utilizar en este nivel
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        fullBackgroundTexture = new Texture("full_background_grey.png");

        for (int i = 0; i < PLATFORM_START_X.length; i++) {
            platforms.add(new Platform(platformGreyTexture, PLATFORM_START_X[i], PLATFORM_START_Y[i], platform_size_x, platform_size_y));
        }    

        for (int i = 0; i < PINCHO_START_X.length; i++) {
            pinchos.add(new Pinchos(pinchosTexture, PINCHO_START_X[i], PINCHO_START_Y[i], pincho_img_x, pincho_img_y, pincho_htb_x, pincho_htb_y));
        }
        
        pinchos.add(new Pinchos(pinchosFlotantesTexture, 800, 150, pincho_img_x, pincho_img_y, pincho_htb_x, pincho_htb_y));

        estatuas.add(new Estatua(estatuaTexture, ESTATUA_START_X, ESTATUA_START_Y, estatua_img_x, estatua_img_y, estatua_htb_x, estatua_htb_y));
        
        enemies.add(new Enemy(enemyTexture, ENEMY_1_START_X, ENEMY_1_START_Y, enemy_size, enemy_size, enemy_speed, ENEMY_1_START_X, ENEMY_1_END_X));
        enemies.add(new Enemy(enemyTexture, ENEMY_2_START_X, ENEMY_2_START_Y, enemy_size, enemy_size, 70, ENEMY_2_START_X, ENEMY_2_END_X));
        enemies.add(new Enemy(enemyTexture, ENEMY_3_START_X, ENEMY_3_START_Y, enemy_size, enemy_size, 70, ENEMY_3_START_X, ENEMY_3_END_X));
        
        font = new BitmapFont(); // Usa la fuente predeterminada
        font.setColor(Color.WHITE); // Cambia el color si lo deseas
        font.getData().setScale(1.5f); // Escala el tamaño del texto
        
        // Cargar la textura del título
        tituloTexture = new Texture("titulo.png");
        playTexture = new Texture("play.png");
        
        // Crear el menú inicial
        createMenu();
        
        // Cargar las musicas
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Menu.MP3"));
        level1Music = Gdx.audio.newMusic(Gdx.files.internal("music/Level1.MP3"));

        // Configuración inicial
        menuMusic.setLooping(true);
        level1Music.setLooping(true);

        // Inicia la música del menú
        menuMusic.play();

    }
    
    
    private void createMenu() {
        stage = new Stage(viewport); // Usar el viewport del nivel
        Gdx.input.setInputProcessor(stage); // Configurar el procesador de entrada para el menú

        Skin skin = new Skin();
        BitmapFont buttonFont = new BitmapFont();
        skin.add("default-font", buttonFont);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default-font");
        skin.add("default", style);

        TextButton playButton = new TextButton("Jugar", skin);
        playButton.setSize(200, 80);
        playButton.setPosition((VIRTUAL_WIDTH - playButton.getWidth()) / 2 + 35, 200);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isMenuActive = false; // Salir del menú
                Gdx.input.setInputProcessor(null); // Habilitar entrada estándar del juego

                // Iniciar transición de música
                new Thread(() -> {
                    // Reducir volumen del menú gradualmente
                    for (float vol = 1.0f; vol > 0; vol -= 0.1f) {
                        menuMusic.setVolume(vol);
                        try {
                            Thread.sleep(100); // Esperar 100ms entre decrementos
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    menuMusic.stop();

                    // Iniciar música del nivel
                    level1Music.play();
                }).start();
            }
        });

        stage.addActor(playButton);
    }

    
    @Override
    public void render(float delta) {
        super.render(delta);

        if (isMenuActive) {
            // Lógica del menú (ya existente)
        	player.move = false;
            stage.act(delta);
            stage.draw();
            batch.begin();
            float scaledWidth = tituloTexture.getWidth() * 0.5f;
            float scaledHeight = tituloTexture.getHeight() * 0.5f;
            batch.draw(tituloTexture, 250, 180, scaledWidth, scaledHeight);
            batch.draw(playTexture, 600, 180);
            batch.end();
            return;
        }

        player.move = true;

        // Si no quedan estatuas y la música aún no está en *fade out*
        if (estatuas.isEmpty() && !isLevelMusicFadingOut) {
            isLevelMusicFadingOut = true; // Marcar que comenzó el *fade out*
            new Thread(() -> {
                for (float vol = 1.0f; vol > 0; vol -= 0.1f) {
                    level1Music.setVolume(vol);
                    try {
                        Thread.sleep(25); // *Fade out* más rápido
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                level1Music.stop();
            }).start();
        }

        // Cambiar a Level2 después del *fade out*
        if (estatuas.isEmpty() && isLevelMusicFadingOut && !level1Music.isPlaying()) {
            game.setScreen(new Level2(game));
        }

        // Resto de la lógica del nivel (ya existente)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetLevel();
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        font.draw(batch, "W saltar", 100, 250);
        font.draw(batch, "W + W doble salto!", 400, 450);
        font.draw(batch, "Cuidado! Space atacar", 1200, 200);
        font.draw(batch, "Destruye la estatua para salvar el bosque", 1750, 400);
        font.draw(batch, "R para reiniciar", 1000, 500);
        batch.end();

        drawHealthBar(player);
    }

    
    @Override
    public void dispose() {
    	super.dispose();
    	font.dispose();
        stage.dispose(); // Liberar recursos de la UI
        menuMusic.dispose();
        level1Music.dispose();
    }

    private void resetLevel() {
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        player.reset();
        
        // Ajustar la posición inicial de la cámara al inicio del nivel
        camera.position.set(PLAYER_START_X + VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        camera.update();

        platforms.clear();
        for (int i = 0; i < PLATFORM_START_X.length; i++) {
            platforms.add(new Platform(platformGreyTexture, PLATFORM_START_X[i], PLATFORM_START_Y[i], platform_size_x, platform_size_y));
        }
        
        pinchos.clear();
        for (int i = 0; i < PINCHO_START_X.length; i++) {
            // Crear pinchos con las dimensiones originales
            pinchos.add(new Pinchos(pinchosTexture, PINCHO_START_X[i], PINCHO_START_Y[i], pincho_img_x, pincho_img_y, pincho_htb_x, pincho_htb_y));
        }
        
        pinchos.add(new Pinchos(pinchosFlotantesTexture, 800, 150, pincho_img_x, pincho_img_y, 100, 30));
        
        estatuas.clear();
        estatuas.add(new Estatua(estatuaTexture, ESTATUA_START_X, ESTATUA_START_Y, estatua_img_x, estatua_img_y, estatua_htb_x, estatua_htb_y));
        
        enemies.clear();
        enemies.add(new Enemy(enemyTexture, ENEMY_1_START_X, ENEMY_1_START_Y, enemy_size, enemy_size, enemy_speed, ENEMY_1_START_X, ENEMY_1_END_X));
        enemies.add(new Enemy(enemyTexture, ENEMY_2_START_X, ENEMY_2_START_Y, enemy_size, enemy_size, 70, ENEMY_2_START_X, ENEMY_2_END_X));
        enemies.add(new Enemy(enemyTexture, ENEMY_3_START_X, ENEMY_3_START_Y, enemy_size, enemy_size, 70, ENEMY_3_START_X, ENEMY_3_END_X));
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
