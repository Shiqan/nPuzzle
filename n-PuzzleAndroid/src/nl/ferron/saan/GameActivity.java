package nl.ferron.saan;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class GameActivity extends DrawerActivity {
	
	public static final String PREFS_NAME = "nPuzzleFile";
	
	ArrayList<Bitmap> splittedImages;
	ArrayList<Bitmap> solution;
	
	int blankTile;
	int[] rightSideTiles;
	int[] leftSideTiles;
	int possiblenumberOfMoves;
	int numberOfMoves;
	int numberTiles;
	
	int image;
	Bitmap bitmap;
	GridView grid;
	GridImageAdapter adapter;
	TextView numberOfMovesTextView;
	SharedPreferences prefs;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_game);
        
        prefs = getSharedPreferences(PREFS_NAME, 0);
        
        numberOfMovesTextView = (TextView) findViewById(R.id.numberofmoves);
        splittedImages = new ArrayList<Bitmap>(numberTiles);
		solution = new ArrayList<Bitmap>(numberTiles);
			
		Intent mIntent = getIntent();
		Log.d("BOOLEAN", mIntent.getBooleanExtra("new", false)+ "");
        if (mIntent.getBooleanExtra("new", false)) {
        	resetGameState();
        	// get number of tiles and image id 
	        numberTiles = mIntent.getIntExtra("numberTiles", 0);
	        blankTile = numberTiles-1;
	        image = mIntent.getIntExtra("imageID", 0);
	        if (image == 0) {
	        	bitmap = (Bitmap) mIntent.getParcelableExtra("Drawable");
	        }             
	        
	        // split image
	        splitImage(image, numberTiles);
        	
        } else {
        	retrieveGameState();
        	numberOfMovesTextView.setText("Number of moves: " + numberOfMoves);
        }
        
        // determine left and right tiles
        possiblenumberOfMoves = (int) Math.sqrt(numberTiles);
		rightSideTiles = new int[possiblenumberOfMoves-1];
		leftSideTiles = new int[possiblenumberOfMoves-1];
		for (int i = 1; i<possiblenumberOfMoves; i++) {
			rightSideTiles[i-1] = i*possiblenumberOfMoves-1;
		}
		for (int i = 1; i<possiblenumberOfMoves; i++) {
			leftSideTiles[i-1] = i*possiblenumberOfMoves;
		}
		
		showSolution();		
    }
    
    public void saveGameState() {
 	    SharedPreferences.Editor editor = prefs.edit();
 	    editor.putBoolean("saved", true);
 	    editor.putInt("numberTiles", numberTiles);
 	    editor.putInt("numberOfMoves", numberOfMoves);
 	    editor.putInt("blankTile", blankTile);
 	    for (int i = 0; i < numberTiles; i++) {
 	    	editor.putString("solution"+i, encodeTobase64(solution.get(i)));
 	    }
 	    for (int i = 0; i < numberTiles; i++) {
 	    	editor.putString("current"+i, encodeTobase64(splittedImages.get(i)));
 	    }
 	    editor.commit();
    }
    
    public void resetGameState() {
 	    SharedPreferences.Editor editor = prefs.edit();
 	    editor.putBoolean("saved", false);
 	    editor.remove("numberTiles");
 	    editor.remove("numberOfMoves");
 	    editor.remove("blankTile");
 	    for (int i = 0; i < numberTiles; i++) {
 	    	editor.remove("solution"+i);
	    }
 	    for (int i = 0; i < numberTiles; i++) {
 	    	editor.remove("current"+i);
 	    }
 	    
 	    editor.commit();
    }
    
    public void retrieveGameState() {
    	boolean saved = prefs.getBoolean("saved", false);
    	 if (saved) {
    		 numberTiles = prefs.getInt("numberTiles", 9);
    	    	blankTile = prefs.getInt("blankTile", numberTiles-1);
    	    	numberOfMoves = prefs.getInt("numberOfMoves", 0);
    	    	for (int i = 0; i < numberTiles; i++) {
    	 	    	solution.add(decodeBase64(prefs.getString("solution"+i, "failed")));
    	 	    }
    	 	    for (int i = 0; i < numberTiles; i++) {
    	 	    	splittedImages.add(decodeBase64(prefs.getString("current"+i, "failed")));
    	 	    }
         }
    	
    }
    
    public void saveHighscores(String name, int moves ) {
 	    SharedPreferences.Editor editor = prefs.edit();
 	    int current;
		switch (numberTiles) {
	 	    case 9:
	 	    	current = prefs.getInt("Emoves", 0);
		 	    if (moves < current) {
		 	    	editor.putString("Ename", name);
		 	    	editor.putInt("Emoves", moves);
		 	    }
	 	    case 16:
	 	    	current = prefs.getInt("Nmoves", 0);
	 	    	if (moves < current) {
	 	    		editor.putString("Nname", name);
	 	    		editor.putInt("Nmoves", moves);
	 	    	}
	 	    case 25:
	 	    	current = prefs.getInt("Hmoves", 0);
	 	    	if (moves < current) {
	 	    		editor.putString("Hname", name);
	 	    		editor.putInt("Hmoves", moves);
	 	    	}
 	    }
 	    editor.commit();
    }
        
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	    return inSampleSize;
    }
       
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void splitImage(int image, int numberTiles) {	
		
		int rows, cols, devwidth, devheight, tileHeight, tileWidth;

		// get screen size
		Display display = getWindowManager().getDefaultDisplay();
		try {
			Point size = new Point();
			display.getSize(size);
			devwidth = size.x;
			devheight = size.y;
		} catch (NoSuchMethodError e) {
			devwidth = display.getWidth();  // deprecated
			devheight = display.getHeight();  // deprecated
		}
		
		// decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(this.getResources(), image, options);
	    
	    // calculate inSampleSize and decode image
	    options.inSampleSize = calculateInSampleSize(options, devwidth, devheight/2);
	    options.inJustDecodeBounds = false;
	    bitmap = BitmapFactory.decodeResource(this.getResources(), image, options);
	    
	    // scale bitmap to screen sizes
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, devwidth, devheight/2, true);
		bitmap.recycle();
		
		// calculate width and height of each tile
		rows = cols = (int) Math.sqrt(numberTiles);
		tileHeight = (devheight/2)/rows;
		tileWidth = devwidth/cols;
		
		// determine positions of each tile
		int posy = 0;
		for (int x=0; x<rows; x++) {
			int posx = 0;
			for (int y=0; y<cols; y++) {
				
				// create blank tile bottom right
				if (x == rows-1 && y == cols-1) {
					// TODO shuffle
					Collections.shuffle(splittedImages);
					Bitmap myBitmap = Bitmap.createBitmap(scaledBitmap, posx, posy, tileWidth, tileHeight);
										
					Bitmap mutableBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
					mutableBitmap.eraseColor(android.graphics.Color.DKGRAY);
					
					splittedImages.add(mutableBitmap);
					solution.add(mutableBitmap);
					
				} else {
					Bitmap tile = Bitmap.createBitmap(scaledBitmap, posx, posy, tileWidth, tileHeight);
					splittedImages.add(tile);
					solution.add(tile);
				}
				posx += tileWidth;
			}
			posy += tileHeight;
		}
		scaledBitmap.recycle();
	}
    
	private void showSolution() {
		// show solution for 3 seconds 
		setGrid(solution);
		grid.setEnabled(false);
			
		new CountDownTimer(3000, 1000) {

			public void onTick(long millisUntilFinished) {
				// TODO
			}
			
		     public void onFinish() {
		    	 setGrid(splittedImages);
		    	 grid.setEnabled(true);
		     }

		  }.start();
	}
		
	private void setGrid(ArrayList<Bitmap> tiles) {
		// set gridview to a specific list of bitmaps
		grid = (GridView) findViewById(R.id.gridview);
		grid.setAdapter(adapter = new GridImageAdapter(this, tiles));
		grid.setNumColumns((int) Math.sqrt(tiles.size()));
		grid.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            swapTiles(position);	
	        }
	    });
	}

	public void swapTiles(int position) {
		// swap tiles if move is allowed
		// TODO seperate function
		if ((position-1 == blankTile && !checkTiles(leftSideTiles, position)) || 
				(position+1 == blankTile && !checkTiles(rightSideTiles, position)) || 
				position+possiblenumberOfMoves == blankTile || position-possiblenumberOfMoves == blankTile) {
			Collections.swap(splittedImages, position, blankTile);
			blankTile = position;
			numberOfMoves += 1;
			numberOfMovesTextView.setText("Number of moves: " + numberOfMoves);
			
			adapter.notifyDataSetChanged();
		}
		
		if (solution.equals(splittedImages)) {
			Log.d("COMPLETE", numberOfMoves + " ");
			onCompleteDialog(null).show();
		}
	}
	
	public Dialog onCompleteDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    // Get the layout inflater
	    LayoutInflater inflater = this.getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.dialog_complete, null))
	    // Add action buttons
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   resetGameState();
	                   // TODO go to highscoreActivity
	               }
	           });     
	    return builder.create();
	}
	
	public static boolean checkTiles(int[] arr, int targetValue) {
		for(int i: arr){
			if(i == targetValue) {
				return true;
			}
		}
		return false;
	};
	
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static String encodeTobase64(Bitmap image) {
	    Bitmap immagex=image;
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    byte[] b = baos.toByteArray();
	    String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
	    return imageEncoded;
	}
	@TargetApi(Build.VERSION_CODES.FROYO)
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
	        	resetGameState();
	            
	        	Collections.swap(splittedImages, blankTile, (possiblenumberOfMoves * possiblenumberOfMoves)-1);  
	        	Collections.shuffle(splittedImages.subList(0, (possiblenumberOfMoves * possiblenumberOfMoves)-1));
	        	blankTile = (possiblenumberOfMoves * possiblenumberOfMoves)-1;
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

	    saveGameState();
	}
	

}