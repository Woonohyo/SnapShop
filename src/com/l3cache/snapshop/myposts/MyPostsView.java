package com.l3cache.snapshop.myposts;

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
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Cache.Entry;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.mysnap.MySnapsAdapter;
import com.l3cache.snapshop.mysnap.MySnapsView;
import com.l3cache.snapshop.newsfeed.NewsfeedRequest;
import com.l3cache.snapshop.util.EndlessScrollListener;

public class MyPostsView extends Fragment {
	private static final String TAG = MyPostsView.class.getSimpleName();
	private GridView mGridView;
	private ArrayList<NewsfeedData> mFeedItems;
	private MyPostsAdapter mListAdapter;
	private int mTotalResults;
	private int mTotalResult;

	private static String URL_FEED = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SnapPreference pref = new SnapPreference(getActivity());
		URL_FEED = SnapConstants.SERVER_URL
				+ SnapConstants.MYPOST_REQUEST(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0));

		View view = inflater.inflate(R.layout.activity_my_posts_view, container, false);

		mGridView = (GridView) view.findViewById(R.id.my_posts_main_grid_view);
		mFeedItems = new ArrayList<NewsfeedData>();
		mListAdapter = new MyPostsAdapter(getActivity(), mFeedItems);
		mGridView.setAdapter(mListAdapter);
		mGridView.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				fetchDataFromServer(page);
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fetchDataFromServer(1);
	}

	private void fetchDataFromServer(int start) {
		Log.i(TAG, "Fetching data via " + URL_FEED);
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
			params.put("start", start+"");

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
		Realm realm = Realm.getInstance(getActivity());
		realm.beginTransaction();
		try {
			JSONObject jsonData = response.getJSONObject("response");
			if (jsonData.getInt("total") == 0) {
				Toast.makeText(getActivity(), "No My Posts", Toast.LENGTH_SHORT).show();
				return;
			}
			mTotalResult = jsonData.getInt("total");
			JSONArray feedArray = jsonData.getJSONArray("data");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);
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
				mFeedItems.add(item);
			}

			// notify data changes to list adapater
			mListAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
			realm.commitTransaction();
		}
		realm.commitTransaction();
	}
}
