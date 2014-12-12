package com.l3cache.snapshop.newsfeed;

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

import com.l3cache.snapshop.R;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.volley.ExtendedImageLoader;
import com.l3cache.snapshop.volley.FeedImageView;

public class NewsfeedVolleyAdapter extends BaseAdapter implements OnTouchListener {
	private static final String TAG = NewsfeedVolleyAdapter.class.getSimpleName();
	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<NewsfeedData> newsfeedDatas;
	ExtendedImageLoader imageLoader = AppController.getInstance().getImageLoader();

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
		return newsfeedDatas.get(position).getPid();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (inflater == null)
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.newsfeed_volley_list_row, null);

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		NewsfeedData item = newsfeedDatas.get(position);

		FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.newsfeed_item_image_view);
		TextView writerTextView = (TextView) convertView.findViewById(R.id.newsfeed_item_writer_text_view);
		Button priceButton = (Button) convertView.findViewById(R.id.newsfeed_item_price_button);
		TextView titleTextView = (TextView) convertView.findViewById(R.id.newsfeed_item_title_text_view);
		ToggleButton snapButton = (ToggleButton) convertView.findViewById(R.id.newsfeed_item_snap_toggle_button);

		snapButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Log.i(TAG, "Snap!");
					return true;
				}
				return false;
			}
		});

		try {
			NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ko_KR"));
			format.setParseIntegerOnly(true);
			String formattedLowPrice = format.format(Integer.parseInt(item.getPrice()));
			priceButton.setText(formattedLowPrice);

		} catch (Exception e) {
			priceButton.setText("0");
		}

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
		snapButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					buttonView.setTextColor(Color.parseColor("#2DB400"));
				} else {
					buttonView.setTextColor(Color.parseColor("#000000"));
				}

			}
		});
		snapButton.setChecked((item.getUserLike() == 1 ? true : false));

		snapButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});

		convertView.setOnTouchListener(this);
		return convertView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.i(TAG, "onTouch");
			return true;
		}
		return false;
	}

}
