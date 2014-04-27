package uk.co.samatkins.ld29;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import uk.co.samatkins.AudioManager;
import uk.co.samatkins.Game;

public class SwanGame extends Game {

    @Override
    public void create() {
        super.create();
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.input.setCatchBackKey(true);

//        AudioManager.loadSound("splash1", 0.9f, 1.2f);
        AudioManager.loadSound("ribbit");
        AudioManager.loadSound("quack");

        setScene(new MainMenuScene(this));
    }
}
