package com.l3cache.snapshop.postviewer;

import io.realm.Realm;
import io.realm.RealmChangeListener;

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
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.app.AppController.TrackerName;
import com.l3cache.snapshop.myposts.MyPost;
import com.l3cache.snapshop.mysnaps.MySnap;
import com.l3cache.snapshop.newsfeed.Newsfeed;
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
	private int pid;
	private Newsfeed currentData;
	private Realm realm;
	private TextView snapsNumberTextView;
	private TextView viewsNumberTextView;
	private RealmChangeListener realmChangeListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tracker t = ((AppController) getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName(PostViewer.class.getSimpleName());
		t.send(new HitBuilders.AppViewBuilder().build());

		realm = Realm.getInstance(this);
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
				.setConverter(new GsonConverter(new Gson())).build();
		SnapShopService service = restAdapter.create(SnapShopService.class);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_post_viewer);

		realm.beginTransaction();
		Bundle extras = getIntent().getExtras();
		switch (extras.getInt("class")) {
		case SnapConstants.CLASS_NEWSFEED: {
			currentData = realm.where(Newsfeed.class).equalTo("pid", extras.getLong("pid")).findFirst();
			break;
		}
		case SnapConstants.CLASS_MYSNAP: {
			MySnap snapData = realm.where(MySnap.class).equalTo("pid", extras.getLong("pid")).findFirst();
			Newsfeed newData = realm.createObject(Newsfeed.class);
			newData.setPid(snapData.getPid());
			newData.setImageUrl(snapData.getImageUrl());
			newData.setWriter(snapData.getWriter());
			newData.setTitle(snapData.getTitle());
			newData.setNumLike(snapData.getNumLike());
			newData.setRead(snapData.getRead());
			newData.setPrice(snapData.getPrice());
			break;
		}
		case SnapConstants.CLASS_MYPOST: {
			MyPost postData = realm.where(MyPost.class).equalTo("pid", extras.getLong("pid")).findFirst();
			Newsfeed newData = realm.createObject(Newsfeed.class);
			newData.setPid(postData.getPid());
			newData.setImageUrl(postData.getImageUrl());
			newData.setWriter(postData.getWriter());
			newData.setTitle(postData.getTitle());
			newData.setNumLike(postData.getNumLike());
			newData.setRead(postData.getRead());
			newData.setPrice(postData.getPrice());
			break;
		}
		}
		realm.commitTransaction();
		currentData = realm.where(Newsfeed.class).equalTo("pid", extras.getLong("pid")).findFirst();

		pid = currentData.getPid();

		feedImageView = (FeedImageView) findViewById(R.id.post_viewer_item_image_view);
		feedImageView.setImageUrl(currentData.getImageUrl(), imageLoader);

		titleTextView = (TextView) findViewById(R.id.post_viewer_item_title_text_view);
		titleTextView.setText(currentData.getTitle());

		userNameTextView = (TextView) findViewById(R.id.post_viewer_item_user_text_view);
		userNameTextView.setText(currentData.getWriter());

		snapsNumberTextView = (TextView) findViewById(R.id.post_viewer_item_snaps_number_text_view);
		snapsNumberTextView.setText("+ " + currentData.getNumLike());

		viewsNumberTextView = (TextView) findViewById(R.id.post_viewer_item_view_number_text_view);
		viewsNumberTextView.setText((currentData.getRead() > 1 ? currentData.getRead() + " Views" : currentData
				.getRead() + " View"));

		/**
		 * Snap/UnSnap의 변경 될 경우 화면도 갱신합니다
		 */
		if (realmChangeListener == null)
			realmChangeListener = new RealmChangeListener() {

				@Override
				public void onChange() {
					if (currentData != null) {
						snapsNumberTextView.setText("+ " + currentData.getNumLike());
						viewsNumberTextView.setText((currentData.getRead() > 1 ? currentData.getRead() + " Views"
								: currentData.getRead() + " View"));
					}
				}
			};
		realm.addChangeListener(realmChangeListener);
		service.readPost(currentData.getPid(), new Callback<DefaultResponse>() {
			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void success(DefaultResponse defResp, Response resp) {
				if (defResp.getStatus() == SnapConstants.SUCCESS) {
					realm.beginTransaction();
					currentData.setRead(currentData.getRead() + 1);
					realm.commitTransaction();
				} else {
					Log.e(TAG, "Server Error - " + defResp.getStatus());
				}
			}

		});

		priceButton = (Button) findViewById(R.id.postviewer_price_button);
		NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ko_KR"));
		format.setParseIntegerOnly(true);
		String formattedPrice = format.format(Integer.parseInt(currentData.getPrice()));
		priceButton.setText(formattedPrice);

		/**
		 * priceButton을 터치하면 해당 아이템의 온라인 쇼핑몰로 이동한다.
		 */
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

		/**
		 * 아이템의 상세 설명을 표시하는 곳
		 */
		descTextView = (TextView) findViewById(R.id.post_viewer_description_text_view);
		descTextView.setText((currentData.getContents().length() > 0 ? currentData.getContents() : "No Description"));

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
						service.unSnapPost(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0), pid,
								new Callback<DefaultResponse>() {

									@Override
									public void failure(RetrofitError error) {
										Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT)
												.show();
									}

									@Override
									public void success(DefaultResponse defResp, Response resp) {
										if (defResp.getStatus() == SnapConstants.SUCCESS) {
											Toast.makeText(getApplicationContext(), "Unsnap - " + pid,
													Toast.LENGTH_SHORT).show();
											snapButton.setChecked(false);
											snapButton.setTextColor(Color.parseColor("#000000"));
											realm.beginTransaction();
											currentData.setUserLike(0);
											currentData.setNumLike(currentData.getNumLike() - 1);
											realm.commitTransaction();
										} else {
											Toast.makeText(getApplicationContext(),
													"UnSnap Failed. Server Error - " + defResp.getStatus(),
													Toast.LENGTH_SHORT).show();

										}
									}

								});

					} else {
						service.snapPost(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0), pid,
								new Callback<DefaultResponse>() {

									@Override
									public void failure(RetrofitError error) {
										Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT)
												.show();
									}

									@Override
									public void success(DefaultResponse defResp, Response resp) {
										if (defResp.getStatus() == SnapConstants.SUCCESS) {
											Toast.makeText(getApplicationContext(), "Snap - " + pid, Toast.LENGTH_SHORT)
													.show();
											snapButton.setChecked(true);
											snapButton.setTextColor(Color.parseColor("#2DB400"));
											realm.beginTransaction();
											currentData.setUserLike(1);
											currentData.setNumLike(currentData.getNumLike() + 1);
											realm.commitTransaction();

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
		/**
		 * 뉴스피드를 갱신할 경우 현재 사용하는 Realm 데이터가 삭제되므로, 더 이상 변경에 대한 listening이 불필요.
		 */
		realm.removeChangeListener(realmChangeListener);
		overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
	}
}
