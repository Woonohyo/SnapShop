package com.l3cache.snapshop.mysnaps;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.l3cache.snapshop.AppController;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.model.Newsfeed;
import com.l3cache.snapshop.volley.ExtendedImageLoader;
import com.l3cache.snapshop.volley.FeedImageView;

public class MySnapsAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<Newsfeed> favoriteItems;
	ExtendedImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public MySnapsAdapter(Activity activity, ArrayList<Newsfeed> feedItems) {
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

		Newsfeed item = favoriteItems.get(position);

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
