package uk.co.samatkins.ngj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

        table.defaults().center();

        table.add("Ludum Dare 29:", "white").row();
        table.add("SWAN QWOP", "white").row();
        table.add("\"Like a swan - calm above water,", "white").row();
        table.add("but working its legs off", "white").row();
        table.add("beneath the surface!\"", "white").row();

        ExtendedButton playButton = new ExtendedButton("Play", skin);
        playButton.setShortcuts(new char[]{' ', Input.Keys.ENTER});
        playButton.setListener(new ExtendedButton.ButtonListener() {
            @Override
            public void triggered() {
                game.setScene(new PlayScene(game));
            }
        });
        table.add(playButton).row();

        table.add("Guide Derek the one-legged swan", "white").row();
        table.add("across the lake!", "white").row();

        setKeyboardFocus(table);
    }

    @Override
    protected void onBackButtonPressed() {
        Gdx.app.exit();
    }
}
