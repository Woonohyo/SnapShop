package com.l3cache.snapshop.newsfeed;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.Header;

import com.l3cache.snapshop.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private ImageView mItemImageView;

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

		mItemImageView = (ImageView) row.findViewById(R.id.newsfeed_list_row_item_image_view);
		TextView itemNameTextView = (TextView) row.findViewById(R.id.newsfeed_list_row_item_name_text_view);
		TextView itemPriceTextView = (TextView) row.findViewById(R.id.newsfeed_list_row_itemPrice_text_view);

		itemNameTextView.setText(mListData.get(position).getName());
		itemPriceTextView.setText(mListData.get(position).getPrice());

		try {
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(mListData.get(position).getImgName(), new FileAsyncHttpResponseHandler(mContext) {

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, File response) {
					
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, File response) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					Bitmap bitmap = BitmapFactory.decodeFile(response.toString(), options);
					bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
					mItemImageView.setImageBitmap(bitmap);
				}
			});

		} catch (Exception e) {
			Log.e("NewsFeed", "Image Error: " + e);
		}

		return row;
	}
}
