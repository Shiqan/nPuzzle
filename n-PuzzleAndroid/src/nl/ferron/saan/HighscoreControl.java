package nl.ferron.saan;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Save, retrieve and reset the highscores
 */
public class HighscoreControl {
	
	public static final String PREFS_NAME = "nPuzzleFile";
	static SharedPreferences prefs;
	
	static String[] scores;
	
	/**
     * Save the highscores if score is better than current highscore
     */
    public static void saveHighscores(Context c, String name, int moves, int numberTiles) {
    	prefs = c.getSharedPreferences(PREFS_NAME, 0);
    	SharedPreferences.Editor editor = prefs.edit();
 	    int current;
		if (numberTiles == 9) {
 	    	current = prefs.getInt("Emoves", 9999);
	 	    if (moves < current) {
	 	    	editor.putString("Ename", name);
	 	    	editor.putInt("Emoves", moves);
	 	    }
		} else if (numberTiles == 16) {	 
 	    	current = prefs.getInt("Mmoves", 9999);
 	    	if (moves < current) {
 	    		editor.putString("Mname", name);
 	    		editor.putInt("Mmoves", moves);
 	    	}
		} else {
 	    	current = prefs.getInt("Hmoves", 9999);
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
    	scores = new String[6];
    	prefs = c.getSharedPreferences(PREFS_NAME, 0);
    	
    	scores[0] = String.valueOf(prefs.getInt("Emoves", 9999));
    	scores[1] = prefs.getString("Ename", "Name");
		scores[2] = String.valueOf(prefs.getInt("Mmoves", 9999));
		scores[3] = prefs.getString("Mname", "Name");
		scores[4] = String.valueOf(prefs.getInt("Hmoves", 9999));
		scores[5] = prefs.getString("Hname", "Name");
		
		return scores;
    }
    
    /**
     * Reset the highscores
     */
	public static void resetHighscores(Context c) {
		prefs = c.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove("Emoves");
		editor.remove("Ename");
		editor.remove("Mmoves");
		editor.remove("Mname");
		editor.remove("Hmoves");
		editor.remove("Hname");
		editor.commit();			
	}
    

}
