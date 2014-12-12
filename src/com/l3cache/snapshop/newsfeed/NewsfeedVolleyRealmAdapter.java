package com.l3cache.snapshop.newsfeed;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.retrofit.DefaultResponse;
import com.l3cache.snapshop.retrofit.SnapShopService;
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
	public NewsfeedData getItem(int position) {
		return realmResults.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyViewHolder viewHolder;
		NewsfeedData item = realmResults.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.newsfeed_volley_list_row, parent, false);
			viewHolder = new MyViewHolder();
			viewHolder.feedImageView = (FeedImageView) convertView.findViewById(R.id.newsfeed_item_image_view);
			viewHolder.writerTextView = (TextView) convertView.findViewById(R.id.newsfeed_item_writer_text_view);
			viewHolder.priceButton = (Button) convertView.findViewById(R.id.newsfeed_item_price_button);
			viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.newsfeed_item_title_text_view);
			viewHolder.snapButton = (ToggleButton) convertView.findViewById(R.id.newsfeed_item_snap_toggle_button);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (MyViewHolder) convertView.getTag();
		}

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

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

		viewHolder.snapButton.setChecked((item.getUserLike() == 1 ? true : false));
		if (viewHolder.snapButton.isChecked()) {
			viewHolder.snapButton.setTextColor(Color.parseColor("#2DB400"));
		} else {
			viewHolder.snapButton.setTextColor(Color.parseColor("#000000"));
		}

		viewHolder.snapButton.setTag(position);
		Log.i(TAG, "Setting button tag into " + getItem(position));
		viewHolder.snapButton.setOnClickListener(new OnClickListener() {
			RestAdapter restAdapter;
			SnapShopService service;
			SnapPreference pref = new SnapPreference(context);
			int pid;
			ToggleButton snapButton;
			NewsfeedData currentItem;

			@Override
			public void onClick(View v) {

				if (restAdapter == null) {
					restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
							.setConverter(new GsonConverter(new Gson())).build();
				}

				if (service == null) {
					service = restAdapter.create(SnapShopService.class);
				}

				pid = (int) getItemId((int) v.getTag());

				snapButton = (ToggleButton) v;
				Realm realm = Realm.getInstance(context);
				currentItem = realm.where(NewsfeedData.class).equalTo("pid", getItemId((int) v.getTag())).findFirst();

				if (snapButton.isChecked()) {
					service.snapPost(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0), pid,
							new Callback<DefaultResponse>() {

								@Override
								public void success(DefaultResponse defResp, Response resp) {
									if (defResp.getStatus() == SnapConstants.SUCCESS) {
										Toast.makeText(context, "Snap - " + pid, Toast.LENGTH_SHORT).show();
										snapButton.setChecked(true);
										snapButton.setTextColor(Color.parseColor("#2DB400"));
										currentItem.setUserLike(1);
									} else {
										Toast.makeText(context, "Snap Failed. Server Error - " + defResp.getStatus(),
												Toast.LENGTH_SHORT).show();
										snapButton.setChecked(false);
										snapButton.setTextColor(Color.parseColor("#000000"));

									}
								}

								@Override
								public void failure(RetrofitError err) {
									Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
									snapButton.setChecked(false);
									snapButton.setTextColor(Color.parseColor("#000000"));
								}
							});

				} else {
					service.unSnapPost(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0), pid,
							new Callback<DefaultResponse>() {

								@Override
								public void success(DefaultResponse defResp, Response resp) {
									if (defResp.getStatus() == SnapConstants.SUCCESS) {
										Toast.makeText(context, "Unsnap - " + pid, Toast.LENGTH_SHORT).show();
										snapButton.setChecked(false);
										currentItem.setUserLike(0);
										snapButton.setTextColor(Color.parseColor("#000000"));
									} else {
										Toast.makeText(context, "Unsnap Failed. Server Error - " + defResp.getStatus(),
												Toast.LENGTH_SHORT).show();
										snapButton.setChecked(true);
										snapButton.setTextColor(Color.parseColor("#2DB400"));

									}

								}

								@Override
								public void failure(RetrofitError err) {
									Toast.makeText(context, "Network Error - " + err.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
									snapButton.setChecked(true);
									snapButton.setTextColor(Color.parseColor("#2DB400"));
								}
							});
				}
			}
		});

		return convertView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.i(TAG, "NOOOOOOO");
			return false;
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
