/**
 * @file NewsfeedView.java
 * @brief 서버 내 모든 포스트의 목록을 출력
 * @author Woonohyo, woonohyo@nhnnext.org
 * @details 서버 내 모든 포스트의 목록을 출력한다. 
 * 각 포스트에 있는 사진의 터치를 통해 상세 포스트를 볼 수 있는 PostViewer.java 로 이동할 수 있다. 
 * 각 포스트에 있는 Snap 버튼의 터치를 통해 해당 포스트를 Snap/UnSnap 할 수 있다. 
 * 화면 좌측 하단에 있는 FloatingActionButton을 통해 카메라/갤러리/검색을 통한 새로운 포스트 작성이 가능하다.
 */

package com.l3cache.snapshop.activity;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.SnapNetworkUtils;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.adapter.EndlessScrollListener;
import com.l3cache.snapshop.adapter.NewsfeedVolleyRealmAdapter;
import com.l3cache.snapshop.controller.AppController;
import com.l3cache.snapshop.controller.AppController.TrackerName;
import com.l3cache.snapshop.fab.FloatingActionButton;
import com.l3cache.snapshop.fab.FloatingActionsMenu;
import com.l3cache.snapshop.model.Newsfeed;
import com.l3cache.snapshop.photocrop.Crop;
import com.l3cache.snapshop.volley.NewsfeedRequest;

public class NewsfeedView extends Fragment implements OnItemSelectedListener {
	private static final String TAG = NewsfeedView.class.getSimpleName();
	private static final String URL_FEED = SnapConstants.SERVER_URL + SnapConstants.NEWSFEED_REQUEST;
	private final int SORT_RECOMMENDED = 0;
	private final int SORT_RECENT = 1;
	private final int SORT_POPULAR = 2;
	private PullToRefreshGridView mGridView;
	private int sortInto = SORT_RECOMMENDED;
	private NewsfeedVolleyRealmAdapter newsfeedAdapter;
	private Uri fileUri;
	private FloatingActionsMenu menuButton;
	private int totalResults = 0;
	private Spinner sortSpinner;
	private EndlessScrollListener endlessScrollListener;
	private OnItemClickListener gridViewItemClickListener;
	private OnTouchListener newPostButtonTouchListener;
	private SnapNetworkUtils netUtils = new SnapNetworkUtils();
	private Realm realm;
	/**
	 * GCM으로부터 Notification이 온 경우 gridview를 새로고침한다.
	 */
	private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			clearRealm();
			reloadDataFromServer();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Tracker t = ((AppController) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName(NewsfeedView.class.getSimpleName());
		t.send(new HitBuilders.AppViewBuilder().build());

		realm = Realm.getInstance(getActivity());

		View view = inflater.inflate(R.layout.activity_newsfeed, container, false);
		mGridView = (PullToRefreshGridView) view.findViewById(R.id.newsfeed_main_gridView);
		initEndlessScrollListener();
		mGridView.setOnScrollListener(endlessScrollListener);
		initGridViewItemClickListener();
		mGridView.setOnItemClickListener(gridViewItemClickListener);
		// mGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
		// @Override
		// public void onRefresh(PullToRefreshBase<GridView> refreshView) {
		// }
		// });

		sortSpinner = (Spinner) view.findViewById(R.id.newsfeed_spinner_sort);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.snaps_sort_array, android.R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(spinnerAdapter);
		sortSpinner.setOnItemSelectedListener(this);

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

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		newsfeedAdapter = new NewsfeedVolleyRealmAdapter(getActivity(), realm.where(Newsfeed.class).findAll(), true);
		mGridView.setAdapter(newsfeedAdapter);

		if (netUtils.isOnline(getActivity())) {
			clearRealm();
			fetchDataFromServer(1);
		}

		realm.addChangeListener(new RealmChangeListener() {
			@Override
			public void onChange() {
				Log.i(TAG, "Realm Changed");
				newsfeedAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		newsfeedAdapter.notifyDataSetChanged();
		getActivity().registerReceiver(messageReceiver, new IntentFilter("NEW_POST"));
	}

	/**
	 * @details 새 포스트 작성을 위한 FlotingActionButton(카메라, 갤러리, 검색)에 TouchListener를
	 *          설정하고, 각 버튼에 대한 사용자의 입력을 처리한다.
	 */
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

	/**
	 * @brief GridView에 물릴 ClickListener 초기화
	 */
	private void initGridViewItemClickListener() {
		gridViewItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), PostViewer.class);
				intent.putExtra("pid", id);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);

			}
		};
	}

	/**
	 * @brief GridView에 물릴 EndlessScrhollListener 초기화
	 */
	private void initEndlessScrollListener() {
		endlessScrollListener = new EndlessScrollListener() {
			private int mLastFirstVisibleItem;

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (totalResults < 10 || ((page - 1) * 20) > totalResults) {
					return;
				}
				fetchDataFromServer(page);
			}

			/**
			 * @brief FloatingActionButton과 SortSpinner을 스크롤에 따라 화면에서 제거/표시한다.
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				// 스크롤을 내리는 경우
				if (mLastFirstVisibleItem < firstVisibleItem) {
					menuButton.setVisibility(View.INVISIBLE);
					sortSpinner.setVisibility(View.INVISIBLE);
				}
				// 스크롤을 올리는 경우
				if (mLastFirstVisibleItem > firstVisibleItem) {
					sortSpinner.setVisibility(View.VISIBLE);
					menuButton.setVisibility(View.VISIBLE);
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		};
	}

	/**
	 * @brief Realm 내에 모든 NewsfeedData를 제거한다.
	 */
	private void clearRealm() {
		realm.beginTransaction();
		realm.where(Newsfeed.class).findAll().clear();
		realm.commitTransaction();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "HI NEWS! Requesting: " + requestCode + " and Result:" + resultCode);
		/**
		 * @brief 카메라를 통해 새로운 포스트 작성 시작
		 */
		if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
			beginCrop(data.getData());
		} else if (requestCode == Crop.REQUEST_CROP) {
			handleCrop(resultCode, data);
		}

		/**
		 * @brief 갤러리를 통해 새로운 포스트 작성 시작
		 */
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

		/**
		 * @brief 업로드를 무사히 마친 경우, 뉴스피드 데이터를 갱신한다.
		 */
		if (requestCode == SnapConstants.REQUEST_UPLOAD) {
			if (resultCode == Activity.RESULT_OK) {
				reloadDataFromServer();
			}
		}
	}

	/**
	 * Crop 될 이미지의 출력 URI를 생성하고, CropActivity를 시작한다.
	 * 
	 * @param source
	 *            Crop할 이미지의 URI
	 */
	private void beginCrop(Uri source) {
		Uri outputUri = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped.jpeg"));
		new Crop(source).output(outputUri).asSquare().start(getActivity(), this);
	}

	/**
	 * 이미지를 Crop한 뒤, 포스트 업로드를 위한 UploadPostView를 시작한다.
	 * 
	 * @param resultCode
	 *            Crop의 성공 여부에 대한 코드
	 * @param data
	 *            Crop된 이미지를 담고 있는 Intent
	 */
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
		if (newsfeedAdapter != null) {
			newsfeedAdapter.notifyDataSetChanged();
		}
	}

	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * 카메라를 통한 포스트 작성 시, 출력할 파일을 생성한다.
	 * 
	 * @param type
	 *            파일 유형. 당연히 이미지 파일.
	 * @return 외부 저장소에 임시 이름으로 저장한 미디어 File
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

	/**
	 * 서버로부터 포스트 목록을 페이지 단위로 받아온다.
	 * 
	 * @param offset
	 *            포스트 목록을 출력할 페이지
	 */
	private void fetchDataFromServer(int offset) {
		SnapPreference pref = new SnapPreference(getActivity());
		Uri.Builder builder = new Uri.Builder();
		String tempUrl = SnapConstants.SERVER_URL + "/app/posts";
		builder.encodedPath(URL_FEED).appendQueryParameter("sort", sortInto + "")
				.appendQueryParameter("start", offset + "")
				.appendQueryParameter("id", pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 1) + "");
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Entry entry = cache.get(builder.build().toString());
		/**
		 * Volley 해당 URL이 캐시가 되어 있을 경우
		 */
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
		}
		/**
		 * Volley 캐시가 되어 있지 않은 경우 새로운 네트워크 요청을 생성한다.
		 */
		else {
			Map<String, String> params = new HashMap<String, String>();
			params.put("sort", sortInto + "");
			params.put("start", offset + "");
			params.put("id", pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 1) + "");
			Log.i(TAG, params.toString());
			NewsfeedRequest jsonReq = new NewsfeedRequest(URL_FEED, params, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					parseJsonFeed(response);
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
				}
			});
			AppController.getInstance().addToRequestQueue(jsonReq);
		}
	}

	/**
	 * HTTP 통신 결과 응답으로 받은 JSONData를 파싱하여, NewsfeedData로 변환한다.
	 * 
	 * @param response
	 *            HTTP 통신 후 응답
	 */
	private void parseJsonFeed(JSONObject jsonData) {
		realm.beginTransaction();
		if (jsonData != null) {
			try {
				totalResults = jsonData.getInt("total");
				JSONArray feedArray = jsonData.getJSONArray("data");

				for (int i = 0; i < feedArray.length(); i++) {
					JSONObject feedObj = (JSONObject) feedArray.get(i);
					if (realm.where(Newsfeed.class).equalTo("pid", feedObj.getInt("pid")).findFirst() != null) {
						realm.where(Newsfeed.class).equalTo("pid", feedObj.getInt("pid")).findFirst().removeFromRealm();
					}
					Newsfeed item = realm.createObject(Newsfeed.class);
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

			} catch (JSONException e) {
				e.printStackTrace();
				realm.commitTransaction();
			}
		}

		realm.where(Newsfeed.class).findAll().sort("pid", RealmResults.SORT_ORDER_DESCENDING);
		newsfeedAdapter.notifyDataSetChanged();
		realm.commitTransaction();

	}

	/**
	 * 포스트 목록 정렬을 위한 sortSpinner 선택 시 분기 처리
	 */
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
				realm.where(Newsfeed.class).findAll().sort("numLike", RealmResults.SORT_ORDER_DESCENDING);
			}
		}
	}

	/**
	 * 새로운 페이지 카운트를 위해 EndlessScrollListener를 초기화 하고, 서버에서 데이터를 1페이지부터 다시 받아온다.
	 */
	private void reloadDataFromServer() {
		endlessScrollListener.reset();
		fetchDataFromServer(1);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
