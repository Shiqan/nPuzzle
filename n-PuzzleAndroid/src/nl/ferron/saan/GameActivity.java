package nl.ferron.saan;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
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
 * Activity to control the flow of the game
 */

public class GameActivity extends NavDrawer {
	
	public static final String PREFS_NAME = "nPuzzleFile";
	SharedPreferences prefs;
	
	boolean mComplete = false;
	
	ArrayList<Bitmap> splittedImages;
	ArrayList<Bitmap> solution;
	ArrayList<Integer> current;
	
	int blankTile;
	int[] rightSideTiles;
	int[] leftSideTiles;
	
	int numberOfMoves;
	int numberTiles;
	int sqrtNumberTiles;
	
	int image;
	GridView grid;
	GridImageAdapter adapter;
	TextView numberOfMovesTextView;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_game);
        
        prefs = getSharedPreferences(PREFS_NAME, 0);
        
        numberOfMovesTextView = (TextView) findViewById(R.id.numberofmoves);
        splittedImages = new ArrayList<Bitmap>(numberTiles);
		solution = new ArrayList<Bitmap>(numberTiles);
		current = new ArrayList<Integer>(numberTiles);
					
		Intent mIntent = getIntent();
        if (mIntent.getBooleanExtra("new", false)) {
        	// new puzzle
  
        	resetGameState();
        	// get number of tiles and image id 
	        numberTiles = mIntent.getIntExtra("numberTiles", 0);
	        blankTile = numberTiles-1;
	        image = mIntent.getIntExtra("imageID", 0);
	        
	        sideTiles(); 
	        
	        // split image
			splittedImages = SplitImage.splitter(this, image, numberTiles);
	        for (int i = 0; i<numberTiles; i++) {
	        	solution.add(splittedImages.get(i));
	        	current.add(i);
	        } 
			shufflePuzzle(250);
        } else {
        	// continue
        	retrieveGameState();
        	numberOfMovesTextView.setText("Number of moves: " + numberOfMoves);
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
 	    editor.putInt("imageID", image);
 	    editor.putInt("numberTiles", numberTiles);
 	    editor.putInt("numberOfMoves", numberOfMoves);
 	    editor.putInt("blankTile", blankTile);
 	    editor.putString("order", current.toString());
 	    editor.commit();
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
     * Retrieve game state from SharedPreferences
     */
    public void retrieveGameState() {
    	boolean saved = prefs.getBoolean("saved", false);
    	if (saved) {
    		image = prefs.getInt("imageID", 0);
    		numberTiles = prefs.getInt("numberTiles", 9);
    		blankTile = prefs.getInt("blankTile", numberTiles-1);
    		numberOfMoves = prefs.getInt("numberOfMoves", 0);
	 	    solution = SplitImage.splitter(this, image, numberTiles);
	 	    current = readString(prefs.getString("order", "failed"));
	 	    orderTiles(current);
         }
    	
    }
    
    /**
   	 * Determine the left and right side tiles of the puzzle
   	 */
    public void sideTiles() {
	    sqrtNumberTiles = (int) Math.sqrt(numberTiles);
		rightSideTiles = new int[sqrtNumberTiles];
		leftSideTiles = new int[sqrtNumberTiles];
		for (int i = 1; i<sqrtNumberTiles+1; i++) {
			rightSideTiles[i-1] = i*sqrtNumberTiles-1;
		}
		for (int i = 1; i<sqrtNumberTiles+1; i++) {
			leftSideTiles[i-1] = i*sqrtNumberTiles;
		}
    }
	    
    
    /**
	 * Shuffle the puzzle by n moves and then move the blankTile back to the bottom right
	 */
	private void shufflePuzzle(int n) {
		int steps = 0;
		int tile = 0;
		while (steps < n) {
			int rnd = (int)(Math.random() * 4); 
			switch (rnd) {
				case 0: 
					// up
					tile = blankTile + sqrtNumberTiles;
					break;
				case 1: 
					// right
					tile = blankTile + 1; 
					break;
				case 2: 
					// down
					tile = blankTile - sqrtNumberTiles;	
					break;
				case 3: 
					// left
					tile = blankTile - 1; 
					break;
			}
			if (tile >= 0 && tile <= numberTiles-1) {
				if (validMove(tile)) {
					Collections.swap(splittedImages, tile, blankTile);
					Collections.swap(current, tile, blankTile);
					blankTile = tile;
				}
			}
			steps++;
		}
		while (true) {
			if (!checkTiles(rightSideTiles, blankTile)) {
				Collections.swap(splittedImages, blankTile+1, blankTile);
				Collections.swap(current, blankTile+1, blankTile);
				blankTile = blankTile + 1;
			} else {
				if (blankTile == numberTiles-1) {
					break;
				} else {
					Collections.swap(splittedImages, blankTile+sqrtNumberTiles, blankTile);
					Collections.swap(current, blankTile+sqrtNumberTiles, blankTile);
					blankTile = blankTile+sqrtNumberTiles;
				}
			}
		}
		
	}

	/**
	 * Set the gridview to a specific list of bitmaps
	 */
	private void setGrid(ArrayList<Bitmap> tiles) {
		grid = (GridView) findViewById(R.id.gridview);
		grid.setAdapter(adapter = new GridImageAdapter(this, tiles));
		grid.setNumColumns((int) Math.sqrt(tiles.size()));
		grid.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            swapTiles(position);	
	        }
	    });
	}
      
    /**
     * Show solution for 3 seconds
     */
	private void showSolution() {
		// show solution for 3 seconds 
		setGrid(solution);
		grid.setEnabled(false);
			
		new CountDownTimer(3000, 1000) {

			public void onTick(long millisUntilFinished) {
				final Toast toast = Toast.makeText(getApplicationContext(), millisUntilFinished/1000+"", Toast.LENGTH_SHORT);
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
		    	 setGrid(splittedImages);
		    	 grid.setEnabled(true);
		     }

		  }.start();
	}

	/**
	 * Swap blankTile with position if move is valid
	 */
	public void swapTiles(int position) {
		// swap tiles if move is allowed
		if (validMove(position)) {
			Collections.swap(splittedImages, position, blankTile);
			Collections.swap(current, position, blankTile);
			blankTile = position;
			numberOfMoves += 1;
			numberOfMovesTextView.setText("Number of moves: " + numberOfMoves);
			
			adapter.notifyDataSetChanged();
		}
		
		if (solution.equals(splittedImages)) {
			onCompleteDialog(null).show();
		}
	}
	
	/**
	 * Check for valid move
	 */
	public boolean validMove(int position) {
		// left, right, up, down
		if (position-1 == blankTile && !checkTiles(leftSideTiles, position)) {
			return true;
		} else if (position+1 == blankTile && !checkTiles(rightSideTiles, position)) {
			return true;
		} else if (position+sqrtNumberTiles == blankTile) {
			return true;
		} else if (position-sqrtNumberTiles == blankTile) {
			return true;
		} else {
			return false;
		}	
	}
	
	/**
	 * Check if targetValue is in array
	 */
	public static boolean checkTiles(int[] arr, int targetValue) {
		for(int i: arr){
			if(i == targetValue) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Dialog to enter name after completing the game and go to highscores afterwards
	 */
	public Dialog onCompleteDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    // Get the layout inflater
	    LayoutInflater inflater = this.getLayoutInflater();
	    final View inflated = inflater.inflate(R.layout.dialog_complete, null);
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflated)
	    // Add action buttons
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   mComplete = true;
	            	   TextView name = (TextView) inflated.findViewById(R.id.username);
	            	   HighscoreControl.saveHighscores(getApplicationContext(), name.getText().toString(), numberOfMoves, numberTiles);
	            	   resetGameState();
	            	   startActivity(new Intent(getApplicationContext() ,HighscoreActivity.class));
	            	   finish();
	               }
	           });     
	    return builder.create();
	}
	
	/**
	 * Re-order the Bitmaps to the saved state
	 */
	public void orderTiles(ArrayList<Integer> x) {
		for (int i = 0; i<x.size(); i++) {
			splittedImages.add(i, solution.get(x.get(i)));
		}		
	}
	
	/**
	 * Read String to ArrayList<Integer>
	 */
	public ArrayList<Integer> readString(String s) {
		String[] t = s.substring(1, s.length()-1).split(",");
		ArrayList<Integer> x = new ArrayList<Integer>(t.length);
		for(String c : t){
			x.add(Integer.valueOf(c.trim()));			
		}
		return x;
	}
	
	@SuppressLint("NewApi")
	public static String encodeTobase64(Bitmap image) {
	    Bitmap immagex=image;
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    byte[] b = baos.toByteArray();
	    String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
	    return imageEncoded;
	}
	
	@SuppressLint("NewApi")
	public static Bitmap decodeBase64(String input) {
		if (input == "failed") {
			return null;
		}
	    byte[] decodedByte = Base64.decode(input, 0);
	    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
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
	        	splittedImages.clear();
	        	current.clear();
	        	for (int i = 0; i<numberTiles; i++) {
		        	splittedImages.add(solution.get(i));
		        	current.add(i);
		        } 
	        	shufflePuzzle(250);
	            setGrid(splittedImages);
	            numberOfMoves = 0;
	            numberOfMovesTextView.setText("Number of moves: " + numberOfMoves);
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
	

}