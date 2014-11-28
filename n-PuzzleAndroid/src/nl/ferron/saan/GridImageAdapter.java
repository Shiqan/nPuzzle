package nl.ferron.saan;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridImageAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Bitmap> imageTiles;
	private int imageWidth, imageHeight;
	
	public GridImageAdapter(Context c, ArrayList<Bitmap> images) {
		mContext = c;
		imageTiles = images;
		imageWidth = images.get(0).getWidth();
		imageHeight = images.get(0).getHeight();
	}
	
	@Override
	public int getCount() {
		return imageTiles.size();
	}

	@Override
	public Object getItem(int position) {
		return imageTiles.get(position);
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
			image.setLayoutParams(new GridView.LayoutParams(imageWidth, imageHeight));
			image.setPadding(1, 1, 1, 1);
			image.setTag(position);
			
		} else {
			image = (ImageView) convertView;
		}
		image.setImageBitmap(imageTiles.get(position));
		image.setTag(position);
		return image;
	}
}
