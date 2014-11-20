package com.l3cache.snapshop;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TabHost.OnTabChangeListener;

import com.l3cache.snapshop.favorite.FavoriteView;
import com.l3cache.snapshop.newsfeed.NewsfeedView;
import com.l3cache.snapshop.search.SearchResultsView;

public class MainTabHostView extends FragmentActivity {
	private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Status Bar 없는 상태로 만들기. XML은 TitleBar까지 없애므로 코드로 처리
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Status Bar에서 App Icon 제거하기
		// getActionBar().setDisplayShowHomeEnabled(false);

		// create the Tabhost that will contain the Tab
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals("snap")) {
					SnapDialogFragment snapDialog = new SnapDialogFragment();	
					snapDialog.show(getSupportFragmentManager(), null);
				}
			}
		});

		mTabHost.addTab(mTabHost.newTabSpec("newsfeed").setIndicator("Newsfeed"), NewsfeedView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("favorite").setIndicator("Favorite"), FavoriteView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("snap").setIndicator("Snap"), NewsfeedView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("myPost").setIndicator("My Post"), NewsfeedView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("info").setIndicator("Info"), NewsfeedView.class, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		ComponentName cn = new ComponentName(this, SearchResultsView.class);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

}
