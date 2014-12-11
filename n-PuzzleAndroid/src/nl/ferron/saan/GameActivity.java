package nl.ferron.saan;

import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity to show the game
 * 
 * @author FerronSaan
 * @mail ferron.saan@live.nl
 * @studentnumber 10386831
 */

public class GameActivity extends NavDrawerActivity {

	private SharedPreferences prefs;

	private int mShuffleMoves = 250;
	private ArrayList<Bitmap> mSplittedImages;
	private ArrayList<Bitmap> mSolution;
	private ArrayList<Integer> mCurrent;

	private int mBlankTile;
	private int[] mRightSideTiles;
	private int[] mLeftSideTiles;
	private int mNumberOfMoves;
	private int mNumberTiles;
	private int mSqrtNumberTiles;

	private int mImageId;
	private boolean mComplete = false;

	private GridView mGrid;
	private GridImageAdapter mGridAdapter;
	private TextView mNumberOfMovesTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_game);

		prefs = getSharedPreferences(MainActivity.PREFS_NAME, 0);

		mNumberOfMovesTextView = (TextView) findViewById(R.id.numberofmoves);
		mSplittedImages = new ArrayList<Bitmap>(mNumberTiles);
		mSolution = new ArrayList<Bitmap>(mNumberTiles);
		mCurrent = new ArrayList<Integer>(mNumberTiles);

		Intent mIntent = getIntent();
		if (mIntent.getBooleanExtra("new", false)) {
			// new puzzle

			recycleBitmaps();
			
			resetGameState();
			
			// get number of tiles and image id
			mNumberTiles = mIntent.getIntExtra("numberTiles", 0);
			mBlankTile = mNumberTiles - 1;
			mImageId = mIntent.getIntExtra("imageID", 0);

			sideTiles();

			// split image
			mSplittedImages = SplitImage.splitter(this, mImageId, mNumberTiles);
			for (int i = 0; i < mNumberTiles; i++) {
				mSolution.add(mSplittedImages.get(i));
				mCurrent.add(i);
			}
			shufflePuzzle(mShuffleMoves);
		} else {
			// continue
			retrieveGameState();
			mNumberOfMovesTextView.setText("Number of moves: " + mNumberOfMoves);
			sideTiles();
		}
		showSolution();
	}

	/**
	 * Save the progress in SharedPreferences
	 */
	public void saveGameState() {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("saved", true);
		editor.putInt("imageID", mImageId);
		editor.putInt("numberTiles", mNumberTiles);
		editor.putInt("numberOfMoves", mNumberOfMoves);
		editor.putInt("blankTile", mBlankTile);
		editor.putString("order", mCurrent.toString());
		editor.commit();
	}

	/**
	 * Retrieve game state from SharedPreferences
	 */
	public void retrieveGameState() {
		boolean saved = prefs.getBoolean("saved", false);
		if (saved) {
			mImageId = prefs.getInt("imageID", 0);
			mNumberTiles = prefs.getInt("numberTiles", 9);
			mBlankTile = prefs.getInt("blankTile", mNumberTiles - 1);
			mNumberOfMoves = prefs.getInt("numberOfMoves", 0);
			mSolution = SplitImage.splitter(this, mImageId, mNumberTiles);
			mCurrent = readString(prefs.getString("order", "failed"));
			orderTiles(mCurrent);
		}
	}

	/**
	 * Clear SharedPreferences
	 */
	public void resetGameState() {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("saved", false);
		editor.remove("numberTiles");
		editor.remove("imageID");
		editor.remove("order");
		editor.remove("numberOfMoves");
		editor.remove("blankTile");
		editor.commit();
	}

	/**
	 * Determine the left and right side tiles of the puzzle
	 */
	public void sideTiles() {
		mSqrtNumberTiles = (int) Math.sqrt(mNumberTiles);
		mRightSideTiles = new int[mSqrtNumberTiles];
		mLeftSideTiles = new int[mSqrtNumberTiles];
		for (int i = 1; i < mSqrtNumberTiles + 1; i++) {
			mRightSideTiles[i - 1] = i * mSqrtNumberTiles - 1;
			mLeftSideTiles[i - 1] = i * mSqrtNumberTiles;
		}
	}

	/**
	 * Shuffle the puzzle by n moves and then move the blankTile back to the
	 * bottom right
	 */
	private void shufflePuzzle(int n) {
		int steps = 0;
		int tile = 0;
		while (steps < n) {
			int rnd = (int) (Math.random() * 4);
			switch (rnd) {
			case 0:
				// up
				tile = mBlankTile + mSqrtNumberTiles;
				break;
			case 1:
				// right
				tile = mBlankTile + 1;
				break;
			case 2:
				// down
				tile = mBlankTile - mSqrtNumberTiles;
				break;
			case 3:
				// left
				tile = mBlankTile - 1;
				break;
			}
			if (tile >= 0 && tile <= mNumberTiles - 1) {
				if (validMove(tile)) {
					Collections.swap(mSplittedImages, tile, mBlankTile);
					Collections.swap(mCurrent, tile, mBlankTile);
					mBlankTile = tile;
				}
			}
			steps++;
		}
		// move back to bottom right
		while (true) {
			if (!checkTiles(mRightSideTiles, mBlankTile)) {
				// move to right side
				Collections.swap(mSplittedImages, mBlankTile + 1, mBlankTile);
				Collections.swap(mCurrent, mBlankTile + 1, mBlankTile);
				mBlankTile = mBlankTile + 1;
			} else {
				if (mBlankTile == mNumberTiles - 1) {
					break;
				} else {
					// move to bottom
					Collections.swap(mSplittedImages, mBlankTile
							+ mSqrtNumberTiles, mBlankTile);
					Collections.swap(mCurrent, mBlankTile + mSqrtNumberTiles,
							mBlankTile);
					mBlankTile = mBlankTile + mSqrtNumberTiles;
				}
			}
		}

	}
	/**
	 * Set the gridview to a specific list of bitmaps
	 */
	private void setGrid(ArrayList<Bitmap> tiles) {
		mGrid = (GridView) findViewById(R.id.gridview);
		mGridAdapter = new GridImageAdapter(this, tiles);
		mGrid.setAdapter(mGridAdapter);
		mGrid.setNumColumns((int) Math.sqrt(tiles.size()));
		mGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				swapTiles(position);
			}
		});
	}

	/**
	 * Show solution for 3 seconds
	 */
	private void showSolution() {
		setGrid(mSolution);
		mGrid.setEnabled(false);

		new CountDownTimer(3000, 1000) {

			public void onTick(long millisUntilFinished) {
				// show toast with seconds remaining for one second
				final Toast toast = Toast.makeText(getApplicationContext(),
						millisUntilFinished / 1000 + "", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						toast.cancel();
					}
				}, 1000);
			}

			public void onFinish() {
				setGrid(mSplittedImages);
				mGrid.setEnabled(true);
			}

		}.start();
	}

	/**
	 * Swap blankTile with position if move is valid
	 */
	public void swapTiles(int position) {
		if (validMove(position)) {
			Collections.swap(mSplittedImages, position, mBlankTile);
			Collections.swap(mCurrent, position, mBlankTile);
			mBlankTile = position;
			mNumberOfMoves += 1;
			mNumberOfMovesTextView.setText("Number of moves: " + mNumberOfMoves);
			
			mGridAdapter.notifyDataSetChanged();
		}
	

		if (mSolution.equals(mSplittedImages)) {
			onCompleteDialog().show();
		}
	}

	/**
	 * Check for valid move
	 */
	public boolean validMove(int position) {
		// left, right, up, down
		if (position - 1 == mBlankTile && !checkTiles(mLeftSideTiles, position)) {
			return true;
		} else if (position + 1 == mBlankTile
				&& !checkTiles(mRightSideTiles, position)) {
			return true;
		} else if (position + mSqrtNumberTiles == mBlankTile) {
			return true;
		} else if (position - mSqrtNumberTiles == mBlankTile) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if targetValue is in array
	 */
	public static boolean checkTiles(int[] arr, int targetValue) {
		for (int i : arr) {
			if (i == targetValue) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Dialog to enter name after completing the game and go to highscores
	 * afterwards
	 */
	public Dialog onCompleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		final View inflated = inflater.inflate(R.layout.dialog_complete, null);
		builder.setView(inflated)
		// action buttons
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						mComplete = true;
						TextView name = (TextView) inflated
								.findViewById(R.id.username);
						HighscoreControl
								.saveHighscores(getApplicationContext(), name
										.getText().toString(), mNumberOfMoves,
										mNumberTiles);
						resetGameState();
						startActivity(new Intent(getApplicationContext(),
								HighscoreActivity.class));
						finish();
					}
				});
		return builder.create();
	}

	/**
	 * Re-order the Bitmaps to the saved state
	 */
	public void orderTiles(ArrayList<Integer> x) {
		for (int i = 0; i < x.size(); i++) {
			mSplittedImages.add(i, mSolution.get(x.get(i)));
		}
	}

	/**
	 * Read the bitmap order String to ArrayList<Integer>
	 */
	public ArrayList<Integer> readString(String s) {
		String[] l = s.substring(1, s.length() - 1).split(",");
		ArrayList<Integer> intlist = new ArrayList<Integer>(l.length);
		for (String c : l) {
			intlist.add(Integer.valueOf(c.trim()));
		}
		return intlist;
	}

	/**
	 * Recycle the bitmaps
	 */
	public void recycleBitmaps() {
		for (int i = 0; i < mSolution.size(); i++) {
			mSolution.get(i).recycle();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.action_show_solution:
			showSolution();
			return true;
		case R.id.action_reset_puzzle:
			// reset gamestate and reshuffle the puzzle
			resetGameState();
			mSplittedImages.clear();
			mCurrent.clear();
			for (int i = 0; i < mNumberTiles; i++) {
				mSplittedImages.add(mSolution.get(i));
				mCurrent.add(i);
			}
			mBlankTile = mNumberTiles - 1;
			shufflePuzzle(mShuffleMoves);
			setGrid(mSplittedImages);
			mNumberOfMoves = 0;
			mNumberOfMovesTextView.setText("Number of moves: " + mNumberOfMoves);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!mComplete) {
			saveGameState();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (!mComplete) {
			saveGameState();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		recycleBitmaps();
	}

}