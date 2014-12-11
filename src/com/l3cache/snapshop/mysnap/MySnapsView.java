package com.l3cache.snapshop.mysnap;

import io.realm.Realm;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.data.User;
import com.l3cache.snapshop.newsfeed.NewsfeedRequest;
import com.l3cache.snapshop.search.EndlessScrollListener;

public class MySnapsView extends Fragment implements OnItemClickListener {
	private static final String TAG = MySnapsView.class.getSimpleName();
	private GridView listView;
	private MySnapsAdapter listAdapter;
	private ArrayList<NewsfeedData> feedItems;
	private int resultSorting = 0;
	private int resultPageStart = 1;
	protected int numOfTotalResult;
	private static final String URL_FEED = SnapConstants.SERVER_URL + SnapConstants.MYSNAP_REQUEST(1);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_my_snap_view, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listView = (GridView) getView().findViewById(R.id.my_snaps_main_grid_view);
		feedItems = new ArrayList<NewsfeedData>();
		listAdapter = new MySnapsAdapter(getActivity(), feedItems);
		listView.setAdapter(listAdapter);
		numOfTotalResult = Realm.getInstance(getActivity()).where(NewsfeedData.class).findAll().size();
		listView.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (numOfTotalResult < 10 || (page * 20) > numOfTotalResult) {
					return;
				}

				// fetchDataFromServer(page);
			}
		});

		// fetchDataFromServer(resultPageStart);
		fetchDataFromRealm(resultPageStart);
	}

	private void fetchDataFromRealm(int offset) {
		Realm realm = Realm.getInstance(getActivity());
		User user = realm.where(User.class).findFirst();
		feedItems.addAll(realm.where(NewsfeedData.class).equalTo("writer", user.getEmail()).findAll());
		listAdapter.notifyDataSetChanged();
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
			NewsfeedRequest jsonReq = new NewsfeedRequest(URL_FEED, null, new Response.Listener<JSONObject>() {
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
