package com.l3cache.snapshop.newsfeed;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.data.NewsfeedData;

public class NewsfeedVolleyAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<NewsfeedData> newsfeedDatas;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public NewsfeedVolleyAdapter(Activity activity, ArrayList<NewsfeedData> newsfeedDatas) {
		this.activity = activity;
		this.newsfeedDatas = newsfeedDatas;
	}

	@Override
	public int getCount() {
		return newsfeedDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return newsfeedDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (inflater == null)
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.newsfeed_volley_list_row, null);

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.feedImage1);
		// NetworkImageView profilePic = (NetworkImageView)
		// convertView.findViewById(R.id.profilePic);
		TextView statusView = (TextView) convertView.findViewById(R.id.txtStatusMsg);
		NewsfeedData item = newsfeedDatas.get(position);

		ToggleButton likeButton = (ToggleButton) convertView.findViewById(R.id.snapButton);
		likeButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					buttonView.setTextColor(Color.parseColor("#2DB400"));
				} else {
					buttonView.setTextColor(Color.parseColor("#a0a3a7"));
				}

			}
		});

		Button priceButton = (Button) convertView.findViewById(R.id.priceTextView);
		NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ko_KR"));
		format.setParseIntegerOnly(true);
		String formattedLowPrice = format.format(Integer.parseInt(item.getPrice()));
		priceButton.setText(formattedLowPrice);

		// profilePic.setImageUrl("http://i.imgur.com/n3DP2to.png",
		// imageLoader);
		statusView.setText(item.getTitle());

		// Feed image
		if (item.getImage() != null) {
			feedImageView.setImageUrl(item.getImage(), imageLoader);
			feedImageView.setVisibility(View.VISIBLE);
			feedImageView.setResponseObserver(new FeedImageView.ResponseObserver() {
				@Override
				public void onError() {
				}

				@Override
				public void onSuccess() {
				}
			});
		} else {
			feedImageView.setVisibility(View.GONE);
		}

		return convertView;
	}

}
