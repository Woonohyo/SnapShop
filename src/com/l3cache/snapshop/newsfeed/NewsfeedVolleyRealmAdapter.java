package com.l3cache.snapshop.newsfeed;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.volley.FeedImageView;

public class NewsfeedVolleyRealmAdapter extends RealmBaseAdapter<NewsfeedData> implements OnTouchListener, ListAdapter {

	private static final String TAG = NewsfeedVolleyRealmAdapter.class.getSimpleName();
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	private static class MyViewHolder {
		FeedImageView feedImageView;
		TextView writerTextView;
		Button priceButton;
		TextView titleTextView;
		ToggleButton snapButton;
	}

	public NewsfeedVolleyRealmAdapter(Context context, RealmResults<NewsfeedData> realmResults, boolean automaticUpdate) {
		super(context, realmResults, automaticUpdate);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyViewHolder viewHolder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.newsfeed_volley_list_row, parent, false);
			viewHolder = new MyViewHolder();
			viewHolder.feedImageView = (FeedImageView) convertView.findViewById(R.id.newsfeed_item_image_view);
			viewHolder.writerTextView = (TextView) convertView.findViewById(R.id.newsfeed_item_writer_text_view);
			viewHolder.priceButton = (Button) convertView.findViewById(R.id.newsfeed_item_price_button);
			viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.newsfeed_item_title_text_view);
			viewHolder.snapButton = (ToggleButton) convertView.findViewById(R.id.newsfeed_item_snap_toggle_button);
			viewHolder.feedImageView.setOnTouchListener(this);
			convertView.setOnTouchListener(this);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (MyViewHolder) convertView.getTag();
		}

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		NewsfeedData item = realmResults.get(position);
		
		try {
			NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ko_KR"));
			format.setParseIntegerOnly(true);
			String formattedLowPrice = format.format(Integer.parseInt(item.getPrice()));
			viewHolder.priceButton.setText(formattedLowPrice);

		} catch (Exception e) {
			viewHolder.priceButton.setText("0");
		}

		viewHolder.writerTextView.setText(item.getWriter());
		viewHolder.titleTextView.setText(item.getTitle());

		// Feed image
		if (item.getImageUrl() != null) {
			viewHolder.feedImageView.setImageUrl(item.getImageUrl(), imageLoader);
			viewHolder.feedImageView.setVisibility(View.VISIBLE);
			viewHolder.feedImageView.setResponseObserver(new FeedImageView.ResponseObserver() {
				@Override
				public void onError() {
				}

				@Override
				public void onSuccess() {
				}
			});
		} else {
			viewHolder.feedImageView.setVisibility(View.GONE);
		}

		// likeButton의 체크 여부를 item에서 가져와서 세팅하자
		viewHolder.snapButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					buttonView.setTextColor(Color.parseColor("#2DB400"));
				} else {
					buttonView.setTextColor(Color.parseColor("#000000"));
				}

			}
		});
		viewHolder.snapButton.setChecked((item.getUserLike() == 1 ? true : false));
		return convertView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.i(TAG, "onTouchHere " + v.toString());
			return true;
		}
		return false;
	}

	public RealmResults<NewsfeedData> getRealmResults() {
		return realmResults;
	}
	
	@Override
	public long getItemId(int position) {
		return realmResults.get(position).getPid();
	}
}
