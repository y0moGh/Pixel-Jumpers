package pixel.jumpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Level2 extends BaseLevel {
    private final float PLAYER_START_X = 0;
    private final float PLAYER_START_Y = 100;

    private final float[][] PLATFORM_POSITIONS = {
        {0, 100}, {300, 200}, {600, 300}, {900, 150}, {1200, 250}, {1500, 200}, {1600, 350}, {2000, 250}
    };

    private final float[][] PINCHOS_FLOTANTES_POSITIONS = {
        {450, 220}, {750, 320}, {1050, 180}, {1400, 370}, {1800, 270}
    };
    
    private Music backgroundMusic; // Música de fondo
    private boolean isLevelMusicFadingOut = false;

    public Level2(Main game) {
        super(game);
        shapeRenderer = new ShapeRenderer();
        player = new Player(PLAYER_START_X, PLAYER_START_Y);

        fullBackgroundTexture = new Texture("semi_grey_background_2.png");
        
        // Cargar la música
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Level2.MP3"));
        backgroundMusic.setLooping(true); // Configurar para que se repita en bucle

        // Agregar plataformas
        for (float[] pos : PLATFORM_POSITIONS) {
            platforms.add(new Platform(
                platformSemiGreyTexture, 
                pos[0], 
                pos[1], 
                platform_size_x, 
                platform_size_y
            ));
        }

        // Agregar enemigos en plataformas seleccionadas
        int[] platformsWithEnemies = {2, 4, 6}; // Solo en estas plataformas habrá enemigos
        for (int i : platformsWithEnemies) {
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

        // Agregar la estatua al final (cerca de X = 2200)
        float finalPlatformX = PLATFORM_POSITIONS[PLATFORM_POSITIONS.length - 1][0];
        float finalPlatformY = PLATFORM_POSITIONS[PLATFORM_POSITIONS.length - 1][1];
        estatuas.add(new Estatua(
            estatuaTexture, 
            finalPlatformX - 10, 
            finalPlatformY + 10, 
            estatua_img_x, 
            estatua_img_y, 
            estatua_htb_x, 
            estatua_htb_y
        ));

        // Agregar pinchos en todo el suelo
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
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // Lógica para Level2
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetLevel();
        }
        
        // Cambiar a Level3 si no quedan estatuas
        // Si no quedan estatuas y la música aún no está en *fade out*
        if (estatuas.isEmpty() && !isLevelMusicFadingOut) {
            isLevelMusicFadingOut = true; // Marcar que comenzó el *fade out*
            new Thread(() -> {
                for (float vol = 1.0f; vol > 0; vol -= 0.1f) {
                	backgroundMusic.setVolume(vol);
                    try {
                        Thread.sleep(50); // *Fade out* más rápido
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                backgroundMusic.stop();
            }).start();
        }

        // Cambiar a Level2 después del *fade out*
        if (estatuas.isEmpty() && isLevelMusicFadingOut && !backgroundMusic.isPlaying()) {
            game.setScreen(new Level3(game));
        }
        
        drawHealthBar(player);
    }
    
    private void resetLevel() {
        // Reiniciar al jugador
        player = new Player(PLAYER_START_X, PLAYER_START_Y);
        player.reset();

        // Ajustar la posición inicial de la cámara al inicio del nivel
        camera.position.set(PLAYER_START_X + VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        camera.update();

        // Reiniciar plataformas, enemigos, pinchos, etc.
        platforms.clear();
        for (float[] pos : PLATFORM_POSITIONS) {
            platforms.add(new Platform(
                platformSemiGreyTexture,
                pos[0],
                pos[1],
                platform_size_x,
                platform_size_y
            ));
        }

        // Reiniciar enemigos
        enemies.clear();
        int[] platformsWithEnemies = {2, 4, 6};
        for (int i : platformsWithEnemies) {
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

        // Reiniciar pinchos en el suelo
        pinchos.clear();
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

        // Reiniciar estatua
        estatuas.clear();
        float finalPlatformX = PLATFORM_POSITIONS[PLATFORM_POSITIONS.length - 1][0];
        float finalPlatformY = PLATFORM_POSITIONS[PLATFORM_POSITIONS.length - 1][1];
        estatuas.add(new Estatua(
            estatuaTexture,
            finalPlatformX - 10,
            finalPlatformY + 10,
            estatua_img_x,
            estatua_img_y,
            estatua_htb_x,
            estatua_htb_y
        ));
    }


    @Override
    public void show() {
        // Reproducir música al mostrar el nivel
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    @Override
    public void hide() {
        // Detener la música al ocultar el nivel
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }
    
    @Override
    public void dispose() {
        // Liberar recursos de la música
        backgroundMusic.dispose();
        super.dispose();
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
