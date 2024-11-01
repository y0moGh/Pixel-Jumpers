package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerFrameAnimation {
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;

    public PlayerFrameAnimation() {
        // Cargar frames individuales
        walkLeftAnimation = createAnimationFromFiles("walk_left_", 5, 0.1f);  // 5 frames para el ejemplo
        walkRightAnimation = createAnimationFromFiles("walk_right_", 5, 0.1f); 
        idleAnimation = createAnimationFromFiles("idle_", 1, 0.1f);  // Solo un frame para idle

        stateTime = 0f;
    }

    private Animation<TextureRegion> createAnimationFromFiles(String filePrefix, int frameCount, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= frameCount; i++) {
            Texture texture = new Texture(filePrefix + i + ".png");
            frames.add(new TextureRegion(texture));
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    public TextureRegion getCurrentFrame(boolean movingLeft, boolean movingRight) {
        stateTime += com.badlogic.gdx.Gdx.graphics.getDeltaTime();

        if (movingLeft) {
            return walkLeftAnimation.getKeyFrame(stateTime, true);
        } else if (movingRight) {
            return walkRightAnimation.getKeyFrame(stateTime, true);
        } else {
            return idleAnimation.getKeyFrame(stateTime, true);
        }
    }
}
