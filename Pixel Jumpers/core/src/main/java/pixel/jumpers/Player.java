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
    private float deathStateTime = 0; // Tiempo desde que comenzó la animación de muerte
    private boolean isAttacking = false; // Indica si el jugador está atacando
    private float attackCooldown = 0;   // Temporizador para el cooldown del ataque
    private final float ATTACK_COOLDOWN_DURATION = 1.0f; // Cooldown en segundos
    private float attackStateTime = 0;  // Tiempo acumulado para la animación de ataque

    
    private PlayerAnimation animation;

    public Player(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        animation = new PlayerAnimation();
    }

    public void update(float deltaTime, Camera camera, Array<Platform> platforms, Array<Enemy> enemies) {
        handleInput(deltaTime, enemies);
        applyGravity(deltaTime);
        checkCollisions(platforms, enemies);
        
        if (!isAlive) {
            deathStateTime += deltaTime; // Avanzar el tiempo de la animación de muerte
            return;
        }
        
        // Reducir el cooldown del ataque
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
        
        if (isAttacking) {
            attackStateTime += deltaTime;

            // Terminar el ataque cuando la animación finaliza
            if (attackStateTime > animation.getAttackAnimationDuration()) {
                isAttacking = false;
                attackStateTime = 0;
            }
        }

        // Restringir al jugador dentro de la pantalla
        constrainPlayerToScreen(camera);

        // Actualizar la posición de la cámara
        camera.position.x = Math.max(camera.position.x, position.x + 32);
        camera.update();
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (!isAlive) {
            currentFrame = animation.getDeathFrame(deathStateTime);
        } else if (isHurt) {
            currentFrame = animation.getHurtFrame();
        } else if (isAttacking) {
            currentFrame = animation.getAttackFrame(attackStateTime);
        } else {
            currentFrame = animation.getCurrentFrame(
                Gdx.input.isKeyPressed(Input.Keys.A),
                Gdx.input.isKeyPressed(Input.Keys.D),
                isJumping, isDoubleJumping
            );
        }

        batch.draw(currentFrame, position.x, position.y, 64, 64);
    }


    private void handleInput(float deltaTime, Array<Enemy> enemies) {
        if (!isAlive || isAttacking) return;

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && attackCooldown <= 0) {
            isAttacking = true;
            attackStateTime = 0; // Reinicia el tiempo de la animación
            attackCooldown = ATTACK_COOLDOWN_DURATION; // Activa el cooldown
            attack(enemies); // Ejecuta el ataque
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

        // Define el área del ataque
        Rectangle attackBounds = new Rectangle(position.x - attackRange / 2, position.y, 64 + attackRange, 64);

        for (Enemy enemy : enemies) {
            if (attackBounds.overlaps(enemy.getBounds())) {
                enemy.takeDamage(25); // Inflige 25 de daño al enemigo
            }
        }
    }
    
    private void constrainPlayerToScreen(Camera camera) {
        float cameraLeftEdge = camera.position.x - camera.viewportWidth / 2f;
        float cameraRightEdge = camera.position.x + camera.viewportWidth / 2f;

        if (position.x < cameraLeftEdge) {
            position.x = cameraLeftEdge;
        }

        if (position.x + 64 > cameraRightEdge) { // 64 es el ancho del jugador
            position.x = cameraRightEdge - 64;
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
