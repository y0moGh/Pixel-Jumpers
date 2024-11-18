package pixel.jumpers;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new Level1(this));
    }
}
