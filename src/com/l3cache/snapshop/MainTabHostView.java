package com.l3cache.snapshop;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.WindowManager;

import com.l3cache.snapshop.favorite.FavoriteView;
import com.l3cache.snapshop.info.InfoView;
import com.l3cache.snapshop.myposts.MyPostsView;
import com.l3cache.snapshop.newsfeed.NewsfeedView;

public class MainTabHostView extends FragmentActivity {
	private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_tab_host);

		// Status Bar 없는 상태로 만들기. XML은 TitleBar까지 없애므로 코드로 처리
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Status Bar에서 App Icon 제거하기
		// getActionBar().setDisplayShowHomeEnabled(false);

		// create the Tabhost that will contain the Tab
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		mTabHost.addTab(
				mTabHost.newTabSpec("newsfeed").setIndicator("SNAPS", getResources().getDrawable(R.drawable.news)),
				NewsfeedView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("favorite").setIndicator("LIKE"), FavoriteView.class, null);
		// mTabHost.addTab(mTabHost.newTabSpec("snap").setIndicator("Snap"),
		// null, null);
		mTabHost.addTab(mTabHost.newTabSpec("myPost").setIndicator("MY SNAP"), MyPostsView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("info").setIndicator("Info"), InfoView.class, null);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

}
