package wit.cgd.warbirds.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GamePreferences {

	public static final String			TAG			= GamePreferences.class.getName();

	public static final GamePreferences	instance	= new GamePreferences();
	private Preferences					prefs;

	public float 						musicVolume = 1;
	public float 						soundVolume = 1;
	public boolean 						sound		= true;
	public boolean 						music		= true;



	private GamePreferences() {
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
	}

	public void load() {
	}

	public void save() {
		prefs.flush();
	}
	
	

}
