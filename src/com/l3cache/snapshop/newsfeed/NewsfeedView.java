package com.l3cache.snapshop.newsfeed;

import io.realm.Realm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
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
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.adapter.NewsfeedViewAdapter;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.fab.FloatingActionButton;
import com.l3cache.snapshop.fab.FloatingActionsMenu;
import com.l3cache.snapshop.search.EndlessScrollListener;
import com.l3cache.snapshop.search.SearchResultsView;
import com.l3cache.snapshop.upload.UploadSnapView;

@SuppressLint("ClickableViewAccessibility")
public class NewsfeedView extends Fragment implements OnItemClickListener {
	private static final String TAG = NewsfeedView.class.getSimpleName();
	private static final String URL_FEED = SnapConstants.SERVER_URL() + SnapConstants.NEWSFEED_REQUEST();
	private ArrayList<NewsfeedData> newsfeedDatas;
	private int resultPageStart = 1;
	private GridView mListView;
	private int resultSorting = 0;
	private NewsfeedViewAdapter mNewsfeedViewAdapter;
	private NewsfeedVolleyAdapter newsfeedVolleyAdapter;
	private Uri fileUri;
	private FloatingActionsMenu menuButton;
	protected int numOfTotalResult = 41;
	private Spinner mSortSpinner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_newsfeed, container, false);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (mNewsfeedViewAdapter != null) {
			mNewsfeedViewAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		 removeAllNewsfeedDatas();

		// 서버로부터 데이터를 동기화하는 서비스 시작
		Intent syncServiceIntent = new Intent();
		syncServiceIntent.setAction(".service.SyncDataService");
		getActivity().startService(syncServiceIntent);
		
		// 화면 상단 스피너 설정
		mSortSpinner = (Spinner) getActivity().findViewById(R.id.newsfeed_spinner_sort);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.snaps_sort_array, android.R.layout.simple_spinner_item);
		mSortSpinner.setAdapter(spinnerAdapter);

		OnTouchListener snapButtonTouchListener = new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					int id = v.getId();

					switch (id) {
					case SnapConstants.CAMERA_BUTTON: {
						Log.i("Snap", id + ": Camera");
						// create Intent to take a picture and return control to
						// the
						// calling application
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

						fileUri = getOutputMediaFileUri(SnapConstants.MEDIA_TYPE_IMAGE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

						// start the image capture Intent
						getActivity().startActivityForResult(intent, SnapConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
						break;
					}
					case SnapConstants.GALLERY_BUTTON: {
						Intent intent = new Intent(Intent.ACTION_PICK);
						intent.setType(Images.Media.CONTENT_TYPE);
						intent.setData(Images.Media.EXTERNAL_CONTENT_URI);
						getActivity().startActivityForResult(intent, SnapConstants.RESULT_LOAD_IMAGE);
						break;
					}
					case SnapConstants.INTERNET_BUTTON: {
						Intent intent = new Intent(getActivity(), SearchResultsView.class);
						getActivity().startActivity(intent);
						break;
					}

					}

				}
				return true;
			}
		};

		menuButton = (FloatingActionsMenu) getView().findViewById(R.id.multiple_actions);
		FloatingActionButton cameraButton = (FloatingActionButton) getView().findViewById(R.id.newsfeed_camera_button);
		FloatingActionButton galleryButton = (FloatingActionButton) getView()
				.findViewById(R.id.newsfeed_gallery_button);
		FloatingActionButton internetButton = (FloatingActionButton) getView().findViewById(
				R.id.newsfeed_internet_button);

		cameraButton.setId(SnapConstants.CAMERA_BUTTON);
		galleryButton.setId(SnapConstants.GALLERY_BUTTON);
		internetButton.setId(SnapConstants.INTERNET_BUTTON);

		cameraButton.setOnTouchListener(snapButtonTouchListener);
		galleryButton.setOnTouchListener(snapButtonTouchListener);
		internetButton.setOnTouchListener(snapButtonTouchListener);

		mListView = (GridView) getView().findViewById(R.id.newsfeed_main_gridView);
		mListView.setOnScrollListener(new EndlessScrollListener() {
			private int mLastFirstVisibleItem;

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (numOfTotalResult < 10 || (page * 20) > numOfTotalResult) {
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

		});

		newsfeedDatas = new ArrayList<NewsfeedData>();
		newsfeedVolleyAdapter = new NewsfeedVolleyAdapter(getActivity(), newsfeedDatas);
		mListView.setAdapter(newsfeedVolleyAdapter);
		fetchDataFromServer(resultPageStart);
	}

	private void removeAllNewsfeedDatas() {
		Realm realm = Realm.getInstance(getActivity());
		realm.beginTransaction();
		realm.clear(NewsfeedData.class);
		realm.commitTransaction();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("Snap", "HI NEWS! Requesting: " + requestCode + " and Result:" + resultCode);
		if (requestCode == SnapConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(getActivity().getApplicationContext(), "OK!", Toast.LENGTH_LONG).show();

			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getActivity().getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();

			} else
				Toast.makeText(getActivity().getApplicationContext(), "Capture Failed", Toast.LENGTH_LONG).show();

		} else if (requestCode == SnapConstants.RESULT_LOAD_IMAGE) {
			if (resultCode == Activity.RESULT_OK) {
				Intent uploadIntent = new Intent(getActivity().getApplicationContext(), UploadSnapView.class);
				uploadIntent.putExtra("data", data);
				uploadIntent.putExtra("handler", SnapConstants.GALLERY_BUTTON);
				startActivity(uploadIntent);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getActivity().getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();
			}
		}
	}

	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i("Newsfeed", position + "번 포스트 선택");
	}

	private void fetchDataFromServer(int start) {
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
			Map<String, String> params = new HashMap<String, String>();
			params.put("sort", resultSorting + "");
			params.put("start", start + "");
			params.put("id", "1");
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
					Log.i(TAG, "Error: " + error.getMessage());
					Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
					parseJsonFeed(null);

				}
			});
			AppController.getInstance().addToRequestQueue(jsonReq);
		}
	}

	private void parseJsonFeed(JSONObject response) {
		Realm realm = Realm.getInstance(getActivity());
		realm.beginTransaction();
		if (response != null) {
			try {
				JSONObject jsonData = response.getJSONObject("response");
				JSONArray feedArray = jsonData.getJSONArray("data");

				for (int i = 0; i < feedArray.length(); i++) {
					JSONObject feedObj = (JSONObject) feedArray.get(i);
					if (realm.where(NewsfeedData.class).equalTo("pid", feedObj.getInt("pid")).findFirst() != null) {
						Log.i(TAG, "Post having id " + feedObj.getInt("pid") + " alreay exists - ");
						continue;
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
				newsfeedVolleyAdapter.notifyDataSetChanged();
			} catch (JSONException e) {
				e.printStackTrace();
				realm.commitTransaction();
			}
		}

		realm.commitTransaction();
		newsfeedDatas.addAll(realm.allObjects(NewsfeedData.class).sort("pid", false));

		Log.i(TAG, newsfeedDatas.size() + "");
	}
}
