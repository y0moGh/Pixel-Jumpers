package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    private float x, y;
    private float width, height;
    private float speed;
    private float minX, maxX; // Límites de movimiento
    private boolean movingLeft;

    private Rectangle bounds;
    private EnemyAnimation enemyAnimation;
    
    private int health;
    private boolean isHurt; // Nuevo estado para daño
    private float hurtTimer;
    private final float HURT_DURATION = 0.5f; // Duración del estado de daño
    private float deathTimer; // Temporizador para la animación de muerte
    private final float DEATH_DURATION = 1.0f; // Duración de la animación de muerte

    // Modificar el estado de la muerte
    private boolean isDead; // Indicador de si el enemigo está muerto

    // Actualizamos el constructor
    public Enemy(Texture texture, float x, float y, float width, float height, float speed, float minX, float maxX) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.minX = minX;
        this.maxX = maxX;
        this.movingLeft = speed < 0; // Determina la dirección inicial según la velocidad

        this.bounds = new Rectangle(x, y, width, height);
        this.enemyAnimation = new EnemyAnimation(); // Usa la animación del enemigo

        this.isHurt = false;
        this.hurtTimer = 0;
        this.health = 100; // Vida inicial del enemigo

        this.isDead = false; // Inicialmente no está muerto
        this.deathTimer = 0; // Inicializamos el temporizador de muerte
    }

    public void update(float deltaTime) {
    	if (isDead) {
    	    deathTimer += deltaTime;
    	    if (deathTimer >= DEATH_DURATION) {
    	        // Aquí no hacemos nada; la eliminación la maneja el `Main`
    	        return;
    	    }
    	    return;
    	}

        if (isHurt) {
            hurtTimer -= deltaTime;
            if (hurtTimer <= 0) {
                isHurt = false; // Finaliza el estado de daño
            }
            return; // Durante el estado de daño, el enemigo no se mueve
        }

        x += speed * deltaTime;

        // Cambiar dirección si alcanza los límites del rango de movimiento
        if (x < minX) {
            x = minX; // Asegurar que no pase del límite
            reverseDirection();
        } else if (x + width > maxX) {
            x = maxX - width; // Asegurar que no pase del límite
            reverseDirection();
        }

        bounds.setPosition(x, y);
    }


    private void reverseDirection() {
        speed = -speed; // Cambia la dirección de la velocidad
        movingLeft = speed < 0; // Actualiza la dirección del movimiento
    }

    public void draw(SpriteBatch batch) {
        boolean movingRight = !movingLeft;

        if (isDead) {
            // Mostrar la animación de muerte
            batch.draw(enemyAnimation.getCurrentFrame(movingLeft, movingRight, isHurt, isDead), x, y, width, height);
        } else {
            // Si no está muerto, mostramos las animaciones normales (caminar, estar herido, etc.)
            batch.draw(enemyAnimation.getCurrentFrame(movingLeft, movingRight, isHurt, isDead), x, y, width, height);
        }
    }



    public Rectangle getBounds() {
        return bounds;
    }

    public void takeDamage(int damage) {
        if (isDead) return; // Si ya está muerto, no hacer nada

        health -= damage;
        if (health <= 0) {
            health = 0;
            isDead = true; // Marcar como muerto
            deathTimer = 0; // Reiniciar el temporizador de muerte
        } else {
            isHurt = true;
            hurtTimer = HURT_DURATION;
        }
    }

    public boolean isReadyToRemove() {
        return isDead && deathTimer >= DEATH_DURATION; // Listo para eliminar después de la animación de muerte
    }


    public int getHealth() {
        return health;
    }
}
