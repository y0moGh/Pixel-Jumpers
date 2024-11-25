package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Button {
    private Texture buttonTexture; // Textura del botón normal
    private Animation<TextureRegion> pressedAnimation; // Animación para el estado presionado
    private float animationTime; // Tiempo para animación

    private Rectangle bounds; // Hitbox del botón
    private boolean isPressed; // Indica si el botón ha sido presionado
    private boolean animationFinished; // Indica si la animación llegó al último frame

    public Button(Texture buttonTexture, Texture pressedTextureAtlas, float x, float y, 
                  float imageWidth, float imageHeight, float hitboxWidth, float hitboxHeight) {
        this.buttonTexture = buttonTexture;

        // Crear animación a partir del atlas de sprites
        TextureRegion[][] frames = TextureRegion.split(pressedTextureAtlas, 
                                                       pressedTextureAtlas.getWidth() / 3, // Asumiendo 3 frames horizontales
                                                       pressedTextureAtlas.getHeight());
        pressedAnimation = new Animation<>(0.035f, frames[0]); // Duración de cada frame: 0.1s
        pressedAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        // Centramos la hitbox en relación con la imagen
        float offsetX = (imageWidth - hitboxWidth) / 2;
        float offsetY = (imageHeight - hitboxHeight) / 2;

        bounds = new Rectangle(x + offsetX, y + offsetY, hitboxWidth, hitboxHeight);
        isPressed = false;
        animationFinished = false;
        animationTime = 0f;
    }

    public boolean update(Rectangle playerBounds, float delta) {
        if (bounds.overlaps(playerBounds) && !animationFinished) {
            isPressed = true;
            animationTime += delta;

            // Verificar si la animación llegó al último frame
            if (pressedAnimation.isAnimationFinished(animationTime)) {
                animationFinished = true;
            }
        }
        return isPressed;
    }

    public void render(SpriteBatch batch, float imageWidth, float imageHeight) {
        if (isPressed) {
            // Si la animación terminó, dibuja el último frame
            TextureRegion currentFrame = animationFinished 
                ? pressedAnimation.getKeyFrame(pressedAnimation.getAnimationDuration()) 
                : pressedAnimation.getKeyFrame(animationTime);

            batch.draw(currentFrame, bounds.x - (imageWidth - bounds.width) / 2, 
                       bounds.y - (imageHeight - bounds.height) / 2, 
                       imageWidth, imageHeight);
        } else {
            // Dibujar textura normal
            batch.draw(buttonTexture, bounds.x - (imageWidth - bounds.width) / 2, 
                       bounds.y - (imageHeight - bounds.height) / 2, 
                       imageWidth, imageHeight);
        }
    }

    public void reset() {
        // Reiniciar el estado del botón
        isPressed = false;
        animationFinished = false;
        animationTime = 0f;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        // Liberar recursos
        buttonTexture.dispose();
    }
}
