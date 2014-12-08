package nl.ferron.saan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Show highscores
 */
public class HighscoreActivity extends NavDrawer {
	
	public static final String PREFS_NAME = "nPuzzleFile";
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_highscores);
		setHighscores();
	}
	
	/**
     * Set the highscores to the appropriate TextView
     */
	private void setHighscores() {		
		String[] scores = HighscoreControl.retrieveHighscores(this);
		
		TextView enameCurrent = (TextView) findViewById(R.id.textView1);
		TextView emovesCurrent = (TextView) findViewById(R.id.textView2);
		TextView mnameCurrent = (TextView) findViewById(R.id.textView4);
		TextView mmovesCurrent = (TextView) findViewById(R.id.textView5);
		TextView hnameCurrent = (TextView) findViewById(R.id.textView7);
		TextView hmovesCurrent = (TextView) findViewById(R.id.textView8);
		
		enameCurrent.setText(scores[0]);
		emovesCurrent.setText(scores[1]);
		mnameCurrent.setText(scores[2]);
		mmovesCurrent.setText(scores[3]);
		hnameCurrent.setText(scores[4]);
		hmovesCurrent.setText(scores[5]);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		 MenuInflater inflater = getMenuInflater();
	       inflater.inflate(R.menu.highscore, menu);
	       return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_reset_highscores:
	            HighscoreControl.resetHighscores(this);
	            setHighscores();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}