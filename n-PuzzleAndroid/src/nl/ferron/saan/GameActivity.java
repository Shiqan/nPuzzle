package nl.ferron.saan;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
	
	ArrayList<Bitmap> splittedImages;
	ArrayList<Bitmap> solution;
	
	int blankTile;
	int[] rightSideTiles;
	int[] leftSideTiles;
	int possiblenumberOfMoves;
	int numberOfMoves;
	
	int image;
	Bitmap bitmap;
	GridView grid;
	GridImageAdapter adapter;
	TextView numberOfMovesTextView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_game);
        
        numberOfMovesTextView = (TextView) findViewById(R.id.numberofmoves);
        
        // get number of tiles and image id
        Intent mIntent = getIntent();
        int numberTiles = mIntent.getIntExtra("numberTiles", 0);
        image = mIntent.getIntExtra("imageID", 0);
        if (image == 0) {
        	bitmap = (Bitmap) mIntent.getParcelableExtra("Drawable");
        }             
        
        // split image
        splitImage(image, numberTiles);
        
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
		
		// store splitted bitmap in a list of length numberTiless
		splittedImages = new ArrayList<Bitmap>(numberTiles);
		solution = new ArrayList<Bitmap>(numberTiles);
			
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
		for(int x=0; x<rows; x++){
			int posx = 0;
			for(int y=0; y<cols; y++){
				
				// create blank tile bottom right
				if (x == rows-1 && y == cols-1) {
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
		blankTile = numberTiles-1;
		showSolution();
	}
    
	private void showSolution() {
		// show solution for 3 seconds 
		setGrid(solution);
		grid.setEnabled(false);
			
		new CountDownTimer(3000, 1000) {

			public void onTick(long millisUntilFinished) {
				// TODO countdown
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
			onCreateDialog(null).show();
		}
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
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
	            // TODO get blank tile
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
	

}