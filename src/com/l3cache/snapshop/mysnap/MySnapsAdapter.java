package com.l3cache.snapshop.favorite;

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

public class FavoriteAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<FavoriteData> favoriteItems;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public FavoriteAdapter(Activity activity, ArrayList<FavoriteData> feedItems) {
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
			convertView = inflater.inflate(R.layout.newsfeed_volley_list_row, null);

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		FavoriteData item = favoriteItems.get(position);

		FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.newsfeed_item_image_view);
		TextView writerTextView = (TextView) convertView.findViewById(R.id.newsfeed_item_writer_text_view);
		Button priceButton = (Button) convertView.findViewById(R.id.newsfeed_item_price_button);
		TextView titleTextView = (TextView) convertView.findViewById(R.id.newsfeed_item_title_text_view);
		ToggleButton likeButton = (ToggleButton) convertView.findViewById(R.id.newsfeed_item_like_toggle_button);

		NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ko_KR"));
		format.setParseIntegerOnly(true);
		String formattedLowPrice = format.format(Integer.parseInt(item.getPrice()));
		priceButton.setText(formattedLowPrice);
		writerTextView.setText(item.getWriter());
		titleTextView.setText(item.getTitle());
		
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

		// likeButton의 체크 여부를 item에서 가져와서 세팅하자
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
		likeButton.setChecked((item.getUserLike() == 1 ? true : false));

		return convertView;
	}
}
