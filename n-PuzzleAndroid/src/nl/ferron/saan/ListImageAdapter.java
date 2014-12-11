package nl.ferron.saan;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for the listview to show the images to play the game with
 * 
 * @author FerronSaan
 */
public class ListImageAdapter extends BaseAdapter {
	private static Context mContext;
	private ArrayList<Integer> mThumbnails;
	private ArrayList<String> mTitles;
	private int mImgWidth = 50;
	private int mImgHeight = 50;

	public ListImageAdapter(Context c, ArrayList<Integer> thumbnails,
			ArrayList<String> titles) {
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

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.list_item_load_img, null);
		}

		ImageView imgIcon = (ImageView) convertView
				.findViewById(R.id.thumbnail);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

		// decode images to thumbnail size
		imgIcon.setImageBitmap(BitmapControl.decodeSampledBitmapFromResource(
				mContext, mThumbnails.get(position), mImgWidth,
				mImgHeight));
		txtTitle.setText(mTitles.get(position));
		return convertView;
	}
}
