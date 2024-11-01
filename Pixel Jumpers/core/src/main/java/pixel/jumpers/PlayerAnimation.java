package pixel.jumpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerAnimation {
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> jumpAnimation;         // Salto hacia la derecha
    private Animation<TextureRegion> jumpLeftAnimation;     // Salto hacia la izquierda
    private Animation<TextureRegion> doubleJumpAnimation;    // Doble salto hacia la derecha
    private Animation<TextureRegion> doubleJumpLeftAnimation; // Doble salto hacia la izquierda
    private float stateTime;

    public PlayerAnimation() {
        // Cargar sprites o texturas para cada animación
        Texture walkSheetLeft = new Texture("run_left.png");
        Texture walkSheetRight = new Texture("run_right.png");
        Texture idleSheet = new Texture("Idle.png");
        Texture jumpSheet = new Texture("Jump.png");
        Texture jumpLeftSheet = new Texture("Jump_Left.png"); // Cargar la textura para el salto hacia la izquierda
        Texture doubleJumpSheet = new Texture("Jump.png");
        Texture doubleJumpLeftSheet = new Texture("Jump_Left.png"); // Cargar la textura para el doble salto hacia la izquierda

        // Dividir en frames y crear animaciones
        walkLeftAnimation = createAnimationFromSheet(walkSheetLeft, 32, 32, 0.1f);
        walkRightAnimation = createAnimationFromSheet(walkSheetRight, 32, 32, 0.1f);
        idleAnimation = createAnimationFromSheet(idleSheet, 32, 32, 0.1f);
        jumpAnimation = createAnimationFromSheet(jumpSheet, 32, 32, 0.1f);
        jumpLeftAnimation = createAnimationFromSheet(jumpLeftSheet, 32, 32, 0.1f);
        doubleJumpAnimation = createAnimationFromSheet(doubleJumpSheet, 32, 32, 0.1f);
        doubleJumpLeftAnimation = createAnimationFromSheet(doubleJumpLeftSheet, 32, 32, 0.1f); // Nueva animación de doble salto hacia la izquierda

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

    public TextureRegion getCurrentFrame(boolean movingLeft, boolean movingRight, boolean isJumping, boolean isDoubleJumping) {
        stateTime += com.badlogic.gdx.Gdx.graphics.getDeltaTime();

        if (isDoubleJumping) {
            // Verificar la dirección del movimiento para seleccionar la animación correcta
            if (movingLeft) {
                return doubleJumpLeftAnimation.getKeyFrame(stateTime, true); // Doble salto hacia la izquierda
            } else {
                return doubleJumpAnimation.getKeyFrame(stateTime, true); // Doble salto hacia la derecha
            }
        } else if (isJumping) {
            // Verificar la dirección del movimiento para seleccionar la animación correcta
            if (movingLeft) {
                return jumpLeftAnimation.getKeyFrame(stateTime, true); // Salto hacia la izquierda
            } else {
                return jumpAnimation.getKeyFrame(stateTime, true); // Salto hacia la derecha
            }
        } else if (movingLeft) {
            return walkLeftAnimation.getKeyFrame(stateTime, true);
        } else if (movingRight) {
            return walkRightAnimation.getKeyFrame(stateTime, true);
        } else {
            return idleAnimation.getKeyFrame(stateTime, true);
        }
    }
}
