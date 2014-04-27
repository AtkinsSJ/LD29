package uk.co.samatkins.ngj;

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

        AudioManager.loadSound("splash1", 0.9f, 1.2f);

        setScene(new MainMenuScene(this));
    }
}
