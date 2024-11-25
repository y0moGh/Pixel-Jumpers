package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Estatua {
    private Texture texture;
    private Rectangle bounds;
    private boolean isDestroyed; // Indica si la estatua ha sido destruida
    public boolean isBreakable = true;
    
    public Estatua(Texture texture, float x, float y, float imageWidth, float imageHeight, float hitboxWidth, float hitboxHeight) {
        this.texture = texture;
        this.isDestroyed = false;

        float offsetX = (imageWidth - hitboxWidth) / 2;
        float offsetY = (imageHeight - hitboxHeight) / 2;
        this.bounds = new Rectangle(x + offsetX, y + offsetY, hitboxWidth, hitboxHeight);
    }

    public void draw(SpriteBatch batch, float imageWidth, float imageHeight) {
        if (!isDestroyed) {
            batch.draw(texture, bounds.x - (imageWidth - bounds.width) / 2,
                       bounds.y - (imageHeight - bounds.height) / 2,
                       imageWidth, imageHeight);
        }
    }

    public void takeDamage() {
        isDestroyed = true; // Marca la estatua como destruida
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}

