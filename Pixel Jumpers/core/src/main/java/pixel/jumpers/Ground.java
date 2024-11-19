package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Ground {
    private Texture texture;
    private Rectangle bounds;

    // Usamos el constructor con el tamaño de la hitbox que quieras, pero no afectamos la textura
    public Ground(Texture texture, float x, float y, float imageWidth, float imageHeight, float hitboxWidth, float hitboxHeight) {
        this.texture = texture;

        // Centramos la hitbox en relación con la imagen
        float offsetX = (imageWidth - hitboxWidth) / 2;
        float offsetY = (imageHeight - hitboxHeight) / 2;

        // Establecemos las dimensiones y posición de la hitbox
        this.bounds = new Rectangle(x + offsetX, y + offsetY, hitboxWidth, hitboxHeight);
    }

    public void draw(SpriteBatch batch, float imageWidth, float imageHeight) {
        // Dibuja la imagen en su posición original con el tamaño especificado
        batch.draw(texture, bounds.x - (imageWidth - bounds.width) / 2, 
                   bounds.y - (imageHeight - bounds.height) / 2, 
                   imageWidth, imageHeight);
    }


    public Rectangle getBounds() {
        return bounds;
    }
}

