/**
 * @file MyPostsView.java
 * @brief 현재 로그인한 유저가 작성한 포스트 목록 출력
 * @author Woonohyo, woonohyo@nhnnext.org
 */

package com.l3cache.snapshop.myposts;

import io.realm.Realm;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.app.AppController.TrackerName;
import com.l3cache.snapshop.listener.EndlessScrollListener;
import com.l3cache.snapshop.utils.SnapNetworkUtils;
import com.l3cache.snapshop.volley.NewsfeedRequest;

public class MyPostsView extends Fragment implements OnItemClickListener {
	private static final String TAG = MyPostsView.class.getSimpleName();
	private GridView gridView;
	private MyPostsAdapter postsAdapter;
	private int numOfTotalResult;
	SnapPreference pref;
	private Realm realm;
	private SnapNetworkUtils netUtils = new SnapNetworkUtils();
	private static String URL_FEED = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Tracker t = ((AppController) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName(MyPostsView.class.getSimpleName());
		t.send(new HitBuilders.AppViewBuilder().build());

		realm = Realm.getInstance(getActivity());
		pref = new SnapPreference(getActivity());
		URL_FEED = SnapConstants.SERVER_URL
				+ SnapConstants.MYPOST_REQUEST(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0));

		View view = inflater.inflate(R.layout.activity_my_posts_view, container, false);
		gridView = (GridView) view.findViewById(R.id.my_posts_main_grid_view);
		postsAdapter = new MyPostsAdapter(getActivity(), realm.where(MyPost.class).findAll(), true);
		gridView.setAdapter(postsAdapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (numOfTotalResult < 10 || ((page - 1) * 20) > numOfTotalResult) {
					return;
				}
				fetchDataFromServer(page);
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (netUtils.isOnline(getActivity())) {
			clearRealm();
			fetchDataFromServer(1);
		}
	}

	private void fetchDataFromServer(int start) {
		Uri.Builder builder = new Uri.Builder();
		String targetUrl = builder.encodedPath(URL_FEED).appendQueryParameter("start", start + "").build().toString();
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Entry entry = cache.get(targetUrl);
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
			params.put("start", start + "");

			NewsfeedRequest jsonReq = new NewsfeedRequest(URL_FEED, params, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					Log.i(TAG, "Response: " + response.toString());
					if (response != null) {
						parseJsonFeed(response);
					}
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
				}
			});
			AppController.getInstance().addToRequestQueue(jsonReq);
		}
	}

	private void parseJsonFeed(JSONObject response) {
		try {
			if (response.getInt("total") == 0) {
				Toast.makeText(getActivity(), "No My Posts", Toast.LENGTH_SHORT).show();
				return;
			}
			realm.beginTransaction();
			numOfTotalResult = response.getInt("total");
			JSONArray feedArray = response.getJSONArray("data");
			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);
				MyPost item = realm.createObject(MyPost.class);
				item.setPid(feedObj.getInt("pid"));
				item.setTitle(feedObj.getString("title"));
				item.setShopUrl(feedObj.getString("shopUrl"));
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
		realm.commitTransaction();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "Result - " + resultCode);
	}

	/**
	 * @brief Realm 내에 모든 NewsfeedData를 제거한다.
	 */
	private void clearRealm() {
		realm.beginTransaction();
		realm.where(MyPost.class).findAll().clear();
		realm.commitTransaction();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		intent.setAction("com.l3cache.snapshop.postviewer.PostViewer");
		intent.putExtra("pid", id);
		intent.putExtra("class", SnapConstants.CLASS_MYPOST);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);

	}
}
