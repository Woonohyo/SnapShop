package com.l3cache.snapshop.newsfeed;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
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

import com.l3cache.snapshop.R;
import com.l3cache.snapshop.adapter.NewsfeedViewAdapter;
import com.l3cache.snapshop.data.NewsfeedData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NewsfeedView extends Fragment implements OnItemClickListener {
	private static final String URL_FEED = "http://10.73.45.133:8080/app/posts/";
	private int POST_START_PAGE = 1;
	private int POST_SORT = 0;
	private ArrayList<NewsfeedData> newsfeedDatas;
	private AsyncHttpClient mClient = new AsyncHttpClient();
	private ListView mListView;
	private NewsfeedViewAdapter mNewsfeedViewAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_newsfeed, container, false);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if(mNewsfeedViewAdapter != null) {
			mNewsfeedViewAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		fetchDataFromServer(POST_START_PAGE, POST_SORT);
		newsfeedDatas = new ArrayList<NewsfeedData>();
		mListView = (ListView) getView().findViewById(R.id.newsfeed_main_listView);
		mListView.setOnItemClickListener(this);
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
						newsfeedData.setImgName(itemList.getJSONObject(index).getString("imgUrl"));
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
