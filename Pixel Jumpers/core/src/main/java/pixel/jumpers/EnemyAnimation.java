package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class EnemyAnimation {
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> hurtAnimation; // Nueva animación para estado de daño
    private float stateTime;

    public EnemyAnimation() {
        // Cargar las texturas para las animaciones del enemigo
        Texture walkSheetLeft = new Texture("enemy_run_left.png");
        Texture walkSheetRight = new Texture("enemy_run_right.png");
        Texture idleSheet = new Texture("enemy_idle.png");
        Texture hurtSheet = new Texture("enemy_hurt.png"); // Nueva textura para daño

        // Dividir las hojas de sprites en frames y crear las animaciones
        walkLeftAnimation = createAnimationFromSheet(walkSheetLeft, 32, 32, 0.1f);
        walkRightAnimation = createAnimationFromSheet(walkSheetRight, 32, 32, 0.1f);
        idleAnimation = createAnimationFromSheet(idleSheet, 32, 32, 0.1f);
        hurtAnimation = createAnimationFromSheet(hurtSheet, 32, 32, 0.1f); // Animación para daño

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

    public TextureRegion getCurrentFrame(boolean movingLeft, boolean movingRight, boolean isHurt) {
        stateTime += com.badlogic.gdx.Gdx.graphics.getDeltaTime();

        if (isHurt) {
            return hurtAnimation.getKeyFrame(stateTime, true);
        } else if (movingLeft) {
            return walkLeftAnimation.getKeyFrame(stateTime, true);
        } else if (movingRight) {
            return walkRightAnimation.getKeyFrame(stateTime, true);
        } else {
            return idleAnimation.getKeyFrame(stateTime, true);
        }
    }
}
