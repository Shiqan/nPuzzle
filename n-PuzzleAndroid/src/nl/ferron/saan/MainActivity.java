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

public class MainActivity extends DrawerActivity {
	
	public static final String PREFS_NAME = "nPuzzleFile";
	SharedPreferences prefs;
	Boolean saved;
	Button mContinue;
	Button mNew;
	Button mHighscores;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
	
		prefs = getSharedPreferences(PREFS_NAME, 0);
        saved = prefs.getBoolean("saved", false);
        if (saved) {
        	mContinue = (Button) findViewById(R.id.btn_continue);
    		mContinue.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				startActivity(new Intent(getApplicationContext() ,GameActivity.class));
					finish();
    			}
    		});
        	mContinue.setVisibility(View.VISIBLE);
        }
        		
		mNew = (Button) findViewById(R.id.btn_new);
		mNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (saved) {
					onNewPuzzleDialog(null).show();
				} else {
					startActivity(new Intent(getApplicationContext() ,LoadImageActivity.class));
					finish();
				}
			}
		});
		
		mHighscores = (Button) findViewById(R.id.btn_highscores);
		mHighscores.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext() ,HighscoreActivity.class));
				finish();
			}
		});
	}
	
	public Dialog onNewPuzzleDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    // Get the layout inflater
	    LayoutInflater inflater = this.getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.dialog_resume, null))
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			})
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					startActivity(new Intent(getApplicationContext() ,LoadImageActivity.class));
					finish();
				}
			});
	    return builder.create();
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
	
	private void openWeb() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Shiqan/Naive-App-Studio"));
		startActivity(browserIntent);
	}
	
	

}