package nl.ferron.saan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Controller to decode images to bitmaps
 * 
 * @author FerronSaan
 */
public class BitmapControl {
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 2;
		if (height >= reqHeight || width >= reqWidth) {
			inSampleSize *= 2;
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Context c, int file,
			int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(c.getResources(), file, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(c.getResources(), file, options);
	}

}
