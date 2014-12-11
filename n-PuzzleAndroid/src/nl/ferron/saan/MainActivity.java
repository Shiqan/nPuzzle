package nl.ferron.saan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity for the main screen with buttons to the other Activities
 * 
 * @author FerronSaan
 */
public class MainActivity extends NavDrawerActivity {

	public static final String PREFS_NAME = "nPuzzleFile";
	private SharedPreferences mPrefs;

	private Boolean mSaved;
	private Button mContinue;
	private Button mNew;
	private Button mHighscores;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);

		mPrefs = getSharedPreferences(PREFS_NAME, 0);
		mSaved = mPrefs.getBoolean("saved", false);
		if (mSaved) {
			mContinue = (Button) findViewById(R.id.btn_continue);
			mContinue.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(getApplicationContext(),
							GameActivity.class));
				}
			});
			mContinue.setVisibility(View.VISIBLE);
		}

		mNew = (Button) findViewById(R.id.btn_new);
		mNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSaved) {
					onNewPuzzleDialog().show();
				} else {
					startActivity(new Intent(getApplicationContext(),
							ChooseImageActivity.class));
				}
			}
		});

		mHighscores = (Button) findViewById(R.id.btn_highscores);
		mHighscores.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						HighscoreActivity.class));
			}
		});
	}

	/**
	 * Show dialog with warning when starting new puzzle if one already exists
	 */
	public Dialog onNewPuzzleDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.dialog_resume, null))
				// action buttons
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						})
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						startActivity(new Intent(getApplicationContext(),
								ChooseImageActivity.class));
						finish();
					}
				});
		return builder.create();
	}

	/**
	 * Open Github page in browser
	 */
	private void openWeb() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://github.com/Shiqan/Naive-App-Studio"));
		startActivity(browserIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.action_web:
			openWeb();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
