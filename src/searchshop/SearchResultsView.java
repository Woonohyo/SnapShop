package searchshop;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.l3cache.snapshop.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SearchResultsView extends Activity implements OnItemClickListener {

	private static final String BASE_URL = "http://10.73.45.133:8080/search/shop";
	private ArrayList<SearchResultsData> searchResultsDatas;
	private int resultPageStart = 1;
	private int numOfResultDisplay = 20;
	private String resultSorting = "sim";
	private String query;
	private ListView listView;
	private SearchResultsViewAdapter searchResultsViewAdapter;
	private AsyncHttpClient client = new AsyncHttpClient();
	private RequestParams params = new RequestParams();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		handleIntent(getIntent());
		listView = (ListView) findViewById(R.id.search_results_listView);
		listView.setOnItemClickListener(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			searchResultsDatas = new ArrayList<SearchResultsData>();
			query = intent.getStringExtra(SearchManager.QUERY);
			this.setTitle("Search Results for " + query);
			fetchDataFromServer(query, resultPageStart);
		}
	}

	private void fetchDataFromServer(String query, int offset) {
		params.put("query", query);
		params.put("display", numOfResultDisplay);
		params.put("start", offset);
		params.put("sort", resultSorting);

		client.get(BASE_URL, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					JSONObject data = response.getJSONObject("response");
					JSONArray itemList = data.getJSONArray("itemList");
					for (int index = 0; index < itemList.length(); ++index) {
						SearchResultsData searchData = new SearchResultsData();
						searchData.setTitle(itemList.getJSONObject(index).getString("title"));
						searchData.setLink(itemList.getJSONObject(index).getString("link"));
						searchData.setImage(itemList.getJSONObject(index).getString("image"));
						searchData.setLprice(itemList.getJSONObject(index).getInt("lprice"));
						searchData.setHprice(itemList.getJSONObject(index).getInt("hprice"));
						searchData.setMallName(itemList.getJSONObject(index).getString("mallName"));
						searchData.setProductId(itemList.getJSONObject(index).getLong("productId"));
						searchData.setProductType(itemList.getJSONObject(index).getInt("productType"));
						;

						searchResultsDatas.add(searchData);
					}

					setListView();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

				super.onFailure(statusCode, headers, responseString, throwable);
			}

		});
	}

	private void setListView() {
		if (searchResultsViewAdapter == null) {
			searchResultsViewAdapter = new SearchResultsViewAdapter(this, R.layout.searchresults_list_row,
					searchResultsDatas);
			listView.setAdapter(searchResultsViewAdapter);
			listView.setOnScrollListener(new EndlessScrollListener() {

				@Override
				public void onLoadMore(int page, int totalItemsCount) {
					customLoadMoreDataFromApi(page);
				}
			});
			Log.i("Search", "setting Adapter");

		}
	}

	private void customLoadMoreDataFromApi(int offset) {
		Log.i("Search", "load more");
		fetchDataFromServer(query, offset);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_results, menu);
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
		// TODO Auto-generated method stub

	}
}
