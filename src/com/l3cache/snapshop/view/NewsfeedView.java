package com.l3cache.snapshop.view;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.SnapNetworkUtils;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.controller.AppController;
import com.l3cache.snapshop.controller.EndlessScrollListener;
import com.l3cache.snapshop.controller.NewsfeedVolleyRealmAdapter;
import com.l3cache.snapshop.controller.AppController.TrackerName;
import com.l3cache.snapshop.fab.FloatingActionButton;
import com.l3cache.snapshop.fab.FloatingActionsMenu;
import com.l3cache.snapshop.model.NewsfeedData;
import com.l3cache.snapshop.photocrop.Crop;
import com.l3cache.snapshop.retrofit.NewsfeedRequest;

public class NewsfeedView extends Fragment implements OnItemSelectedListener {
	private final int SORT_RECOMMENDED = 0;
	private final int SORT_RECENT = 1;
	private final int SORT_POPULAR = 2;
	private static final String TAG = NewsfeedView.class.getSimpleName();
	private static final String URL_FEED = SnapConstants.SERVER_URL + SnapConstants.NEWSFEED_REQUEST;
	private GridView mGridView;
	private int sortInto = SORT_RECOMMENDED;
	// private NewsfeedVolleyAdapter mNewsfeedVolleyAdapter;
	private NewsfeedVolleyRealmAdapter mNewsfeedVolleyAdapter;
	private Uri fileUri;
	private FloatingActionsMenu menuButton;
	protected int totalResults = 0;
	private Spinner mSortSpinner;
	private EndlessScrollListener mEndlessScrollListener;
	private OnItemClickListener mGridViewItemClickListener;
	private OnTouchListener newPostButtonTouchListener;
	private SnapNetworkUtils netUtils = new SnapNetworkUtils();
	private Realm realm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Get tracker.
		Tracker t = ((AppController) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName(NewsfeedView.class.getSimpleName());
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());

		View view = inflater.inflate(R.layout.activity_newsfeed, container, false);
		mGridView = (GridView) view.findViewById(R.id.newsfeed_main_gridView);
		initEndlessScrollListener();
		mGridView.setOnScrollListener(mEndlessScrollListener);
		initGridViewItemClickListener();
		mGridView.setOnItemClickListener(mGridViewItemClickListener);

		mSortSpinner = (Spinner) view.findViewById(R.id.newsfeed_spinner_sort);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.snaps_sort_array, android.R.layout.simple_spinner_dropdown_item);
		mSortSpinner.setAdapter(spinnerAdapter);
		mSortSpinner.setOnItemSelectedListener(this);

		initNewPostButtonTouchListener();

		menuButton = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
		FloatingActionButton cameraButton = (FloatingActionButton) view.findViewById(R.id.newsfeed_camera_button);
		FloatingActionButton galleryButton = (FloatingActionButton) view.findViewById(R.id.newsfeed_gallery_button);
		FloatingActionButton internetButton = (FloatingActionButton) view.findViewById(R.id.newsfeed_internet_button);

		cameraButton.setId(SnapConstants.CAMERA_BUTTON);
		galleryButton.setId(SnapConstants.GALLERY_BUTTON);
		internetButton.setId(SnapConstants.INTERNET_BUTTON);

		cameraButton.setOnTouchListener(newPostButtonTouchListener);
		galleryButton.setOnTouchListener(newPostButtonTouchListener);
		internetButton.setOnTouchListener(newPostButtonTouchListener);

		realm = Realm.getInstance(getActivity());

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		realm.addChangeListener(new RealmChangeListener() {
			@Override
			public void onChange() {
			}
		});
		mNewsfeedVolleyAdapter = new NewsfeedVolleyRealmAdapter(getActivity(), realm.where(NewsfeedData.class)
				.findAll(), true);
		mGridView.setAdapter(mNewsfeedVolleyAdapter);
		if (netUtils.isOnline(getActivity())) {
			clearRealm();
			fetchDataFromServer(1);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mNewsfeedVolleyAdapter.notifyDataSetChanged();
	}

	private void initNewPostButtonTouchListener() {
		newPostButtonTouchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					int id = v.getId();

					switch (id) {
					case SnapConstants.CAMERA_BUTTON: {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

						fileUri = getOutputMediaFileUri(SnapConstants.MEDIA_TYPE_IMAGE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

						// start the image capture Intent
						// getActivity().startActivityForResult(intent,
						// SnapConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
						startActivityForResult(intent, SnapConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
						break;
					}
					case SnapConstants.GALLERY_BUTTON: {
						// Crop.pickImage(getActivity());
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
						startActivityForResult(intent, Crop.REQUEST_PICK);
						break;
					}
					case SnapConstants.INTERNET_BUTTON: {
						Intent intent = new Intent(getActivity(), SearchResultsView.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivityForResult(intent, SnapConstants.REQUEST_UPLOAD);
						break;
					}

					}

				}
				return true;
			}
		};
	}

	private void initGridViewItemClickListener() {
		mGridViewItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), PostViewer.class);
				intent.putExtra("pid", id);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);

			}
		};
	}

	private void initEndlessScrollListener() {
		mEndlessScrollListener = new EndlessScrollListener() {
			private int mLastFirstVisibleItem;

			@Override
			public void onLoadMore(int page, int totalItemsCount) {

				if (totalResults < 10 || ((page - 1) * 20) > totalResults) {
					return;
				}

				fetchDataFromServer(page);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				if (mLastFirstVisibleItem < firstVisibleItem) {
					menuButton.setVisibility(View.INVISIBLE);
					mSortSpinner.setVisibility(View.INVISIBLE);
				}
				if (mLastFirstVisibleItem > firstVisibleItem) {
					mSortSpinner.setVisibility(View.VISIBLE);
					menuButton.setVisibility(View.VISIBLE);
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		};
	}

	private void clearRealm() {
		realm.beginTransaction();
		realm.where(NewsfeedData.class).findAll().clear();
		realm.commitTransaction();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "HI NEWS! Requesting: " + requestCode + " and Result:" + resultCode);
		// 카메라 촬영 사진 이용
		if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
			beginCrop(data.getData());
		} else if (requestCode == Crop.REQUEST_CROP) {
			handleCrop(resultCode, data);
		}

		// 갤러리 이용
		if (requestCode == SnapConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				beginCrop(fileUri);
				Toast.makeText(getActivity(), "OK!", Toast.LENGTH_LONG).show();

			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getActivity(), "Photo Cancelled", Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(getActivity(), "Capture Failed", Toast.LENGTH_LONG).show();
			}
		}

		if (requestCode == SnapConstants.REQUEST_UPLOAD) {
			if (resultCode == Activity.RESULT_OK) {
				reloadDataFromServer();
			}
		}
	}

	private void beginCrop(Uri source) {
		Uri outputUri = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped.jpeg"));
		new Crop(source).output(outputUri).asSquare().start(getActivity(), this);
	}

	private void handleCrop(int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Intent uploadIntent = new Intent(getActivity(), UploadPostView.class);
			uploadIntent.putExtra("data", Crop.getOutput(data));
			uploadIntent.putExtra("handler", SnapConstants.GALLERY_BUTTON);
			startActivityForResult(uploadIntent, SnapConstants.REQUEST_UPLOAD);
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(getActivity(), Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (mNewsfeedVolleyAdapter != null) {
			mNewsfeedVolleyAdapter.notifyDataSetChanged();
		}
	}

	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/*
	 * Create a File for saving an image or video
	 */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == SnapConstants.MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	private void fetchDataFromServer(int offset) {
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Entry entry = cache.get(URL_FEED);
		if (entry != null) {
			try {
				String data = new String(entry.data, "UTF-8");
				try {
					parseJsonFeed(new JSONObject(data));
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			SnapPreference pref = new SnapPreference(getActivity());
			Map<String, String> params = new HashMap<String, String>();
			params.put("sort", sortInto + "");
			params.put("start", offset + "");
			params.put("id", pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 1) + "");
			NewsfeedRequest jsonReq = new NewsfeedRequest(URL_FEED, params, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					if (response != null) {
						parseJsonFeed(response);
					}
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
					realm.where(NewsfeedData.class).findAll().sort("pid", RealmResults.SORT_ORDER_DESCENDING);
					RealmResults<NewsfeedData> result = realm.where(NewsfeedData.class).findAll();
					result.sort("pid", RealmResults.SORT_ORDER_DESCENDING);
					mNewsfeedVolleyAdapter.notifyDataSetChanged();
				}
			});
			AppController.getInstance().addToRequestQueue(jsonReq);
		}
	}

	/* 
	 * 
	 */

	private void parseJsonFeed(JSONObject response) {
		realm.beginTransaction();
		if (response != null) {
			try {
				JSONObject jsonData = response.getJSONObject("response");
				totalResults = jsonData.getInt("total");
				JSONArray feedArray = jsonData.getJSONArray("data");

				for (int i = 0; i < feedArray.length(); i++) {
					JSONObject feedObj = (JSONObject) feedArray.get(i);
					if (realm.where(NewsfeedData.class).equalTo("pid", feedObj.getInt("pid")).findFirst() != null) {
						realm.where(NewsfeedData.class).equalTo("pid", feedObj.getInt("pid")).findFirst()
								.removeFromRealm();
					}

					NewsfeedData item = realm.createObject(NewsfeedData.class);
					item.setPid(feedObj.getInt("pid"));
					item.setTitle(feedObj.getString("title"));
					// url might be null sometimes
					String feedUrl = feedObj.isNull("shopUrl") ? null : feedObj.getString("shopUrl");
					item.setShopUrl(feedUrl);
					item.setContents(feedObj.getString("contents"));
					item.setImageUrl(feedObj.getString("imgUrl"));
					item.setNumLike(feedObj.getInt("numLike"));
					item.setPrice(feedObj.getString("price"));
					item.setWriteDate(feedObj.getString("writeDate"));
					item.setWriter(feedObj.getString("writer"));
					item.setUserLike(feedObj.getInt("like"));
					item.setRead(feedObj.getInt("read"));
				}

				// notify data changes to list adapater
			} catch (JSONException e) {
				e.printStackTrace();
				realm.commitTransaction();
			}
		}

		realm.where(NewsfeedData.class).findAll().sort("pid", RealmResults.SORT_ORDER_DESCENDING);
		mNewsfeedVolleyAdapter.notifyDataSetChanged();
		realm.commitTransaction();

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		String sortBy = parent.getItemAtPosition(position).toString();
		int currentSort = this.sortInto;
		if (sortBy.equals("Recommended")) {
			this.sortInto = SORT_RECOMMENDED;
		} else if (sortBy.equals("Recent")) {
			this.sortInto = SORT_RECENT;

		} else if (sortBy.equals("Popular")) {
			this.sortInto = SORT_POPULAR;
		}

		if (currentSort != this.sortInto) {
			if (netUtils.isOnline(getActivity())) {
				clearRealm();
				reloadDataFromServer();
			} else {
				realm.where(NewsfeedData.class).findAll().sort("numLike", RealmResults.SORT_ORDER_DESCENDING);
			}
		}
	}

	private void reloadDataFromServer() {
		mEndlessScrollListener.reset();
		fetchDataFromServer(1);
		mGridView.post(new Runnable() {

			@Override
			public void run() {
				// mGridView.setSelection(0);
			}
		});
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
