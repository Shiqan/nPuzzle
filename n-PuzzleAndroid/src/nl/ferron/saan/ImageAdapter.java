package nl.ferron.saan;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
    private static Context mContext;
    private ArrayList<Integer> mThumbnails;
    private ArrayList<String> mTitles;
    
    int imgWidth = 50;
    int imgHeight = 50;

    public ImageAdapter(Context c, ArrayList<Integer> thumbnails, ArrayList<String> titles) {
        mContext = c;
        mThumbnails = thumbnails;
        mTitles = titles;
    }

    public int getCount() {
        return mTitles.size();
    }

    public Object getItem(int position) {
    	return mTitles.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
    	if (convertView == null) {  
        	LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_load_img, null);
        } 
        
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
         
        //imgIcon.setImageResource(mThumbnails.get(position));
        
		imgIcon.setImageBitmap(decodeSampledBitmapFromResource(mThumbnails.get(position), imgWidth, imgHeight));
        
        txtTitle.setText(mTitles.get(position));
        
        return convertView;
    }
    
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

	public static Bitmap decodeSampledBitmapFromResource(int file,
	        int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(mContext.getResources(), file, options);
	    options.inSampleSize = calculateInSampleSize(options, reqWidth,
	            reqHeight);
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(mContext.getResources(), file, options);
	}
}
