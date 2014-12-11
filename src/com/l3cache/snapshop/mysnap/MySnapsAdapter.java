package com.l3cache.snapshop.mysnap;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.volley.FeedImageView;

public class MySnapsAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<NewsfeedData> favoriteItems;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public MySnapsAdapter(Activity activity, ArrayList<NewsfeedData> feedItems) {
		this.activity = activity;
		this.favoriteItems = feedItems;
	}

	@Override
	public int getCount() {
		return favoriteItems.size();
	}

	@Override
	public Object getItem(int location) {
		return favoriteItems.get(location);
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
			convertView = inflater.inflate(R.layout.my_snaps_volley_list_row, null);

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		NewsfeedData item = favoriteItems.get(position);

		FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.my_snap_image_view);
		TextView writerTextView = (TextView) convertView.findViewById(R.id.my_snap_writer_text_view);
		TextView titleTextView = (TextView) convertView.findViewById(R.id.my_snap_title_text_view);
		titleTextView.setText(item.getTitle());
		writerTextView.setText(item.getWriter());

		// Feed image
		if (item.getImageUrl() != null) {
			feedImageView.setImageUrl(item.getImageUrl(), imageLoader);
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
