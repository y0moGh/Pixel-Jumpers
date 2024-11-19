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
        
    private Rectangle hitbox; // Hitbox del jugador
    private float hitboxWidth = 32;  // Ancho reducido de la hitbox
    private float hitboxHeight = 32; // Alto reducido de la hitbox
    private float hitboxOffsetX = 8; // Desplazamiento horizontal (centrar la hitbox)
    private float hitboxOffsetY = 8; // Desplazamiento vertical

    public Player(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        animation = new PlayerAnimation();
        
        // Inicializa la hitbox centrada en la imagen
        hitbox = new Rectangle(
            position.x + hitboxOffsetX, 
            position.y + hitboxOffsetY, 
            hitboxWidth, 
            hitboxHeight
        );
    }

    public void update(float deltaTime, Camera camera, Array<Platform> platforms, Array<Enemy> enemies, Array<Pinchos> pinchos,  Array<Estatua> estatuas, Array<Ground> grounds) {
        handleInput(deltaTime, enemies, estatuas);
        applyGravity(deltaTime);
        checkCollisions(platforms, enemies, pinchos, grounds);

        // Actualiza la hitbox según la posición del personaje
        hitbox.setPosition(position.x + hitboxOffsetX, position.y + hitboxOffsetY);

        if (!isAlive) {
            deathStateTime += deltaTime;
            return;
        }

        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }

        if (isAttacking) {
            attackStateTime += deltaTime;
            if (attackStateTime > animation.getAttackAnimationDuration()) {
                isAttacking = false;
                attackStateTime = 0;
            }
        }

        constrainPlayerToScreen(camera);
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


    private void handleInput(float deltaTime, Array<Enemy> enemies,  Array<Estatua> estatuas) {
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
            attack(enemies, estatuas); // Ejecuta el ataque
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

    private void checkCollisions(Array<Platform> platforms, Array<Enemy> enemies, Array<Pinchos> pinchos, Array<Ground> grounds) {
        // Colisiones con plataformas
        for (Platform platform : platforms) {
            if (hitbox.overlaps(platform.getBounds()) && velocity.y <= 0) {
                position.y = platform.getBounds().y + platform.getBounds().height - hitboxOffsetY;
                velocity.y = 0;
                isJumping = false;
                isDoubleJumping = false;
                break;
            }
        }

        // Colisiones con enemigos
        for (Enemy enemy : enemies) {
            if (hitbox.overlaps(enemy.getBounds()) && isAlive && !isHurt) {
                health -= 10;
                isHurt = true;
                hurtTimer = HURT_DURATION;

                if (health <= 0) {
                    health = 0;
                    isAlive = false;
                }
            }
        }

        // Colisiones con pinchos
        for (Pinchos pincho : pinchos) {
            if (hitbox.overlaps(pincho.getBounds())) {
                if (velocity.y <= 0) { // Si el jugador cae o está sobre los pinchos
                    position.y = pincho.getBounds().y + pincho.getBounds().height - hitboxOffsetY;
                    velocity.y = 0;
                    isJumping = false;
                    isDoubleJumping = false;
                }

                if (!isHurt) { // Daño al colisionar
                    health -= 100;
                    isHurt = true;
                    hurtTimer = HURT_DURATION;

                    if (health <= 0) {
                        health = 0;
                        isAlive = false;
                    }
                }
                break;
            }
        }

        // Temporizador para el estado de daño
        if (isHurt) {
            hurtTimer -= Gdx.graphics.getDeltaTime();
            if (hurtTimer <= 0) {
                isHurt = false;
            }
        }
    }





    
    public void attack(Array<Enemy> enemies, Array<Estatua> estatuas) {
        float attackRange = 100f; // Rango de ataque en píxeles

        // Define el área del ataque
        Rectangle attackBounds = new Rectangle(position.x - attackRange / 2, position.y, 64 + attackRange, 64);

        // Daño a enemigos
        for (Enemy enemy : enemies) {
            if (attackBounds.overlaps(enemy.getBounds())) {
                enemy.takeDamage(25); // Inflige 25 de daño al enemigo
            }
        }

        // Daño a estatuas
        for (Estatua estatua : estatuas) {
            if (attackBounds.overlaps(estatua.getBounds())) {
                estatuas.removeValue(estatua, true); // Destruir la estatua
                break; // Detenemos el bucle si una estatua es destruida
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
    
    public float getY() {
        return position.y;
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
        health = 100;  // Restablecer salud
        hurtTimer = 0;  // Reiniciar el temporizador de daño
        isHurt = false;  // Asegurarse de que no esté en estado de herido
    }
}
