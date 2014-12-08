package com.l3cache.snapshop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Toast;

import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.favorite.FavoriteView;
import com.l3cache.snapshop.info.InfoView;
import com.l3cache.snapshop.myposts.MyPostsView;
import com.l3cache.snapshop.newsfeed.NewsfeedView;
import com.l3cache.snapshop.upload.UploadSnapView;

public class MainTabHostView extends FragmentActivity {
	private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_tab_host);

		// create the Tabhost that will contain the Tab
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		mTabHost.addTab(
				mTabHost.newTabSpec("newsfeed").setIndicator("FEEDS", getResources().getDrawable(R.drawable.news)),
				NewsfeedView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("favorite").setIndicator("MY SNAPS"), FavoriteView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("myPost").setIndicator("MY POSTS"), MyPostsView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("info").setIndicator("Info"), InfoView.class, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("Snap", "HI MAIN! Requesting: " + requestCode + " and Result:" + resultCode);
		if (requestCode == SnapConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(getApplicationContext(), "OK!", Toast.LENGTH_LONG).show();

			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();

			} else
				Toast.makeText(getApplicationContext(), "Capture Failed", Toast.LENGTH_LONG).show();

		} else if (requestCode == SnapConstants.RESULT_LOAD_IMAGE) {
			if (resultCode == Activity.RESULT_OK) {
				Intent uploadIntent = new Intent(getApplicationContext(), UploadSnapView.class);
				uploadIntent.putExtra("data", data);
				uploadIntent.putExtra("handler", SnapConstants.GALLERY_BUTTON);
				startActivity(uploadIntent);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
