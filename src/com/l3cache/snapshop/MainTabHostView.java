package com.l3cache.snapshop;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.info.InfoView;
import com.l3cache.snapshop.myposts.MyPostsView;
import com.l3cache.snapshop.mysnap.MySnapsView;
import com.l3cache.snapshop.newsfeed.NewsfeedView;
import com.l3cache.snapshop.photocrop.Crop;
import com.l3cache.snapshop.upload.UploadPostView;

public class MainTabHostView extends FragmentActivity {
	private FragmentTabHost mTabHost;
	private String TAG = MainTabHostView.class.getSimpleName();
	private NewsfeedView mNewsfeedView;

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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "HI MAIN! Requesting: " + requestCode + " and Result:" + resultCode);
		// 갤러리로부터 사진 로드
		if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
			beginCrop(data.getData());
		} else if (requestCode == Crop.REQUEST_CROP) {
			handleCrop(resultCode, data);

		}
	}

	private void beginCrop(Uri source) {
		Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped.jpeg"));
		new Crop(source).output(outputUri).withAspect(1, 1).start(this);
	}

	private void handleCrop(int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Intent uploadIntent = new Intent(this, UploadPostView.class);
			uploadIntent.putExtra("data", Crop.getOutput(data));
			uploadIntent.putExtra("handler", SnapConstants.GALLERY_BUTTON);
			startActivity(uploadIntent);
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onBackPressed() {
		Log.i(TAG, "On Back Pressed");
		moveTaskToBack(true);
	}
}
