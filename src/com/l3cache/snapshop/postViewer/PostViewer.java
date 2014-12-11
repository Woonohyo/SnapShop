package com.l3cache.snapshop.postViewer;

import io.realm.Realm;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.retrofit.DefaultResponse;
import com.l3cache.snapshop.retrofit.SnapShopService;
import com.l3cache.snapshop.volley.FeedImageView;

public class PostViewer extends Activity {
	private String TAG = PostViewer.class.getSimpleName();
	private FeedImageView feedImageView;
	private TextView titleTextView;
	private TextView userNameTextView;
	private Button priceButton;
	private TextView descTextView;

	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_post_viewer);
		Realm realm = Realm.getInstance(this);
		Bundle extras = getIntent().getExtras();
		NewsfeedData currentData = realm.where(NewsfeedData.class).equalTo("pid", extras.getLong("pid")).findFirst();
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
