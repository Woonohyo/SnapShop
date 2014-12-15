package com.l3cache.snapshop.mysnap;

import io.realm.Realm;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.app.AppController.TrackerName;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.data.User;
import com.l3cache.snapshop.newsfeed.NewsfeedRequest;
import com.l3cache.snapshop.newsfeed.NewsfeedView;
import com.l3cache.snapshop.util.EndlessScrollListener;

public class MySnapsView extends Fragment implements OnItemClickListener {
	private static final String TAG = MySnapsView.class.getSimpleName();
	private GridView listView;
	private MySnapsAdapter listAdapter;
	private ArrayList<NewsfeedData> feedItems;
	private int resultPageStart = 1;
	protected int numOfTotalResult;
	private static String URL_FEED;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Tracker t = ((AppController) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName(MySnapsView.class.getSimpleName());
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());

		SnapPreference pref = new SnapPreference(getActivity());
		URL_FEED = SnapConstants.SERVER_URL
				+ SnapConstants.MYSNAP_REQUEST(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0));

		View view = inflater.inflate(R.layout.activity_my_snaps_view, container, false);
		listView = (GridView) view.findViewById(R.id.my_snaps_main_grid_view);
		feedItems = new ArrayList<NewsfeedData>();
		listAdapter = new MySnapsAdapter(getActivity(), feedItems);
		listView.setAdapter(listAdapter);
		listView.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (numOfTotalResult < 10 || (page * 20) > numOfTotalResult) {
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

		fetchDataFromServer(resultPageStart);
		// fetchDataFromRealm(resultPageStart);
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
			params.put("start", start + "");
			Log.i(TAG, URL_FEED);
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
					Log.i(TAG, "Error: " + error.getMessage());
				}
			});
			AppController.getInstance().addToRequestQueue(jsonReq);
		}
	}

	private void parseJsonFeed(JSONObject response) {
		try {
			JSONObject jsonData = response.getJSONObject("response");
			if (jsonData.getInt("total") == 0) {
				Toast.makeText(getActivity(), "No My Snaps", Toast.LENGTH_SHORT).show();
				return;
			}
			JSONArray feedArray = jsonData.getJSONArray("data");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);
				if (feedObj.getInt("like") == 0) {
					continue;
				}

				NewsfeedData item = new NewsfeedData();

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

				feedItems.add(item);
			}

			// notify data changes to list adapater
			listAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

	}
}
