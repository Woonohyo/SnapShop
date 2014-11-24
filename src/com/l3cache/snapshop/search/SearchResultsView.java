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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.adapter.SearchResultsViewAdapter;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.data.SearchResultsItem;
import com.loopj.android.http.AsyncHttpClient;

public class SearchResultsView extends Activity implements OnItemClickListener {

	private final String TAG = SearchResultsView.class.getSimpleName();
	private static final String URL_FEED = "http://10.73.45.133:8080/search/shop";
	private ArrayList<SearchResultsItem> resultItems = new ArrayList<SearchResultsItem>();
	private int resultPageStart = 1;
	private int numOfTotalResult;
	private int numOfResultDisplay = 20;
	private String resultSorting = "sim";
	private ListView listView;
	private String query;
	private SearchResultsViewAdapter searchResultsViewAdapter;
	private SearchResultsVolleyAdapter searchResultsViewVolleyAdapter;
	private AsyncHttpClient client = new AsyncHttpClient();
	private Map<String, String> params;
	private ProgressDialog progressDialog;
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
				fetchDataFromServer(page);
			}
		});
		handleIntent(getIntent());
	}
	
	private void customLoadMoreDataFromApi(int offset) {
		Log.i("Search", "load more and total: " + numOfTotalResult);
		// 검색 결과가 10개 미만인 경우 추가 로드 방지
		if (numOfTotalResult < 10) {
			return;
		}
		fetchDataFromServer(offset);
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
			/*
			 * String requestUrl =
			 * "http://10.73.45.133:8080/search/shop?query=청바지&display=20&start=1&sort=sim"
			 * ; JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
			 * requestUrl, null, new Response.Listener<JSONObject>() {
			 * 
			 * @Override public void onResponse(JSONObject response) {
			 * VolleyLog.d(TAG, "Response: " + response.toString()); if
			 * (response != null) { parseJsonFeed(response); } } }, new
			 * Response.ErrorListener() {
			 * 
			 * @Override public void onErrorResponse(VolleyError error) {
			 * VolleyLog.d(TAG, "Error: " + error.getMessage()); } });
			 */

			// Adding request to volley request queue
			AppController.getInstance().addToRequestQueue(searchReq);
		}

		/*
		 * resultItems = new ArrayList<SearchResultsItem>(); query =
		 * intent.getStringExtra(SearchManager.QUERY);
		 * this.setTitle("Search Results for " + query);
		 * fetchDataFromServer(query, resultPageStart);
		 */
	}

	private void parseJsonFeed(JSONObject response) {
		try {
			JSONObject jsonData = response.getJSONObject("response");
			jsonData = jsonData.getJSONObject("data");
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
		}
	}

	/*
	 * private void fetchDataFromServer(String query, int offset) {
	 * handler.post(new Runnable() {
	 * 
	 * @Override public void run() { progressDialog =
	 * ProgressDialog.show(SearchResultsView.this, "", "Fetching..."); } });
	 * 
	 * params.put("query", query); params.put("display", numOfResultDisplay+"");
	 * params.put("start", offset+""); params.put("sort", resultSorting);
	 * 
	 * client.get(URL_FEED, params, new JsonHttpResponseHandler() {
	 * 
	 * @Override public void onSuccess(int statusCode, Header[] headers,
	 * JSONObject response) { try { JSONObject data =
	 * response.getJSONObject("response"); data = data.getJSONObject("data");
	 * numOfTotalResult = data.getInt("total"); JSONArray itemList =
	 * data.getJSONArray("list");
	 * 
	 * for (int index = 0; index < itemList.length(); ++index) {
	 * SearchResultsItem searchData = new SearchResultsItem();
	 * searchData.setTitle(itemList.getJSONObject(index).getString("title"));
	 * searchData.setLink(itemList.getJSONObject(index).getString("link"));
	 * searchData.setImage(itemList.getJSONObject(index).getString("image"));
	 * searchData.setLprice(itemList.getJSONObject(index).getInt("lprice"));
	 * searchData.setHprice(itemList.getJSONObject(index).getInt("hprice"));
	 * searchData
	 * .setMallName(itemList.getJSONObject(index).getString("mallName"));
	 * searchData
	 * .setProductId(itemList.getJSONObject(index).getLong("productId"));
	 * searchData
	 * .setProductType(itemList.getJSONObject(index).getInt("productType")); ;
	 * 
	 * resultItems.add(searchData); } Log.i("Search", "Number of DataSource: " +
	 * resultItems.size());
	 * 
	 * setListView();
	 * 
	 * } catch (JSONException e) { handler.post(new Runnable() {
	 * 
	 * @Override public void run() { progressDialog.cancel(); } }); Toast toast
	 * = Toast.makeText(SearchResultsView.this, "검색 결과가 없습니다!",
	 * Toast.LENGTH_LONG); toast.setGravity(Gravity.CENTER, 0, 0); toast.show();
	 * } }
	 * 
	 * @Override public void onFailure(int statusCode, Header[] headers, String
	 * responseString, Throwable throwable) { super.onFailure(statusCode,
	 * headers, responseString, throwable); } }); }
	 */

	private void setListView() {
		if (searchResultsViewAdapter == null) {
			searchResultsViewAdapter = new SearchResultsViewAdapter(this, R.layout.searchresults_list_row, resultItems);
			listView.setAdapter(searchResultsViewAdapter);
			listView.setOnScrollListener(new EndlessScrollListener() {
				@Override
				public void onLoadMore(int page, int totalItemsCount) {
					customLoadMoreDataFromApi(page);
				}
			});
			Log.i("Search", "setting Adapter");
		}

		handler.post(new Runnable() {
			@Override
			public void run() {
				progressDialog.cancel();
			}
		});
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
