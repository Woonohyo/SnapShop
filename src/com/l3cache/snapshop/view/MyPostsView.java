package com.l3cache.snapshop.view;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapConstants;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.controller.AppController;
import com.l3cache.snapshop.controller.EndlessScrollListener;
import com.l3cache.snapshop.controller.MyPostsAdapter;
import com.l3cache.snapshop.controller.AppController.TrackerName;
import com.l3cache.snapshop.model.NewsfeedData;
import com.l3cache.snapshop.retrofit.DefaultResponse;
import com.l3cache.snapshop.retrofit.NewsfeedRequest;
import com.l3cache.snapshop.retrofit.SnapShopService;

public class MyPostsView extends Fragment {
	private static final String TAG = MyPostsView.class.getSimpleName();
	private GridView mGridView;
	private ArrayList<NewsfeedData> mFeedItems;
	private MyPostsAdapter mListAdapter;
	private int mTotalResult;

	private static String URL_FEED = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Tracker t = ((AppController) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName(MyPostsView.class.getSimpleName());
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
		SnapPreference pref = new SnapPreference(getActivity());
		URL_FEED = SnapConstants.SERVER_URL
				+ SnapConstants.MYPOST_REQUEST(pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0));

		View view = inflater.inflate(R.layout.activity_my_posts_view, container, false);

		mGridView = (GridView) view.findViewById(R.id.my_posts_main_grid_view);
		mFeedItems = new ArrayList<NewsfeedData>();
		mListAdapter = new MyPostsAdapter(getActivity(), mFeedItems);
		mGridView.setAdapter(mListAdapter);
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
					.setConverter(new GsonConverter(new Gson())).build();
			SnapShopService service = restAdapter.create(SnapShopService.class);
			SnapPreference pref = new SnapPreference(getActivity());
			int pid;
			int mPosition;

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				AlertDialog.Builder alt_bld = new AlertDialog.Builder(getActivity());
				pid = (int) id;
				mPosition = position;

				alt_bld.setMessage("Do you want to delete your post?").setCancelable(true)
						.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								Log.i(TAG,
										"Deleting item pid - " + pid + " of user - "
												+ pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0));
								service.deletePost(pid, pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0),
										new Callback<DefaultResponse>() {

											@Override
											public void success(DefaultResponse defResp, retrofit.client.Response arg1) {
												if (defResp.getStatus() == 10) {
													Toast.makeText(getActivity(), "The post was deleted.",
															Toast.LENGTH_SHORT).show();
													mFeedItems.remove(mPosition);
													mListAdapter.notifyDataSetChanged();
												} else if (defResp.getStatus() == 20) {
													Toast.makeText(getActivity(), "Deletion failed", Toast.LENGTH_SHORT)
															.show();

												}
											}

											@Override
											public void failure(RetrofitError arg0) {
												Toast.makeText(getActivity(),
														"Network Error - " + arg0.getLocalizedMessage(),
														Toast.LENGTH_SHORT).show();

											}
										});

							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				AlertDialog alert = alt_bld.create();
				alert.setTitle("Delete Post");
				alert.show();

				return true;
			}
		});

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
			params.put("start", start + "");

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
					Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
				}
			});
			AppController.getInstance().addToRequestQueue(jsonReq);
		}
	}

	private void parseJsonFeed(JSONObject response) {
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
		}
	}
}