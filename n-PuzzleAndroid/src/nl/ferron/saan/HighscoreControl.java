package nl.ferron.saan;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Controller to save, retrieve and reset the highscores
 * 
 * @author FerronSaan
 */
public class HighscoreControl {

	private static SharedPreferences mPrefs;
	public static String[] mScores;

	/**
	 * Save the highscores if score is better than current highscore
	 */
	public static void saveHighscores(Context c, String name, int moves,
			int numberTiles) {
		mPrefs = c.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = mPrefs.edit();
		int current;
		if (numberTiles == 9) {
			current = mPrefs.getInt("Emoves", 9999);
			if (moves < current) {
				editor.putString("Ename", name);
				editor.putInt("Emoves", moves);
			}
		} else if (numberTiles == 16) {
			current = mPrefs.getInt("Mmoves", 9999);
			if (moves < current) {
				editor.putString("Mname", name);
				editor.putInt("Mmoves", moves);
			}
		} else {
			current = mPrefs.getInt("Hmoves", 9999);
			if (moves < current) {
				editor.putString("Hname", name);
				editor.putInt("Hmoves", moves);
			}
		}
		editor.commit();
	}

	/**
	 * Retrieve the highscores
	 */
	public static String[] retrieveHighscores(Context c) {
		mScores = new String[6];
		mPrefs = c.getSharedPreferences(MainActivity.PREFS_NAME, 0);

		mScores[0] = mPrefs.getString("Ename", "Name");
		mScores[1] = String.valueOf(mPrefs.getInt("Emoves", 9999));
		mScores[2] = mPrefs.getString("Mname", "Name");
		mScores[3] = String.valueOf(mPrefs.getInt("Mmoves", 9999));
		mScores[4] = mPrefs.getString("Hname", "Name");
		mScores[5] = String.valueOf(mPrefs.getInt("Hmoves", 9999));

		return mScores;
	}

	/**
	 * Reset the highscores
	 */
	public static void resetHighscores(Context c) {
		mPrefs = c.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.remove("Emoves");
		editor.remove("Ename");
		editor.remove("Mmoves");
		editor.remove("Mname");
		editor.remove("Hmoves");
		editor.remove("Hname");
		editor.commit();
	}

}
