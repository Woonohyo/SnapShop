package com.l3cache.snapshop.newsfeed;

import java.io.InputStream;
import java.util.ArrayList;
import com.l3cache.snapshop.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsfeedViewAdapter extends ArrayAdapter<NewsfeedData> {
	private Context mContext;
	private int mLayoutResourceId;
	private ArrayList<NewsfeedData> mListData;

	public NewsfeedViewAdapter(Context context, int layoutResourceId, ArrayList<NewsfeedData> listData) {
		super(context, layoutResourceId, listData);
		mContext = context;
		mLayoutResourceId = layoutResourceId;
		mListData = listData;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}

		ImageView itemImageView = (ImageView) row.findViewById(R.id.newsfeed_list_row_item_image_view);
		TextView itemNameTextView = (TextView) row.findViewById(R.id.newsfeed_list_row_item_name_text_view);
		TextView itemPriceTextView = (TextView) row.findViewById(R.id.newsfeed_list_row_itemPrice_text_view);

		itemNameTextView.setText(mListData.get(position).getName());
		itemPriceTextView.setText(mListData.get(position).getPrice());

		try {
			InputStream is = mContext.getAssets().open(mListData.get(position).getImgName());
			Drawable d = Drawable.createFromStream(is, null);
			itemImageView.setImageDrawable(d);

		} catch (Exception e) {
			Log.e("NewsFeed", "Image Error: " + e);
		}

		return row;
	}
}
