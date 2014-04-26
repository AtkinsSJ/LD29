package uk.co.samatkins.ngj;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import uk.co.samatkins.Game;

public class NGJGame extends Game {

    @Override
    public void create() {
        super.create();
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.input.setCatchBackKey(true);

        setScene(new MainMenuScene(this));
    }
}
