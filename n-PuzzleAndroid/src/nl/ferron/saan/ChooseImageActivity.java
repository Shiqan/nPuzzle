package nl.ferron.saan;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Activity to choose an image to play the game with
 * 
 * @author FerronSaan
 */
public class ChooseImageActivity extends NavDrawerActivity {
	// default number of tiles
	private int mNumberTiles = 16;

	private ArrayList<String> mImageNames = new ArrayList<String>();
	private ArrayList<Integer> mImages = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_load_img);
		ListView imageList = (ListView) findViewById(R.id.list);

		addImages();

		ListImageAdapter adapter = new ListImageAdapter(this, mImages, mImageNames);
		imageList.setAdapter(adapter);
		imageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent myIntent = new Intent(getApplicationContext(),
						GameActivity.class);
				myIntent.putExtra("numberTiles", mNumberTiles);
				myIntent.putExtra("imageID", mImages.get(position));
				myIntent.putExtra("new", true);
				startActivity(myIntent);
				finish();
			}
		});

		setModeDialog().show();
	}

	/**
	 * Add image ids and names to Arraylist
	 */
	private void addImages() throws NotFoundException {
		for (int i = 0; i < 10; i++) {
			int drawableId = getResources().getIdentifier("puzzle_" + i,
					"drawable", getPackageName());
			if (drawableId != 0) {
				mImageNames.add("puzzle_" + i);
				mImages.add(drawableId);
			}
		}

	}

	/**
	 * Difficulty dialog
	 */
	private Dialog setModeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.choose_difficulty)).setItems(
				R.array.mode, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							mNumberTiles = 9;
							break;
						case 1:
							mNumberTiles = 16;
							break;
						case 2:
							mNumberTiles = 25;
							break;

						}
					}
				});
		return builder.create();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.loadimg, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.action_set_mode:
			setModeDialog().show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
