package nl.ferron.saan;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Adapter for the GridView to show the game
 * 
 * @author FerronSaan
 */
public class GridImageAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Bitmap> mImageTiles;
	private int mImgWidth;
	private int mImgHeight;

	public GridImageAdapter(Context c, ArrayList<Bitmap> images) {
		mContext = c;
		mImageTiles = images;
		mImgWidth = images.get(0).getWidth();
		mImgHeight = images.get(0).getHeight();
	}

	@Override
	public int getCount() {
		return mImageTiles.size();
	}

	@Override
	public Object getItem(int position) {
		return mImageTiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ImageView image;
		if (convertView == null) {
			image = new ImageView(mContext);
			image.setLayoutParams(new GridView.LayoutParams(mImgWidth,
					mImgHeight));
			image.setPadding(1, 1, 1, 1);

		} else {
			image = (ImageView) convertView;
		}
		image.setImageBitmap(mImageTiles.get(position));
		return image;
	}
}
