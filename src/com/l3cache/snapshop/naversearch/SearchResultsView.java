package com.l3cache.snapshop.naversearch;

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
import android.widget.AbsListView;
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
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.app.AppController.TrackerName;
import com.l3cache.snapshop.listener.EndlessScrollListener;
import com.l3cache.snapshop.upload.UploadPostView;
import com.l3cache.snapshop.volley.SearchRequest;

public class SearchResultsView extends Activity implements OnItemClickListener {

	private final String TAG = SearchResultsView.class.getSimpleName();
	private static final String URL_FEED = SnapConstants.SERVER_URL + SnapConstants.SEARCH_REQUEST;
	private final String SORT_SIMILARITY = "sim";
	private final String SORT_ASC = "asc";
	private final String SORT_DESC = "dsc";
	private final String SORT_RECENT = "date";
	private ArrayList<NaverSearchResult> resultItems = new ArrayList<NaverSearchResult>();
	private int numOfTotalResult;
	private int numOfResultDisplay = 20;
	private String sortInto = SORT_SIMILARITY;
	private ListView listView;
	private static String query;
	private SearchResultsVolleyAdapter searchResultsViewVolleyAdapter;
	private Map<String, String> params;
	private SearchView searchView;
	private Spinner mSortSpinner;
	private TextView totalResultTextView;
	private LinearLayout toolBar;
	private EndlessScrollListener endlessScrollListener;
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
		toolBar = (LinearLayout) findViewById(R.id.search_results_tool_bar);
		listView = (ListView) findViewById(R.id.search_results_listView);
		listView.setOnItemClickListener(this);
		searchResultsViewVolleyAdapter = new SearchResultsVolleyAdapter(this, resultItems);
		listView.setAdapter(searchResultsViewVolleyAdapter);

		endlessScrollListener = new EndlessScrollListener() {
			private int mLastFirstVisibleItem;

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (numOfTotalResult < 10 || ((page - 1) * 20) > numOfTotalResult) {
					return;
				}
				Log.i(TAG, "Loading page " + page);
				fetchDataFromServer(page);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				/**
				 * 스크롤을 내리는 경우 상단의 ToolBar를 제거한다.
				 */
				if (mLastFirstVisibleItem < firstVisibleItem) {
					toolBar.setVisibility(View.INVISIBLE);
				}

				/**
				 * 스크롤을 올리는 경우 상단의 ToolBar를 표시한다.
				 */
				if (mLastFirstVisibleItem > firstVisibleItem) {
					toolBar.setVisibility(View.VISIBLE);
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		};

		listView.setOnScrollListener(endlessScrollListener);

		toolBar = (LinearLayout) findViewById(R.id.search_results_tool_bar);

		mSortSpinner = (Spinner) findViewById(R.id.search_results_sort_spinner);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.search_sort_array,
				android.R.layout.simple_spinner_dropdown_item);
		mSortSpinner.setAdapter(spinnerAdapter);
		mSortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String oldSortInto = sortInto;
				switch (position) {
				case 0:
					sortInto = SORT_SIMILARITY;
					break;

				case 1:
					sortInto = SORT_ASC;
					break;

				case 2:
					sortInto = SORT_DESC;
					break;

				case 3:
					sortInto = SORT_RECENT;
					break;
				}

				if (oldSortInto.equals(sortInto)) {
					return;
				}
				reloadData();
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
				.appendQueryParameter("sort", sortInto).build().toString();
		Entry entry = cache.get(targetUrl);
		if (entry != null) {
			/**
			 * 캐시된 데이터를 가져온다.
			 */
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
			/**
			 * 새로운 Volley 리퀘스트를 생성하고, json데이터를 받아온다.
			 */
			params = new HashMap<String, String>();
			params.put("query", query);
			params.put("display", numOfResultDisplay + "");
			params.put("start", offset + "");
			params.put("sort", sortInto);

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

				NaverSearchResult item = new NaverSearchResult();
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
				reloadData();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	private void reloadData() {
		resultItems.clear();
		searchResultsViewVolleyAdapter.notifyDataSetChanged();
		endlessScrollListener.reset();
		fetchDataFromServer(1);
		searchView.clearFocus();
	}

	/**
	 * 개별 item을 선택한 경우, 해당 item의 정보를 토대로 UploadPostView 시작
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		NaverSearchResult item = resultItems.get(position);
		Intent uploadIntent = new Intent("com.l3cache.snapshop.upload.UploadPostView");
		uploadIntent.putExtra("image", item.getImage());
		uploadIntent.putExtra("handler", SnapConstants.INTERNET_BUTTON);
		uploadIntent.putExtra("shopUrl", item.getLink());
		uploadIntent.putExtra("price", item.getLprice());
		uploadIntent.putExtra("title", query);
		startActivityForResult(uploadIntent, SnapConstants.REQUEST_UPLOAD);
	}

	/**
	 * UploadPostView가 업로드에 성공하고 종료된 경우, SearchResultsView(현재) 액티비티를 같이 종료한다.
	 */
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

}
