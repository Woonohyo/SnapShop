package com.l3cache.snapshop.mysnaps;

import io.realm.Realm;
import io.realm.RealmResults;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.hardware.camera2.TotalCaptureResult;
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
import com.google.android.gms.internal.mr;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.app.AppController.TrackerName;
import com.l3cache.snapshop.listener.EndlessScrollListener;
import com.l3cache.snapshop.newsfeed.Newsfeed;
import com.l3cache.snapshop.postviewer.PostViewer;
import com.l3cache.snapshop.utils.SnapNetworkUtils;
import com.l3cache.snapshop.volley.NewsfeedRequest;

public class MySnapsView extends Fragment implements OnItemClickListener {
	private static final String TAG = MySnapsView.class.getSimpleName();
	private GridView gridView;
	private MySnapsAdapter snapsAdapter;
	private ArrayList<Newsfeed> feedItems;
	private int resultPageStart = 1;
	protected int numOfTotalResult;
	private static String URL_FEED;
	private SnapPreference pref;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		registerGoogleAnalytics();

		pref = new SnapPreference(getActivity());
		URL_FEED = SnapConstants.SERVER_URL
				+ SnapConstants.MYSNAP_REQUEST(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0));

		View view = inflater.inflate(R.layout.activity_my_snaps_view, container, false);
		gridView = (GridView) view.findViewById(R.id.my_snaps_main_grid_view);
		feedItems = new ArrayList<Newsfeed>();
		snapsAdapter = new MySnapsAdapter(getActivity(), feedItems);
		gridView.setAdapter(snapsAdapter);
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

	private void registerGoogleAnalytics() {
		Tracker t = ((AppController) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName(MySnapsView.class.getSimpleName());
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	/**
	 * 디바이스가 네트워크에 연결되어 있을 경우, 서버로부터 새로운 데이터를 받아옵니다. 그렇지 않을 경우 NewsfeedView에서 이미
	 * 받아놓은 Realm에 저장된 로컬 데이터 중 (userLike == 1)인 데이터만 가져와서 출력합니다.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SnapNetworkUtils netUtils = new SnapNetworkUtils();
		if (netUtils.isOnline(getActivity()))
			fetchDataFromServer(resultPageStart);
		else {
			Realm realm = Realm.getInstance(getActivity());
			RealmResults<Newsfeed> results = realm.where(Newsfeed.class).equalTo("userLike", 1).findAll("pid", false);
			feedItems.addAll(results);
			numOfTotalResult = feedItems.size();
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
			Log.i(TAG, URL_FEED);
			NewsfeedRequest jsonReq = new NewsfeedRequest(URL_FEED, params, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					try {
						int status = response.getInt("result");
						if (response != null && status == SnapConstants.SUCCESS)
							parseJsonFeed(response);
					} catch (JSONException e) {
						e.printStackTrace();
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
		Realm realm = Realm.getInstance(getActivity());
		realm.beginTransaction();
		try {
			if (response.getInt("total") == 0) {
				Toast.makeText(getActivity(), "No My Snaps", Toast.LENGTH_SHORT).show();
				return;
			}
			numOfTotalResult = response.getInt("total");
			Log.i(TAG, "Total - " + numOfTotalResult);
			JSONArray feedArray = response.getJSONArray("data");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);
				if (feedObj.getInt("like") == 0) {
					continue;
				}

//				Newsfeed item = new Newsfeed();
				Newsfeed item = realm.createObject(Newsfeed.class);
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
				feedItems.add(item);
			}

			// notify data changes to list adapater
			snapsAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
			realm.commitTransaction();
		}
		realm.commitTransaction();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		intent.setAction("com.l3cache.snapshop.postviewer.PostViewer");
		intent.putExtra("pid", id);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);

	}
}
