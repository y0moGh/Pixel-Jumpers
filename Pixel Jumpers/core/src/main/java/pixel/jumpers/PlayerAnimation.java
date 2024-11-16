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
    private Animation<TextureRegion> jumpLeftAnimation;
    private Animation<TextureRegion> doubleJumpAnimation;
    private Animation<TextureRegion> doubleJumpLeftAnimation;
    private Animation<TextureRegion> hurtAnimation;

    // Texturas utilizadas para crear las animaciones
    private Texture walkSheetLeft;
    private Texture walkSheetRight;
    private Texture idleSheet;
    private Texture jumpSheet;
    private Texture jumpLeftSheet;
    private Texture doubleJumpSheet;
    private Texture doubleJumpLeftSheet;
    private Texture hurtSheet;

    private float stateTime;

    public PlayerAnimation() {
        // Cargar texturas
        walkSheetLeft = new Texture("run_left.png");
        walkSheetRight = new Texture("run_right.png");
        idleSheet = new Texture("Idle.png");
        jumpSheet = new Texture("Jump.png");
        jumpLeftSheet = new Texture("Jump_Left.png");
        doubleJumpSheet = new Texture("Jump.png");
        doubleJumpLeftSheet = new Texture("Jump_Left.png");
        hurtSheet = new Texture("hurt.png");

        // Crear animaciones a partir de las texturas
        walkLeftAnimation = createAnimationFromSheet(walkSheetLeft, 32, 32, 0.1f);
        walkRightAnimation = createAnimationFromSheet(walkSheetRight, 32, 32, 0.1f);
        idleAnimation = createAnimationFromSheet(idleSheet, 32, 32, 0.1f);
        jumpAnimation = createAnimationFromSheet(jumpSheet, 32, 32, 0.1f);
        jumpLeftAnimation = createAnimationFromSheet(jumpLeftSheet, 32, 32, 0.1f);
        doubleJumpAnimation = createAnimationFromSheet(doubleJumpSheet, 32, 32, 0.1f);
        doubleJumpLeftAnimation = createAnimationFromSheet(doubleJumpLeftSheet, 32, 32, 0.1f);
        hurtAnimation = createAnimationFromSheet(hurtSheet, 32, 32, 0.1f);

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
            if (movingLeft) {
                return doubleJumpLeftAnimation.getKeyFrame(stateTime, true);
            } else {
                return doubleJumpAnimation.getKeyFrame(stateTime, true);
            }
        } else if (isJumping) {
            if (movingLeft) {
                return jumpLeftAnimation.getKeyFrame(stateTime, true);
            } else {
                return jumpAnimation.getKeyFrame(stateTime, true);
            }
        } else if (movingLeft) {
            return walkLeftAnimation.getKeyFrame(stateTime, true);
        } else if (movingRight) {
            return walkRightAnimation.getKeyFrame(stateTime, true);
        } else {
            return idleAnimation.getKeyFrame(stateTime, true);
        }
    }

    public TextureRegion getHurtFrame() {
        stateTime += com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        return hurtAnimation.getKeyFrame(stateTime, true);
    }

    public TextureRegion getIdleFrame() {
        stateTime += com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        return idleAnimation.getKeyFrame(stateTime, true);
    }

    // Liberar todos los recursos de textura
    public void dispose() {
        if (walkSheetLeft != null) walkSheetLeft.dispose();
        if (walkSheetRight != null) walkSheetRight.dispose();
        if (idleSheet != null) idleSheet.dispose();
        if (jumpSheet != null) jumpSheet.dispose();
        if (jumpLeftSheet != null) jumpLeftSheet.dispose();
        if (doubleJumpSheet != null) doubleJumpSheet.dispose();
        if (doubleJumpLeftSheet != null) doubleJumpLeftSheet.dispose();
        if (hurtSheet != null) hurtSheet.dispose();
    }
}
