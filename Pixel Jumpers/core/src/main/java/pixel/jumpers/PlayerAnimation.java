package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerAnimation {
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private float stateTime;

    public PlayerAnimation() {
        // Cargar sprites o texturas para cada animación
        Texture walkSheetLeft = new Texture("run_left.png");
        Texture walkSheetRight = new Texture("run_right.png");
        Texture idleSheet = new Texture("Idle.png");
        Texture jumpSheet = new Texture("Jump.png");

        // Dividir en frames y crear animaciones (asume que cada frame es de 128x128 píxeles)
        walkLeftAnimation = createAnimationFromSheet(walkSheetLeft, 128, 128, 0.1f);
        walkRightAnimation = createAnimationFromSheet(walkSheetRight, 128, 128, 0.1f);
        idleAnimation = createAnimationFromSheet(idleSheet, 128, 128, 0.1f);
        jumpAnimation = createAnimationFromSheet(jumpSheet, 128, 128, 0.1f);

        stateTime = 0f;
    }

    private Animation<TextureRegion> createAnimationFromSheet(Texture sheet, int frameWidth, int frameHeight, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight);
        Array<TextureRegion> frames = new Array<>();
        for (TextureRegion[] row : tmp) {
            for (TextureRegion frame : row) {
                frames.add(frame);
            }
        }

        if (frames.size == 0) {
            throw new IllegalArgumentException("La hoja de sprites no contiene frames o no se ha cargado correctamente: " + sheet);
        }

        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    public TextureRegion getCurrentFrame(boolean movingLeft, boolean movingRight, boolean isJumping) {
        stateTime += com.badlogic.gdx.Gdx.graphics.getDeltaTime();

        if (isJumping) {
            return jumpAnimation.getKeyFrame(stateTime, true);
        } else if (movingLeft) {
            return walkLeftAnimation.getKeyFrame(stateTime, true);
        } else if (movingRight) {
            return walkRightAnimation.getKeyFrame(stateTime, true);
        } else {
            return idleAnimation.getKeyFrame(stateTime, true);
        }
    }
}
