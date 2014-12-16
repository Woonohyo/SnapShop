package com.l3cache.snapshop.activity;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.adapter.EndlessScrollListener;
import com.l3cache.snapshop.adapter.SearchResultsVolleyAdapter;
import com.l3cache.snapshop.controller.AppController;
import com.l3cache.snapshop.controller.AppController.TrackerName;
import com.l3cache.snapshop.model.SearchResultsItem;
import com.l3cache.snapshop.retrofit.SearchRequest;

public class SearchResultsView extends Activity implements OnItemClickListener, OnItemSelectedListener {

	private final String TAG = SearchResultsView.class.getSimpleName();
	private static final String URL_FEED = SnapConstants.SERVER_URL + SnapConstants.SEARCH_REQUEST;
	private final String SORT_SIMILARITY = "sim";
	private final String SORT_ASC = "asc";
	private final String SORT_DESC = "dsc";
	private final String SORT_RECENT = "date";
	private ArrayList<SearchResultsItem> resultItems = new ArrayList<SearchResultsItem>();
	private int resultPageStart = 1;
	private int numOfTotalResult;
	private int numOfResultDisplay = 20;
	private String sortingBy = SORT_SIMILARITY;
	private ListView listView;
	private static String query;
	private SearchResultsVolleyAdapter searchResultsViewVolleyAdapter;
	private Map<String, String> params;
	private SearchView searchView;
	private Spinner mSortSpinner;
	private TextView totalResultTextView;
	private LinearLayout mToolBar;
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get tracker.
		Tracker t = ((AppController) getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName(SearchResultsView.class.getSimpleName());
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());

		setContentView(R.layout.activity_search_results);
		setTitle("SnapShop Search");
		mToolBar = (LinearLayout) findViewById(R.id.search_results_tool_bar);
		listView = (ListView) findViewById(R.id.search_results_listView);
		listView.setOnItemClickListener(this);
		searchResultsViewVolleyAdapter = new SearchResultsVolleyAdapter(this, resultItems);
		listView.setAdapter(searchResultsViewVolleyAdapter);
		listView.setOnScrollListener(new EndlessScrollListener(5, 1) {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (numOfTotalResult < 10 || ((page - 1) * 20) > numOfTotalResult) {
					return;
				}
				Log.i(TAG, "Loading page " + page);
				fetchDataFromServer(page);
			}
		});

		mSortSpinner = (Spinner) findViewById(R.id.search_results_sort_spinner);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.search_sort_array,
				android.R.layout.simple_spinner_dropdown_item);
		mSortSpinner.setAdapter(spinnerAdapter);
		mSortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0: {
					break;
				}

				case 1: {
					break;
				}

				case 2: {
					break;

				}

				case 3: {
					break;

				}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		totalResultTextView = (TextView) findViewById(R.id.search_results_totalNumber_textView);
	}

	private void fetchDataFromServer(int offset) {
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Uri.Builder builder = new Uri.Builder();
		String targetUrl = builder.encodedPath(URL_FEED).appendQueryParameter("query", query)
				.appendQueryParameter("display", numOfResultDisplay + "").appendQueryParameter("start", offset + "")
				.appendQueryParameter("sort", sortingBy).build().toString();
		Entry entry = cache.get(targetUrl);
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
			params.put("sort", sortingBy);

			SearchRequest searchReq = new SearchRequest(URL_FEED, params, new Response.Listener<JSONObject>() {
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
					Toast toast = Toast.makeText(SearchResultsView.this, "Network Error", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			});

			AppController.getInstance().addToRequestQueue(searchReq);
		}
	}

	private void parseJsonFeed(JSONObject response) {
		try {
			JSONObject jsonData = response.getJSONObject("data");
			numOfTotalResult = jsonData.getInt("total");
			DecimalFormat formatter = new DecimalFormat("#,###,###");
			totalResultTextView.setText(formatter.format(numOfTotalResult) + "건");
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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		int textViewId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		TextView textView = (TextView) searchView.findViewById(textViewId);
		textView.setTextColor(Color.WHITE);
		textView.setHintTextColor(Color.WHITE);

		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.i("Search", "Query Submitted: " + query);
				SearchResultsView.query = query;
				resultItems.clear();
				searchResultsViewVolleyAdapter.notifyDataSetChanged();
				fetchDataFromServer(resultPageStart);
				searchView.clearFocus();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
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
		SearchResultsItem item = resultItems.get(position);
		Intent uploadIntent = new Intent(this, UploadPostView.class);
		uploadIntent.putExtra("image", item.getImage());
		uploadIntent.putExtra("handler", SnapConstants.INTERNET_BUTTON);
		uploadIntent.putExtra("shopUrl", item.getLink());
		uploadIntent.putExtra("price", item.getLprice());
		uploadIntent.putExtra("title", query);
		startActivityForResult(uploadIntent, SnapConstants.REQUEST_UPLOAD);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "Request: " + requestCode + " Result: " + resultCode);
		if (requestCode == SnapConstants.REQUEST_UPLOAD) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
				Log.i(TAG, "finishing");
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Log.i(TAG, parent.getItemAtPosition(position).toString());

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}
}
