package com.l3cache.snapshop.postViewer;

import io.realm.Realm;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.validator.routines.UrlValidator;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.app.AppController.TrackerName;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.retrofit.DefaultResponse;
import com.l3cache.snapshop.retrofit.SnapShopService;
import com.l3cache.snapshop.volley.ExtendedImageLoader;
import com.l3cache.snapshop.volley.FeedImageView;

public class PostViewer extends Activity {
	private String TAG = PostViewer.class.getSimpleName();
	ExtendedImageLoader imageLoader = AppController.getInstance().getImageLoader();
	private FeedImageView feedImageView;
	private TextView titleTextView;
	private TextView userNameTextView;
	private Button priceButton;
	private TextView descTextView;
	private ToggleButton snapButton;
	private NewsfeedData currentData;
	private int mPid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get tracker.
		Tracker t = ((AppController) getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName(PostViewer.class.getSimpleName());
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_post_viewer);

		Realm realm = Realm.getInstance(this);
		Bundle extras = getIntent().getExtras();
		currentData = realm.where(NewsfeedData.class).equalTo("pid", extras.getLong("pid")).findFirst();
		mPid = currentData.getPid();
		Log.i(TAG, currentData.toString());

		feedImageView = (FeedImageView) findViewById(R.id.post_viewer_item_image_view);
		feedImageView.setImageUrl(currentData.getImageUrl(), imageLoader);

		titleTextView = (TextView) findViewById(R.id.post_viewer_item_title_text_view);
		titleTextView.setText(currentData.getTitle());

		userNameTextView = (TextView) findViewById(R.id.post_viewer_item_user_text_view);
		userNameTextView.setText(currentData.getWriter());

		priceButton = (Button) findViewById(R.id.postviewer_price_button);
		NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ko_KR"));
		format.setParseIntegerOnly(true);
		String formattedPrice = format.format(Integer.parseInt(currentData.getPrice()));
		priceButton.setText(formattedPrice);
		priceButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					UrlValidator urlValidator = new UrlValidator();
					if (urlValidator.isValid(currentData.getShopUrl())) {
						Uri uri = Uri.parse(currentData.getShopUrl());
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
						return true;
					} else {
						Toast.makeText(getApplicationContext(), "Invalid Shop URL", Toast.LENGTH_SHORT).show();
						return true;
					}
				}
				return false;
			}
		});

		descTextView = (TextView) findViewById(R.id.post_viewer_description_text_view);
		descTextView.setText((currentData.getContents().length() > 0 ? currentData.getContents() : "No Description"));

		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
				.setConverter(new GsonConverter(new Gson())).build();
		SnapShopService service = restAdapter.create(SnapShopService.class);
		service.readPost(extras.getLong("pid"), new Callback<DefaultResponse>() {

			@Override
			public void success(DefaultResponse defaultResponse, Response response) {
				Log.i(TAG, defaultResponse.getStatus() + "");

			}

			@Override
			public void failure(RetrofitError error) {
				if (error.getResponse() != null) {
					Log.i(TAG, error.getResponse() + "");
				}

			}
		});

		snapButton = (ToggleButton) findViewById(R.id.postviewer_snap_button);
		if (currentData.getUserLike() == 1) {
			snapButton.setChecked(true);
			snapButton.setTextColor(Color.parseColor("#2DB400"));
		}
		snapButton.setOnTouchListener(new OnTouchListener() {
			RestAdapter restAdapter;
			SnapShopService service;
			SnapPreference pref = new SnapPreference(getApplicationContext());

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (restAdapter == null) {
						restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
								.setConverter(new GsonConverter(new Gson())).build();
					}

					if (service == null) {
						service = restAdapter.create(SnapShopService.class);
					}

					if (snapButton.isChecked()) {
						service.unSnapPost(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0), mPid,
								new Callback<DefaultResponse>() {

									@Override
									public void failure(RetrofitError error) {
										Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT)
												.show();
									}

									@Override
									public void success(DefaultResponse defResp, Response resp) {
										if (defResp.getStatus() == SnapConstants.SUCCESS) {
											Toast.makeText(getApplicationContext(), "Unsnap - " + mPid,
													Toast.LENGTH_SHORT).show();
											snapButton.setChecked(false);
											snapButton.setTextColor(Color.parseColor("#000000"));
											currentData.setUserLike(0);
										} else {
											Toast.makeText(getApplicationContext(),
													"UnSnap Failed. Server Error - " + defResp.getStatus(),
													Toast.LENGTH_SHORT).show();

										}
									}

								});

					} else {
						service.snapPost(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0), mPid,
								new Callback<DefaultResponse>() {

									@Override
									public void failure(RetrofitError error) {
										Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT)
												.show();
									}

									@Override
									public void success(DefaultResponse defResp, Response resp) {
										if (defResp.getStatus() == SnapConstants.SUCCESS) {
											Toast.makeText(getApplicationContext(), "Snap - " + mPid,
													Toast.LENGTH_SHORT).show();
											snapButton.setChecked(true);
											snapButton.setTextColor(Color.parseColor("#2DB400"));
											currentData.setUserLike(1);

										} else {
											Toast.makeText(getApplicationContext(),
													"Snap Failed. Server Error - " + defResp.getStatus(),
													Toast.LENGTH_SHORT).show();
										}

									}

								});

					}

					return true;
				}

				return false;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_viewer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		} else if (id == android.R.id.home) {
			onBackPressed();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
	}
}
