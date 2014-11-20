package com.l3cache.snapshop.info;

import com.l3cache.snapshop.R;
import com.l3cache.snapshop.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class InfoView extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_info, container, false);
	}
}
