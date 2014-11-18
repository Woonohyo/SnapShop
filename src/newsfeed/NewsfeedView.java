package newsfeed;

import java.util.ArrayList;

import searchshop.SearchResultsView;

import com.l3cache.snapshop.R;
import com.l3cache.snapshop.R.id;
import com.l3cache.snapshop.R.layout;
import com.l3cache.snapshop.R.menu;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class NewsfeedView extends Activity implements OnItemClickListener {

	private ArrayList<NewsfeedData> newsfeedDatas = new ArrayList<NewsfeedData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsfeed);

		// Status Bar 없는 상태로 만들기. XML은 TitleBar까지 없애므로 코드로 처리
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Status Bar에서 App Icon 제거하기
		// getActionBar().setDisplayShowHomeEnabled(false);

		NewsfeedData data1 = new NewsfeedData("JessicaInLove.png", "아이템이름1", "아이템가격1");
		newsfeedDatas.add(data1);

		NewsfeedData data2 = new NewsfeedData("JessicaInLove.png", "아이템이름2", "아이템가격2");
		newsfeedDatas.add(data2);

		ListView listView = (ListView) findViewById(R.id.newsfeed_main_listView);

		NewsfeedViewAdapter newsfeedViewAdapter = new NewsfeedViewAdapter(this, R.layout.newsfeed_list_row,
				newsfeedDatas);
		listView.setAdapter(newsfeedViewAdapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i("Newsfeed", position + "번 포스트 선택");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		ComponentName cn = new ComponentName(this, SearchResultsView.class);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}
	

}
