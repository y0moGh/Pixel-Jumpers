package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;

public class Nivel {
    private Texture backgroundTexture;
    private float length;

    public Nivel(String backgroundPath, float length) {
        this.backgroundTexture = new Texture(backgroundPath);
        this.length = length;
    }

    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    public float getLength() {
        return length;
    }

    public void dispose() {
        backgroundTexture.dispose();
    }
}
