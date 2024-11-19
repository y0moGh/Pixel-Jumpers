package pixel.jumpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

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

    }
    
    @Override
    public void render(float delta) {
    	super.render(delta);
    	
        // Fijar el texto en la pantalla
        batch.setProjectionMatrix(viewport.getCamera().combined); // Cambiar a coordenadas de pantalla
        batch.begin();
        font.draw(batch, "W saltar", 100, 250); // Coordenadas fijas
        font.draw(batch, "W + W doble salto!", 400, 450); // Coordenadas fijas
        font.draw(batch, "Cuidado! Space atacar", 1200, 200); // Coordenadas fijas
        font.draw(batch, "Destruye la estatua para salvar el bosque", 1750, 400); // Coordenadas fijas
        font.draw(batch, "R para reiniciar", 1000, 500); // Coordenadas fijas
        batch.end();
    	
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetLevel();
        }
        
        // Cambiar a Level2 si no quedan estatuas
        if (estatuas.isEmpty()) {
            game.setScreen(new Level2(game));
        }

    }
    
    @Override
    public void dispose() {
    	super.dispose();
    	font.dispose();
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
