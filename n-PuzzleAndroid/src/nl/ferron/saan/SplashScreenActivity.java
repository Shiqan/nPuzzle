package nl.ferron.saan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Show splashscreen for 1 second before going to the MainActivity
 */
public class SplashScreenActivity extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_splash);

		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen for 1 seconds
			 */

			@Override
			public void run() {
				Intent i = new Intent(getApplicationContext() ,MainActivity.class);           	
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}
