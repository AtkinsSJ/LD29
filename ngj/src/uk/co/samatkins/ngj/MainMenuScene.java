package uk.co.samatkins.ngj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import uk.co.samatkins.Scene;
import uk.co.samatkins.ui.ExtendedButton;

/**
 * Very basic main menu
 */
public class MainMenuScene extends Scene<NGJGame> {

    public MainMenuScene(final NGJGame game) {
        super(game);

        Skin skin = game.getSkin();

        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();
        addActor(table);
        table.pack();

        table.add("Ludum Dare 29").row();

        ExtendedButton playButton = new ExtendedButton("Play", skin);
        playButton.setShortcut(' ');
        playButton.setListener(new ExtendedButton.ButtonListener() {
            @Override
            public void triggered() {
                game.setScene(new PlayScene(game));
            }
        });
        table.add(playButton).row();
    }

    @Override
    protected void onBackButtonPressed() {
        Gdx.app.exit();
    }
}
