package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Platform {
    private Texture texture;
    private Rectangle bounds;

    public Platform(Texture texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
