package com.l3cache.snapshop.login;

import io.realm.Realm;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import android.content.Context;

import com.google.gson.Gson;
import com.l3cache.snapshop.constants.SnapConstants;

public class LoginOperator {

	public LoginOperator() {
		// TODO Auto-generated constructor stub
	}

	public boolean authorizeSignIn(Context context, String email, String password) {

		Realm realm = Realm.getInstance(context);
		
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL())
				.setConverter(new GsonConverter(new Gson())).build();
		// 콜백함수에서 사용할 수 있도록 email을 지역변수에 저장
		return false;
	}
}
