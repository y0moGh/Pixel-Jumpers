package pixel.jumpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Level3 extends BaseLevel {

    private final float PLAYER_START_X = 0; // Posición inicial del jugador
    private final float PLAYER_START_Y = 100;

    private Music backgroundMusic;
    private Music badEndingMusic;
    private Music goodEndingMusic;
    private Button button; // Declarar el botón como atributo de la clase
    private Button button2;
    private Button button3;

    private final float[][] PLATFORM_POSITIONS = {
            // Parte baja
            {0, 100}, {200, 110}, {400, 90}, {600, 105}, {800, 115}, {1000, 100}, {1200, 110},
            {1400, 95}, {1600, 105}, {1800, 100}, {2000, 110}, {2200, 100}, 

            // Altura media baja
            {150, 250}, {350, 260}, {550, 240}, {800, 250}, {890, 250}, {1150, 265}, 
            {1350, 240}, {1550, 255}, {1750, 250}, {1950, 265}, {2150, 255},

            // Altura media
            {50, 400}, {250, 390}, {450, 410}, {650, 395}, {850, 405}, {1050, 400}, 
            {1250, 415}, {1450, 390}, {1650, 400}, {1850, 410}, {2050, 405},

            // Altura media alta
            {100, 550}, {300, 540}, {500, 560}, {700, 550}, {900, 565}, {1100, 540}, 
            {1300, 560}, {1500, 550}, {1700, 565}, {1900, 545}, {2150, 550},

            // Parte alta
            {100, 800}, {300, 790}, {500, 810}, {700, 800}, {900, 805}, {1100, 790}, 
            {1300, 810}, {1500, 800}, {1700, 805}, {1900, 795}
        };

    private final float[][] PINCHOS_FLOTANTES_POSITIONS = {
    	    // Zona baja (entre plataformas y espaciados, altura aumentada)
    	    {100, 160}, {500, 170}, {900, 180}, {1300, 160}, {1700, 170},

    	    // Zona media baja (entre plataformas y espaciados, altura aumentada)
    	    {250, 300}, {650, 290}, {1050, 310}, {1450, 300}, {1850, 310},

    	    // Zona media (entre plataformas y espaciados, altura aumentada)
    	    {150, 440}, {550, 430}, {950, 440}, {1350, 450}, {1750, 430},

    	    // Zona media alta (entre plataformas y espaciados, altura aumentada)
    	    {200, 600}, {600, 590}, {1000, 610}, {1400, 600}, {1800, 590},

    	    // Zona alta (entre plataformas y espaciados, altura aumentada)
    	    {150, 840}, {550, 830}, {950, 850}, {1350, 840}, {1750, 830}
    	};


    private final int[] PLATFORMS_WITH_ENEMIES = {
            1, 5, 7, 9, 11, 13, 17, 19, 21, 23, 27, 31, 33, 37 // Más plataformas con enemigos
        };
    
	// Estatua (final del nivel)
	private final float ESTATUA_START_X = 830;
	private final float ESTATUA_START_Y = 270;
	
    private boolean timerStarted = true;
    private float timer = 60f; // Temporizador de 30 segundos
    private float backgroundChangeInterval = 2.5f; // Intervalo de cambio de fondo
    private float backgroundChangeTimer = 0f;
    private Texture semiGreyBackgroundTexture;
    private Texture fullBackgroundGreyTexture; // Declarar al nivel de clase
    private Texture colorBackgroundTexture; // Declarar al nivel de clase
    Texture temp2;
    
    private String[] messages = {
            "Si no me apresuro...",
            "El bosque será destruido por siempre..."
        };
        private int currentMessageIndex = 0;
        private float messageTimer = 2f;
        private float messageDisplayTime = 2f;

    private boolean levelComplete = false; // Bandera de victoria
    private boolean win = false;
    private BitmapFont defaultFont;
    private BitmapFont timerFont;
    private float delayTimer = 0f; // Variable para controlar el tiempo de espera
    private boolean isGameOverTriggered = false; // Para asegurarte de que solo inicie una vez
    private boolean badEndingMusicPlayed = false;
    private boolean goodEndingMusicPlayed = false;
    private boolean alreadyReseted = false;
    
    public Level3(Main game) {
        super(game);

        // Configuración inicial
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        colorBackgroundTexture = new Texture("full_background.png");
        semiGreyBackgroundTexture = new Texture("semi_grey_background_2.png");   
        fullBackgroundGreyTexture = new Texture("full_background_grey.png");
        fullBackgroundTexture = semiGreyBackgroundTexture;
        temp2 = colorBackgroundTexture;

        // Cargar música de fondo
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Level3.MP3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        
        badEndingMusic = Gdx.audio.newMusic(Gdx.files.internal("music/bad_ending.MP3"));
        goodEndingMusic = Gdx.audio.newMusic(Gdx.files.internal("music/win_sound.MP3"));
        
        // Inicializar la fuente predeterminada
        defaultFont = new BitmapFont();
        timerFont = new BitmapFont();
        timerFont.getData().setScale(1.5f); // Escala el tamaño del texto
        
        // Nueva estatua
        estatuas.add(new Estatua(estatuaFinalTexture, ESTATUA_START_X, ESTATUA_START_Y, estatua_img_x,  estatua_img_y, estatua_htb_x, estatua_htb_y));
        for (Estatua estatua : estatuas) {
            estatua.isBreakable = false;
        }
        
        // Instanciar el botón con texturas y configuración
        Texture buttonTexture = new Texture("button.png");
        Texture pressedTextureAtlas = new Texture("pressed.png");
        button = new Button(buttonTexture, pressedTextureAtlas, 135, 565, // Coordenadas (x, y)
                32, 32, // Dimensiones de la imagen
                20, 20); // Dimensiones de la hitbox
        button2 = new Button(buttonTexture, pressedTextureAtlas, 2235, 115, // Coordenadas (x, y)
                32, 32, // Dimensiones de la imagen
                20, 20); // Dimensiones de la hitbox
        button3 = new Button(buttonTexture, pressedTextureAtlas, 1485, 405, // Coordenadas (x, y)
                32, 32, // Dimensiones de la imagen
                20, 20); // Dimensiones de la hitbox

        // Agregar plataformas
        for (float[] pos : PLATFORM_POSITIONS) {
            platforms.add(new Platform(
                platformTexture, 
                pos[0], 
                pos[1], 
                platform_size_x, 
                platform_size_y
            ));
        }

        // Agregar enemigos en plataformas seleccionadas
        for (int i : PLATFORMS_WITH_ENEMIES) {
            float enemyX = PLATFORM_POSITIONS[i][0];
            float enemyY = PLATFORM_POSITIONS[i][1] + 20; // Enemigos sobre las plataformas
            enemies.add(new Enemy(
                enemyTexture, 
                enemyX, 
                enemyY, 
                enemy_size, 
                enemy_size, 
                40, 
                enemyX, 
                enemyX + 100
            ));
        }

        // Agregar pinchos flotantes
        for (float[] pos : PINCHOS_FLOTANTES_POSITIONS) {
            pinchos.add(new Pinchos(
                pinchosFlotantesTexture, 
                pos[0], 
                pos[1], 
                pincho_img_x, 
                pincho_img_y, 
                pincho_htb_x, 
                pincho_htb_y
            ));
        }

        // Agregar pinchos en el suelo (cobertura total)
        for (float x = 0; x <= 2200; x += 50) {
            pinchos.add(new Pinchos(
                pinchosTexture, 
                x, 
                30, 
                pincho_img_x, 
                pincho_img_y, 
                pincho_htb_x, 
                pincho_htb_y
            ));
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && !levelComplete) {
            resetLevel();
        }

        if (player.getY() < 0) {
            resetLevel();
        }
        
        // Lógica del temporizador al perder
        if (player.getHealth() == 0 && levelComplete && !win) {
            if (!isGameOverTriggered) {
                isGameOverTriggered = true; // Marcar que el temporizador ha comenzado
                delayTimer = 2f; // Configurar el temporizador a 2 segundos
            } else {
                delayTimer -= delta; // Reducir el tiempo
                if (delayTimer <= 0) {
                    game.setScreen(new Level1(game)); // Cambiar de nivel después de 2 segundos
                    badEndingMusic.stop();
                }
            }
        }
        else if(levelComplete && win) {
        	if (!isGameOverTriggered) {
                isGameOverTriggered = true; // Marcar que el temporizador ha comenzado
                delayTimer = 5f; // Configurar el temporizador a 2 segundos
            } else {
                delayTimer -= delta; // Reducir el tiempo
                if (delayTimer <= 0) {
                    game.setScreen(new End(game)); // Cambiar de nivel después de 2 segundos
                }
            }
        }

        boolean isPressed1 = button.update(player.getBounds(), delta);
        boolean isPressed2 = button2.update(player.getBounds(), delta);
        boolean isPressed3 = button3.update(player.getBounds(), delta);

        if (isPressed1 && isPressed2 && isPressed3) {
            for (Estatua estatua : estatuas) {
                estatua.isBreakable = true;
            }
        }

        if (timer > 0 && !levelComplete) {
            // Temporizador y cambio de fondo
            if ((timerStarted && currentMessageIndex >= messages.length) || timerStarted && alreadyReseted) {
                timer -= delta;

                if (timer <= 0) {
                    levelComplete = true;
                }
            }
            
            if (backgroundChangeTimer >= backgroundChangeInterval) {
                backgroundChangeTimer = 0f;
                Texture temp = fullBackgroundTexture;
                fullBackgroundTexture = temp2;
                temp2 = temp;
            }
            backgroundChangeTimer += delta;
        }

        batch.begin();

        if ((currentMessageIndex < messages.length) && !alreadyReseted) {
            defaultFont.draw(batch, messages[currentMessageIndex], player.getX(), player.getY() + 70);
            messageTimer -= delta;
            if (messageTimer <= 0) {
                currentMessageIndex++;
                messageTimer = messageDisplayTime;
            }
        } else if (timerStarted) {
            timerFont.draw(batch, "Tiempo restante: " + (int) timer, camera.position.x - 50, camera.position.y + 320);
        }

        if (estatuas.isEmpty()) {
        	winReset();
        	levelComplete = true;
        	win = true;
        }
        else if(levelComplete && !win) {
        	lostReset();
        }
        else if (!levelComplete) {
            button.render(batch, 32, 32); // Tamaño de la imagen: 32x32
            button2.render(batch, 32, 32);
            button3.render(batch, 32, 32);
        }

        batch.end();
        drawHealthBar(player);
    }
    
    private void winReset() {
    	enemies.clear();
    	pinchos.clear();
    	backgroundMusic.pause();
    	fullBackgroundTexture = colorBackgroundTexture;
    	
    	if(!goodEndingMusicPlayed) {
    		goodEndingMusic.play();
    		goodEndingMusicPlayed = true;
    	}
    }
    
    private void lostReset() {
    	fullBackgroundTexture = fullBackgroundGreyTexture; 	
    	platformTexture = platformGreyTexture;
        // Reiniciar plataformas
        platforms.clear();
        for (float[] pos : PLATFORM_POSITIONS) {
            platforms.add(new Platform(
                platformTexture, 
                pos[0], 
                pos[1], 
                platform_size_x, 
                platform_size_y
            ));
        }
        enemies.clear();
        backgroundMusic.pause();
        player.mute();
        // Reproducir música de "badEnding" solo una vez
        if (!badEndingMusicPlayed) {
            badEndingMusic.play();
            badEndingMusicPlayed = true; // Marcar que ya se reprodujo
        }
    }

    private void resetLevel() {
        // Reiniciar al jugador
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        player.reset();

        // Ajustar la posición inicial de la cámara
        camera.position.set(PLAYER_START_X + VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        camera.update();

        // Reiniciar plataformas
        platforms.clear();
        for (float[] pos : PLATFORM_POSITIONS) {
            platforms.add(new Platform(
                platformTexture, 
                pos[0], 
                pos[1], 
                platform_size_x, 
                platform_size_y
            ));
        }

        // Reiniciar enemigos
        enemies.clear();
        for (int i : PLATFORMS_WITH_ENEMIES) {
            float enemyX = PLATFORM_POSITIONS[i][0];
            float enemyY = PLATFORM_POSITIONS[i][1] + 20;
            enemies.add(new Enemy(
                enemyTexture, 
                enemyX, 
                enemyY, 
                enemy_size, 
                enemy_size, 
                40, 
                enemyX, 
                enemyX + 100
            ));
        }

        // Reiniciar pinchos
        pinchos.clear();
        for (float x = 0; x <= 3400; x += 50) {
            pinchos.add(new Pinchos(
                pinchosTexture, 
                x, 
                30, 
                pincho_img_x, 
                pincho_img_y, 
                pincho_htb_x, 
                pincho_htb_y
            ));
        }

        // Reiniciar pinchos flotantes
        for (float[] pos : PINCHOS_FLOTANTES_POSITIONS) {
            pinchos.add(new Pinchos(
                pinchosFlotantesTexture, 
                pos[0], 
                pos[1], 
                pincho_img_x, 
                pincho_img_y, 
                pincho_htb_x, 
                pincho_htb_y
            ));
        }
        
        button.reset();
        button2.reset();
        button3.reset();
        
        for (Estatua estatua : estatuas) {
            estatua.isBreakable = false;
        }
        
        backgroundChangeTimer = 0f;
        timerStarted = true;
        currentMessageIndex = 0;
        messageTimer = messageDisplayTime;
        levelComplete = false;
        alreadyReseted = true;
    }

    @Override
    public void show() {
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    @Override
    public void hide() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    @Override
    public void dispose() {
        backgroundMusic.dispose();
        super.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
