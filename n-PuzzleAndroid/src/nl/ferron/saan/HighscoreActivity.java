package nl.ferron.saan;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Activity to show highscores
 * 
 * @author FerronSaan
 * @mail ferron.saan@live.nl
 * @studentnumber 10386831
 */
public class HighscoreActivity extends NavDrawerActivity {

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

		TextView eNameCurrent = (TextView) findViewById(R.id.textView1);
		TextView eMovesCurrent = (TextView) findViewById(R.id.textView2);
		TextView mNameCurrent = (TextView) findViewById(R.id.textView4);
		TextView mMovesCurrent = (TextView) findViewById(R.id.textView5);
		TextView hNameCurrent = (TextView) findViewById(R.id.textView7);
		TextView hMovesCurrent = (TextView) findViewById(R.id.textView8);

		eNameCurrent.setText(scores[0]);
		eMovesCurrent.setText(scores[1]);
		mNameCurrent.setText(scores[2]);
		mMovesCurrent.setText(scores[3]);
		hNameCurrent.setText(scores[4]);
		hMovesCurrent.setText(scores[5]);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.highscore, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
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