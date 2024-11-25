package pixel.jumpers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
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
    private final float ATTACK_COOLDOWN_DURATION = 0f; // Cooldown en segundos
    private float attackStateTime = 0;  // Tiempo acumulado para la animación de ataque
    private boolean isFacingRight = true;
    
    private PlayerAnimation animation;
        
    private Rectangle hitbox; // Hitbox del jugador
    private float hitboxWidth = 32;  // Ancho reducido de la hitbox
    private float hitboxHeight = 32; // Alto reducido de la hitbox
    private float hitboxOffsetX = 8; // Desplazamiento horizontal (centrar la hitbox)
    private float hitboxOffsetY = 8; // Desplazamiento vertical
    
    private boolean isBeingPushed = false; // Indica si el jugador est  siendo empujado
    private float pushTimer = 0;           // Temporizador para la duraci n del empuje
    private final float PUSH_DURATION = 0.2f; // Duraci n del empuje en segundos
    public boolean move = true;

    // Sonidos
    private Sound jumpSound;
    private Sound attackSound;
    private Sound airAttackSound;
    private Sound walkSound;
    private Sound deathSound;
    private Sound hurtSound;
    
    private long walkSoundId = -1; // ID para el sonido actual
    private boolean isOnGround = true; // Indica si el jugador está en el suelo
    private boolean isOnPlatform = false;
    private long deathSoundId = -1;
    private long hurtSoundId = -1;
    
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
        
        // Cargar sonidos
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));
        attackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/attack.wav"));
        airAttackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Air_Attack.WAV"));
        walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/walk.wav"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/death.wav"));
        hurtSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hurt.wav"));
    }

    public void update(float deltaTime, Camera camera, Array<Platform> platforms, Array<Enemy> enemies, Array<Pinchos> pinchos, Array<Estatua> estatuas, Array<Ground> grounds) {
        applyGravity(deltaTime);
        checkCollisions(platforms, enemies, pinchos, grounds);
        handleInput(deltaTime, enemies, estatuas);

        // Actualiza la hitbox según la posición del personaje
        hitbox.setPosition(position.x + hitboxOffsetX, position.y + hitboxOffsetY);

        if (!isAlive) {
            deathStateTime += deltaTime;
            if (walkSoundId != -1) {
                walkSound.stop(walkSoundId); // Detener el sonido del caminar
                walkSoundId = -1;
            }

            // Reproducir sonido de muerte solo una vez
            if (deathSoundId == -1) {
                deathSoundId = deathSound.play(); // Usa play() para evitar bucles
            }
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

        if (isBeingPushed) {
            // Aplica la velocidad del empuje a la posición
            position.x += velocity.x * deltaTime;

            // Reduce el temporizador del empuje
            pushTimer -= deltaTime;

            if (pushTimer <= 0) {
                isBeingPushed = false;
                velocity.x = 0; // Detener el movimiento después del empuje
            }

            // Salimos para evitar que se procesen otros movimientos
            return;
        }
        
     // Temporizador para el estado de daño
        if (isHurt && isAlive) { // Solo procesar daño si está vivo
            if (hurtSoundId == -1) {
                hurtSoundId = hurtSound.play(); // Usa play() para evitar bucles
            }
            hurtTimer -= Gdx.graphics.getDeltaTime();
            if (hurtTimer <= 0) {
                isHurt = false;
                hurtSoundId = -1; // Resetear el ID del sonido al finalizar
            }
        }


        constrainPlayerToScreen(camera);
        camera.position.x = Math.max(camera.position.x, position.x + 32);
        camera.update();
    }



    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;
        
        if (move) {
            if (!isAlive) {
                currentFrame = isFacingRight
                    ? animation.getDeathFrame(deathStateTime)
                    : animation.getDeathLeftFrame(deathStateTime);
            } else if (isHurt) {
                currentFrame = isFacingRight
                    ? animation.getHurtFrame()
                    : animation.getHurtLeftFrame();
            } else if (isAttacking) {
                currentFrame = isFacingRight
                    ? animation.getAttackFrame(attackStateTime)
                    : animation.getAttackLeftFrame(attackStateTime);
            } else if (isJumping || isDoubleJumping) {
                currentFrame = animation.getJumpFrame(isFacingRight);
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                // Caminar hacia la izquierda
                currentFrame = animation.getCurrentFrame(true, false, false, false);
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                // Caminar hacia la derecha
                currentFrame = animation.getCurrentFrame(false, true, false, false);
            } else {
                // Animación de reposo
                currentFrame = isFacingRight
                    ? animation.getIdleFrame()
                    : animation.getIdleLeftFrame();
            }

            batch.draw(currentFrame, position.x, position.y, 64, 64);
        	
        }
    }

    protected void handleInput(float deltaTime, Array<Enemy> enemies, Array<Estatua> estatuas) {
        if(move) {
        	if (!isAlive || isAttacking) return; // Bloquea entrada si está muerto o atacando

            boolean walking = false;
            
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                position.x -= 200 * deltaTime;
                isFacingRight = false;
                walking = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                position.x += 200 * deltaTime;
                isFacingRight = true;
                walking = true;
            }
            if (walking && (isOnGround || isOnPlatform)) {
                if (walkSoundId == -1) { // Si el sonido no está reproduciéndose
                    walkSoundId = walkSound.loop(); // Reproduce en bucle
                }
            } else {
                if (walkSoundId != -1) { // Detenemos el sonido si no estamos caminando o en el suelo
                    walkSound.stop(walkSoundId);
                    walkSoundId = -1;
                }
            }
            }
            
	        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
	            if (!isJumping) {
	                velocity.y = 500;
	                isJumping = true;
	                isOnPlatform = false; // Salimos de la plataforma al saltar
	                jumpSound.play(0.5f);
	            } else if (!isDoubleJumping) {
	                velocity.y = 500;
	                isDoubleJumping = true;
	                jumpSound.play(0.5f);
	            }
	        }

	        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && attackCooldown <= 0) {
	            isAttacking = true;
	            attackStateTime = 0; // Reinicia el tiempo de la animación
	            attackCooldown = ATTACK_COOLDOWN_DURATION; // Activa el cooldown

	            boolean hitSomething = attack(enemies, estatuas); // Ejecuta el ataque y verifica impacto

	            if (hitSomething) {
	                attackSound.play(); // Sonido de impacto
	            } else {
	                airAttackSound.play(0.5f); // Sonido de ataque al aire
	            }
	        }   	
        }


    private void applyGravity(float deltaTime) {
        velocity.y -= 25;
        position.add(0, velocity.y * deltaTime);
        isOnGround = false;
        
        if (position.y <= 50) {
            position.y = 50;
            velocity.y = 0;
            isJumping = false;
            isDoubleJumping = false;
            isOnGround = true; // Marca como en el suelo si toca la base
            isOnPlatform = false;
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
                isOnPlatform = true;
                break;
            }
        }
        
     // Colisiones con enemigos
        for (Enemy enemy : enemies) {
            if (hitbox.overlaps(enemy.getBounds()) && isAlive && !isHurt && !isBeingPushed) {
                // Reducir salud
                health -= 10;
                isHurt = true;
                hurtTimer = HURT_DURATION;

                // Determinar la direcci n del empuje
                float pushDirection = position.x < enemy.getX() ? -1 : 1; // Empujar hacia la izquierda o derecha

                // Aplicar empuje
                velocity.x = pushDirection * 180; // Magnitud del empuje
                isBeingPushed = true;
                pushTimer = PUSH_DURATION;

                // Verificar si el jugador muere
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

                if (isAlive) { // Solo manejar daño si el jugador está vivo
                    health -= 100;

                    if (health <= 0) { // Si la salud llega a 0, el jugador muere
                        health = 0;
                        isAlive = false;

                        // Reproduce el sonido de daño solo una vez al morir
                        if (hurtSoundId == -1) {
                            hurtSoundId = hurtSound.play();
                        }

                        // Opcional: puedes añadir efectos adicionales al morir
                        isHurt = false; // Resetea el estado de daño
                        hurtTimer = 0;  // Reinicia el temporizador de daño
                    } else if (!isHurt) { // Si no muere, pero recibe daño
                        isHurt = true;
                        hurtTimer = HURT_DURATION;

                        // Reproduce el sonido de daño
                        if (hurtSoundId == -1) {
                            hurtSoundId = hurtSound.play();
                        }
                    }
                }
                break; // Sal del bucle si hay colisión
            }
        }

    }

    public boolean attack(Array<Enemy> enemies, Array<Estatua> estatuas) {
        float attackRange = 30f; // Rango de ataque en píxeles
        boolean hitSomething = false; // Indica si el ataque golpeó algo

        // Define el área del ataque
        Rectangle attackBounds = new Rectangle(position.x - attackRange / 2, position.y, 64 + attackRange, 64);

        // Daño y empuje a enemigos
        for (Enemy enemy : enemies) {
            if (attackBounds.overlaps(enemy.getBounds())) {
                hitSomething = true; // Se golpeó un enemigo
                enemy.takeDamage(25); // Inflige 25 de daño al enemigo

                // Calcula la dirección del empuje
                float pushDirection = position.x < enemy.getX() ? 1 : -1; // Empuja hacia la derecha o izquierda

                // Aplica el empuje al enemigo
                enemy.applyPush(pushDirection, 100, 0.2f); // Dirección, velocidad y duración del empuje
            }
        }

        // Daño a estatuas
        for (Estatua estatua : estatuas) {
            if (attackBounds.overlaps(estatua.getBounds()) && estatua.isBreakable) {
                hitSomething = true; // Se golpeó una estatua
                estatuas.removeValue(estatua, true); // Destruir la estatua
                break; // Detenemos el bucle si una estatua es destruida
            }
        }

        return hitSomething; // Retorna si se golpeó algo
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
    
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, hitboxWidth, hitboxHeight);
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
        isAlive = true;  // Asegurarse de que el personaje esté vivo
        deathStateTime = 0; // Reiniciar el tiempo de la animación de muerte

        // Detener el sonido de muerte si está activo
        if (deathSoundId != -1) {
            deathSound.stop(deathSoundId); // Detener sonido
            deathSoundId = -1;             // Resetear el ID del sonido
        }
    }

}