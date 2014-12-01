package com.l3cache.snapshop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.l3cache.snapshop.constants.SnapConstants;
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

		mTabHost.addTab(mTabHost.newTabSpec("newsfeed").setIndicator("NEWSFEED"), NewsfeedView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("favorite").setIndicator("LIKE"), FavoriteView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("snap").setIndicator("Snap"), null, null);
		mTabHost.addTab(mTabHost.newTabSpec("myPost").setIndicator("My Post"), MyPostsView.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("info").setIndicator("Info"), InfoView.class, null);

		View snapTabWidget = mTabHost.getTabWidget().getChildAt(2);
		snapTabWidget.setBackgroundColor(getResources().getColor(R.color.naver_green));
		snapTabWidget.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_UP) {
					SnapDialogFragment snapDialog = new SnapDialogFragment();
					snapDialog.show(getSupportFragmentManager(), null);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("Snap", "HI MAIN! Requesting: " + requestCode + " and Result:" + resultCode);
		if (requestCode == SnapConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				
			} else if (resultCode == RESULT_CANCELED) {
				
			} else {
				Toast.makeText(getApplicationContext(), "Capture Failed", Toast.LENGTH_LONG).show();
			}
		} else if (requestCode == SnapConstants.RESULT_LOAD_IMAGE) {
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		/*
		 * MenuInflater inflater = getMenuInflater();
		 * inflater.inflate(R.menu.options_menu, menu);
		 * 
		 * SearchManager searchManager = (SearchManager)
		 * getSystemService(Context.SEARCH_SERVICE); SearchView searchView =
		 * (SearchView) menu.findItem(R.id.search).getActionView();
		 * ComponentName cn = new ComponentName(this, SearchResultsView.class);
		 * searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
		 * 
		 * return super.onCreateOptionsMenu(menu);
		 */
		return true;
	}

}
