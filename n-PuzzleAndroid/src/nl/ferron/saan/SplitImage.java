package nl.ferron.saan;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;

/**
 * Split the image into specific number of tiles
 * 
 * @author FerronSaan
 */
public class SplitImage {

	private static Bitmap bitmap;
	private static ArrayList<Bitmap> splittedImages;

	/**
	 * Get the device width and height, scale chosen image and split into n
	 * tiles
	 */
	public static ArrayList<Bitmap> splitter(Activity act, int image,
			int numberTiles) {

		int rows, cols, devwidth, devheight, tileHeight, tileWidth;

		splittedImages = new ArrayList<Bitmap>(numberTiles);

		// get screen size
		Display display = act.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			devwidth = size.x;
			devheight = size.y;
		} else {
			devwidth = display.getWidth(); // deprecated
			devheight = display.getHeight(); // deprecated
		}
		// decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(act.getResources(), image, options);

		// calculate inSampleSize and decode image
		options.inSampleSize = BitmapControl.calculateInSampleSize(options,
				devwidth, devheight / 2);
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeResource(act.getResources(), image, options);

		// scale bitmap to screen sizes
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, devwidth,
				devheight / 2, true);
		bitmap.recycle();
		
		// calculate width and height of each tile
		rows = cols = (int) Math.sqrt(numberTiles);
		tileHeight = (devheight / 2) / rows;
		tileWidth = devwidth / cols;

		// determine positions of each tile
		int posy = 0;
		for (int x = 0; x < rows; x++) {
			int posx = 0;
			for (int y = 0; y < cols; y++) {

				// create blank tile bottom right
				if (x == rows - 1 && y == cols - 1) {
					Bitmap myBitmap = Bitmap.createBitmap(scaledBitmap, posx,
							posy, tileWidth, tileHeight);

					Bitmap mutableBitmap = myBitmap.copy(
							Bitmap.Config.ARGB_8888, true);
					mutableBitmap.eraseColor(android.graphics.Color.DKGRAY);

					splittedImages.add(mutableBitmap);

				} else {
					Bitmap tile = Bitmap.createBitmap(scaledBitmap, posx, posy,
							tileWidth, tileHeight);
					splittedImages.add(tile);
				}
				posx += tileWidth;
			}
			posy += tileHeight;
		}
		scaledBitmap.recycle();
		return splittedImages;
	}

}
