package com.l3cache.snapshop.newsfeed;

import java.util.ArrayList;

import com.l3cache.snapshop.R;
import com.l3cache.snapshop.R.id;
import com.l3cache.snapshop.R.layout;
import com.l3cache.snapshop.R.menu;
import com.l3cache.snapshop.search.SearchResultsView;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class NewsfeedView extends Fragment implements OnItemClickListener {

	private ArrayList<NewsfeedData> newsfeedDatas = new ArrayList<NewsfeedData>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_newsfeed, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		NewsfeedData data1 = new NewsfeedData("JessicaInLove.png", "아이템이름1", "아이템가격1");
		newsfeedDatas.add(data1);

		NewsfeedData data2 = new NewsfeedData("JessicaInLove.png", "아이템이름2", "아이템가격2");
		newsfeedDatas.add(data2);

		ListView listView = (ListView) getView().findViewById(R.id.newsfeed_main_listView);

		NewsfeedViewAdapter newsfeedViewAdapter = new NewsfeedViewAdapter(getActivity(), R.layout.newsfeed_list_row,
				newsfeedDatas);
		listView.setAdapter(newsfeedViewAdapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i("Newsfeed", position + "번 포스트 선택");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

}
