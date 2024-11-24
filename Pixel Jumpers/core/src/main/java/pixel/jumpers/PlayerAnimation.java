package pixel.jumpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> attackLeftAnimation;
    private Animation<TextureRegion> hurtLeftAnimation;
    private Animation<TextureRegion> deathLeftAnimation;
    private Animation<TextureRegion> idleLeftAnimation;

    // Texturas utilizadas para crear las animaciones
    private Texture walkSheetLeft;
    private Texture walkSheetRight;
    private Texture idleSheet;
    private Texture jumpSheet;
    private Texture jumpLeftSheet;
    private Texture doubleJumpSheet;
    private Texture doubleJumpLeftSheet;
    private Texture hurtSheet;
    private Texture deathSheet;
    private Texture attackSheet;
    private Texture attackLeftSheet;
    private Texture hurtLeftSheet;
    private Texture deathLeftSheet;
    private Texture idleLeftSheet;

    private float stateTime;

    public PlayerAnimation() {
        // Cargar texturas
        walkSheetLeft = new Texture("player_animations/run_left.png");
        walkSheetRight = new Texture("player_animations/run_right.png");
        idleSheet = new Texture("player_animations/Idle.png");
        jumpSheet = new Texture("player_animations/Jump.png");
        jumpLeftSheet = new Texture("player_animations/Jump_Left.png");
        doubleJumpSheet = new Texture("player_animations/Jump.png");
        doubleJumpLeftSheet = new Texture("player_animations/Jump_Left.png");
        hurtSheet = new Texture("player_animations/hurt.png");
        deathSheet = new Texture("player_animations/death.png");
        attackSheet = new Texture("player_animations/attack.png");
        attackLeftSheet = new Texture("player_animations/attack_left.png");
        hurtLeftSheet = new Texture("player_animations/hurt_left.png");
        deathLeftSheet = new Texture("player_animations/death_left.png");
        idleLeftSheet = new Texture("player_animations/Idle_left.png");

        // Crear animaciones a partir de las texturas
        walkLeftAnimation = createAnimationFromSheet(walkSheetLeft, 32, 32, 0.1f);
        walkRightAnimation = createAnimationFromSheet(walkSheetRight, 32, 32, 0.1f);
        idleAnimation = createAnimationFromSheet(idleSheet, 32, 32, 0.1f);
        jumpAnimation = createAnimationFromSheet(jumpSheet, 32, 32, 0.1f);
        jumpLeftAnimation = createAnimationFromSheet(jumpLeftSheet, 32, 32, 0.1f);
        doubleJumpAnimation = createAnimationFromSheet(doubleJumpSheet, 32, 32, 0.1f);
        doubleJumpLeftAnimation = createAnimationFromSheet(doubleJumpLeftSheet, 32, 32, 0.1f);
        hurtAnimation = createAnimationFromSheet(hurtSheet, 32, 32, 0.1f);
        deathAnimation = createAnimationFromSheet(deathSheet, 32, 32, 0.1f);
        attackAnimation = createAnimationFromSheet(attackSheet, 32, 32, 0.1f);
        attackLeftAnimation = createAnimationFromSheet(attackLeftSheet, 32, 32, 0.1f);
        hurtLeftAnimation = createAnimationFromSheet(hurtLeftSheet, 32, 32, 0.1f);
        deathLeftAnimation = createAnimationFromSheet(deathLeftSheet, 32, 32, 0.1f);
        idleLeftAnimation = createAnimationFromSheet(idleLeftSheet, 32, 32, 0.1f);

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
    
    public TextureRegion getAttackFrame(float stateTime) {
        return attackAnimation.getKeyFrame(stateTime, false);
    }
    
    public float getAttackAnimationDuration() {
        return attackAnimation.getAnimationDuration();
    }
    
    public TextureRegion getAttackLeftFrame(float stateTime) {
        return attackLeftAnimation.getKeyFrame(stateTime, false);
    }

    public TextureRegion getHurtLeftFrame() {
        stateTime += com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        return hurtLeftAnimation.getKeyFrame(stateTime, true);
    }
    
    public TextureRegion getIdleLeftFrame() {
        stateTime += com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        return idleLeftAnimation.getKeyFrame(stateTime, true);
    }
    
    public TextureRegion getDeathFrame(float stateTime) {
        // Obtener los frames de la animación
        Object[] frames = deathAnimation.getKeyFrames();

        // Convertir el último frame en un TextureRegion
        TextureRegion lastFrame = (TextureRegion) frames[frames.length - 1];

        // Si la animación terminó, devolver el último frame
        if (deathAnimation.isAnimationFinished(stateTime)) {
            return lastFrame;
        }

        // De lo contrario, devolver el frame actual de la animación
        return deathAnimation.getKeyFrame(stateTime, false);
    }
    
    public TextureRegion getJumpFrame(boolean facingRight) {
        stateTime += Gdx.graphics.getDeltaTime();
        return facingRight ? jumpAnimation.getKeyFrame(stateTime, false)
                           : jumpLeftAnimation.getKeyFrame(stateTime, false);
    }    
    
    public TextureRegion getDeathLeftFrame(float stateTime) {
        Object[] frames = deathLeftAnimation.getKeyFrames();
        TextureRegion lastFrame = (TextureRegion) frames[frames.length - 1];
        if (deathLeftAnimation.isAnimationFinished(stateTime)) {
            return lastFrame;
        }
        return deathLeftAnimation.getKeyFrame(stateTime, false);
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
        if (deathSheet != null) deathSheet.dispose();
        if (attackSheet != null) attackSheet.dispose();
        if (attackLeftSheet != null) attackLeftSheet.dispose();
        if (hurtLeftSheet != null) hurtLeftSheet.dispose();
        if (deathLeftSheet != null) deathLeftSheet.dispose();
        if (idleLeftSheet != null) idleLeftSheet.dispose();

    }
}
