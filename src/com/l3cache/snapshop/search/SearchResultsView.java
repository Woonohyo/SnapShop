package com.l3cache.snapshop.search;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.adapter.SearchResultsViewAdapter;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.data.SearchResultsItem;
import com.loopj.android.http.AsyncHttpClient;

public class SearchResultsView extends Activity implements OnItemClickListener {

	private final String TAG = SearchResultsView.class.getSimpleName();
	private static final String URL_FEED = SnapConstants.SERVER_URL() + SnapConstants.SEARCH_REQUEST();
	private ArrayList<SearchResultsItem> resultItems = new ArrayList<SearchResultsItem>();
	private int resultPageStart = 1;
	private int numOfTotalResult;
	private int numOfResultDisplay = 20;
	private String resultSorting = "sim";
	private ListView listView;
	private String query;
	private SearchResultsVolleyAdapter searchResultsViewVolleyAdapter;
	private Map<String, String> params;
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		listView = (ListView) findViewById(R.id.search_results_listView);
		listView.setOnItemClickListener(this);
		searchResultsViewVolleyAdapter = new SearchResultsVolleyAdapter(this, resultItems);
		listView.setAdapter(searchResultsViewVolleyAdapter);
		listView.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (numOfTotalResult < 10 || (page * 20) > numOfTotalResult) {
					return;
				}
				fetchDataFromServer(page);
			}
		});
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra("query");
			fetchDataFromServer(resultPageStart);
		}
	}

	private void fetchDataFromServer(int offset) {
		Cache cache = AppController.getInstance().getRequestQueue().getCache();

		Entry entry = cache.get(URL_FEED);
		if (entry != null) {
			// fetch the data from cache
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
			// making fresh volley request and getting json
			params = new HashMap<String, String>();
			params.put("query", query);
			params.put("display", numOfResultDisplay + "");
			params.put("start", offset + "");
			params.put("sort", resultSorting);

			SearchRequest searchReq = new SearchRequest(URL_FEED, params, new Response.Listener<JSONObject>() {
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

			AppController.getInstance().addToRequestQueue(searchReq);
		}
	}

	private void parseJsonFeed(JSONObject response) {
		try {
			JSONObject jsonData = response.getJSONObject("response");
			jsonData = jsonData.getJSONObject("data");
			numOfTotalResult = jsonData.getInt("total");
			JSONArray feedArray = jsonData.getJSONArray("list");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);

				SearchResultsItem item = new SearchResultsItem();
				String image = feedObj.isNull("image") ? null : feedObj.getString("image");
				item.setImage(image);
				item.setTitle(feedObj.getString("title"));
				item.setLprice(feedObj.getInt("lprice"));
				item.setHprice(feedObj.getInt("hprice"));
				item.setMallName(feedObj.getString("mallName"));
				item.setLink(feedObj.getString("link"));
				item.setProductId(feedObj.getLong("productId"));

				resultItems.add(item);
			}

			// notify data changes to list adapater
			searchResultsViewVolleyAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
			Toast toast = Toast.makeText(SearchResultsView.this, "검색 결과가 없습니다!", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i("Search", position + "번 선택");

	}
}
