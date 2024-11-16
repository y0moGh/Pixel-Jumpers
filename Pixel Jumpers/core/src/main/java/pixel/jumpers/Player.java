package pixel.jumpers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Vector2 position;
    private Vector2 velocity;
    private boolean isJumping;
    private boolean isDoubleJumping;
    private int health = 100;
    private boolean isHurt = false;
    private float hurtTimer = 0;
    private final float HURT_DURATION = 0.5f;
    private boolean isAlive = true;

    private PlayerAnimation animation;

    public Player(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        animation = new PlayerAnimation();
    }

    public void update(float deltaTime, Camera camera, Array<Platform> platforms, Array<Enemy> enemies) {
        handleInput(deltaTime);
        applyGravity(deltaTime);
        checkCollisions(platforms, enemies);

        // Manejar ataques
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attack(enemies);
        }
        
        // Asegurar que el jugador no salga del borde izquierdo visible
        float cameraLeftEdge = camera.position.x - camera.viewportWidth / 2f;
        if (position.x < cameraLeftEdge) {
            position.x = cameraLeftEdge;
        }
        
        // Asegurar que el jugador no salga del borde derecho visible
        float cameraRightEdge = camera.position.x + camera.viewportWidth / 2f;
        if (position.x + 64 > cameraRightEdge) { // `width` es el ancho del jugador
            position.x = cameraRightEdge - 64;   // Reajustar la posición
        }

        
        // Actualizar la posición de la cámara para que siga al jugador
        camera.position.x = Math.max(camera.position.x, position.x + 32);
        camera.update();
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;
        if (isHurt) {
            currentFrame = animation.getHurtFrame();
        } else if (!isAlive) {
            currentFrame = animation.getIdleFrame();
        } else {
            currentFrame = animation.getCurrentFrame(
                Gdx.input.isKeyPressed(Input.Keys.A),
                Gdx.input.isKeyPressed(Input.Keys.D),
                isJumping, isDoubleJumping
            );
        }
        batch.draw(currentFrame, position.x, position.y, 64, 64);
    }

    private void handleInput(float deltaTime) {
        if (!isAlive) return;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= 200 * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += 200 * deltaTime;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (!isJumping) {
                velocity.y = 500;
                isJumping = true;
            } else if (!isDoubleJumping) {
                velocity.y = 500;
                isDoubleJumping = true;
            }
        }
    }

    private void applyGravity(float deltaTime) {
        velocity.y -= 25;
        position.add(0, velocity.y * deltaTime);
        if (position.y <= 50) {
            position.y = 50;
            velocity.y = 0;
            isJumping = false;
            isDoubleJumping = false;
        }
    }

    private void checkCollisions(Array<Platform> platforms, Array<Enemy> enemies) {
        Rectangle playerBounds = new Rectangle(position.x, position.y, 64, 64);

        for (Platform platform : platforms) {
            if (playerBounds.overlaps(platform.getBounds()) && velocity.y <= 0) {
                position.y = platform.getBounds().y + platform.getBounds().height;
                velocity.y = 0;
                isJumping = false;
                isDoubleJumping = false;
                break;
            }
        }

        for (Enemy enemy : enemies) {
            if (playerBounds.overlaps(enemy.getBounds()) && isAlive && !isHurt) {
                health -= 10;
                isHurt = true;
                hurtTimer = HURT_DURATION;

                if (health <= 0) {
                    health = 0;
                    isAlive = false;
                }
            }
        }

        if (isHurt) {
            hurtTimer -= Gdx.graphics.getDeltaTime();
            if (hurtTimer <= 0) {
                isHurt = false;
            }
        }
    }
    
    public void attack(Array<Enemy> enemies) {
        float attackRange = 100f; // Rango de ataque en píxeles

        // Definir el área del ataque (puede ser un rectángulo o círculo)
        Rectangle attackBounds = new Rectangle(position.x - attackRange / 2, position.y, 64 + attackRange, 64);

        for (Enemy enemy : enemies) {
            if (attackBounds.overlaps(enemy.getBounds())) {
                enemy.takeDamage(25); // Inflige 20 de daño al enemigo
            }
        }
    }
    
    public float getX() {
        return position.x;
    }

    public float getWidth() {
        return 64; // El ancho del jugador
    }

    public void dispose() {
        animation.dispose();
    }
    
    public int getHealth() {
    	return health;
    }
    
    public void reset() {
        health = 110;  // Restablecer salud
        hurtTimer = 0;  // Reiniciar el temporizador de daño
        isHurt = false;  // Asegurarse de que no esté en estado de herido
    }
}
