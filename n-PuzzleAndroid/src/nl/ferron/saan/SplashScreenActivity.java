package nl.ferron.saan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Activity to show splashscreen for 1 second before going to the main screen
 * (MainActivity)
 * 
 * @author FerronSaan
 */
public class SplashScreenActivity extends Activity {

	// SplashScreen timer
	private int mSplashTime = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_splash);

		new Handler().postDelayed(new Runnable() {

			//

			@Override
			public void run() {
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(i);
				finish();
			}
		}, mSplashTime);
	}

}
