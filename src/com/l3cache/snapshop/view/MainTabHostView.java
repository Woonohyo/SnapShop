package com.l3cache.snapshop.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;

import com.l3cache.snapshop.R;
import com.l3cache.snapshop.R.drawable;
import com.l3cache.snapshop.R.id;
import com.l3cache.snapshop.R.layout;

public class MainTabHostView extends FragmentActivity {
	private FragmentTabHost mTabHost;
	private String TAG = MainTabHostView.class.getSimpleName();

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
		mTabHost.addTab(mTabHost.newTabSpec("favorite").setIndicator("MY SNAPS"), MySnapsView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("myPost").setIndicator("MY POSTS"), MyPostsView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("info").setIndicator("Info"), InfoView.class, null);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "OnResume");
	}

	@Override
	public void onBackPressed() {
		Log.i(TAG, "On Back Pressed");
		moveTaskToBack(true);
	}
}
