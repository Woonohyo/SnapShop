package com.l3cache.snapshop.newsfeed;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.adapter.NewsfeedViewAdapter;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.data.NewsfeedData;
import com.l3cache.snapshop.favorite.FavoriteData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NewsfeedView extends Fragment implements OnItemClickListener {
	private static final String TAG = NewsfeedView.class.getSimpleName();
	private static final String URL_FEED = "http://10.73.45.133:8080/app/posts/";
	private int POST_START_PAGE = 1;
	private int POST_SORT = 0;
	private ArrayList<NewsfeedData> newsfeedDatas;
	private AsyncHttpClient mClient = new AsyncHttpClient();
	private ListView mListView;
	private NewsfeedViewAdapter mNewsfeedViewAdapter;
	private NewsfeedVolleyAdapter newsfeedVolleyAdapter;

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
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mListView = (ListView) getView().findViewById(R.id.newsfeed_main_listView);

		newsfeedDatas = new ArrayList<NewsfeedData>();
		newsfeedVolleyAdapter = new NewsfeedVolleyAdapter(getActivity(), newsfeedDatas);
		mListView.setAdapter(newsfeedVolleyAdapter);

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
			// making fresh volley reqeust and getting json
			JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET, URL_FEED, null,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							VolleyLog.d(TAG, "Response: " + response.toString());
							if (response != null) {
								parseJsonFeed(response);
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							VolleyLog.d(TAG, "Error: " + error.getMessage());
						}
					});

			// Adding request to volley request queue
			AppController.getInstance().addToRequestQueue(jsonReq);
		}

		/*
		 * fetchDataFromServer(POST_START_PAGE, POST_SORT);
		 * mListView.setOnItemClickListener(this);
		 */
	}

	private void parseJsonFeed(JSONObject response) {
		try {
			JSONObject jsonData = response.getJSONObject("response");
			jsonData = jsonData.getJSONObject("data");
			JSONArray feedArray = jsonData.getJSONArray("list");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);

				NewsfeedData item = new NewsfeedData();
				// item.setId(feedObj.getInt("id"));
//				item.setName(feedObj.getString("name"));

				// Image might be null sometimes
				String image = feedObj.isNull("imgUrl") ? null : feedObj.getString("imgUrl");
				item.setImage(image);
//				item.setStatus(feedObj.getString("status"));
//				item.setProfilePic(feedObj.getString("profilePic"));
//				item.setTimeStamp(feedObj.getString("timeStamp"));
				item.setPrice(feedObj.getString("price"));

				// url might be null sometimes
				String feedUrl = feedObj.isNull("shopUrl") ? null : feedObj.getString("shopUrl");
				item.setUrl(feedUrl);

				newsfeedDatas.add(item);
			}

			// notify data changes to list adapater
			newsfeedVolleyAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i("Newsfeed", position + "번 포스트 선택");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	
	
	private void fetchDataFromServer(int start, final int sort) {
		RequestParams params = new RequestParams();
		params.put("sort", sort);
		params.put("start", start);

		mClient.get(URL_FEED, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					JSONObject data = response.getJSONObject("response");
					data = data.getJSONObject("data");
					JSONArray itemList = data.getJSONArray("list");
					for (int index = 0; index < itemList.length(); ++index) {
						NewsfeedData newsfeedData = new NewsfeedData();
						newsfeedData.setName(itemList.getJSONObject(index).getString("title"));
						newsfeedData.setImage(itemList.getJSONObject(index).getString("imgUrl"));
						newsfeedData.setPrice(itemList.getJSONObject(index).getString("price"));
						newsfeedDatas.add(newsfeedData);
					}

					setListView();

				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		});
	}

	private void setListView() {
		if (mNewsfeedViewAdapter == null) {
			mNewsfeedViewAdapter = new NewsfeedViewAdapter(getActivity(), R.layout.newsfeed_list_row, newsfeedDatas);
		}
		mListView.setAdapter(mNewsfeedViewAdapter);
	}
}
