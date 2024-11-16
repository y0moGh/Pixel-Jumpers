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

    private boolean isHurt; // Nuevo estado para daño
    private float hurtTimer;
    private final float HURT_DURATION = 0.5f; // Duración del estado de daño

    private int health;

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
    }

    public void update(float deltaTime) {
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
        batch.draw(enemyAnimation.getCurrentFrame(movingLeft, movingRight, isHurt), x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
        } else {
            isHurt = true;
            hurtTimer = HURT_DURATION;
        }
    }

    public int getHealth() {
        return health;
    }
}
