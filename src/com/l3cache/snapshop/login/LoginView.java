package com.l3cache.snapshop.login;

import io.realm.Realm;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.google.gson.Gson;
import com.l3cache.snapshop.MainTabHostView;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.data.User;
import com.l3cache.snapshop.retrofit.SnapShopService;

public class LoginView extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_view);

		SnapPreference pref = new SnapPreference(this);
		if (pref.getValue(SnapPreference.PREF_CURRENT_USER_EMAIL, null) != null) {
			authorizeSignin(pref.getValue(SnapPreference.PREF_CURRENT_USER_EMAIL, null),
					pref.getValue(SnapPreference.PREF_CURRENT_USER_PASSWORD, null));
		}

		Button signInEmailButton = (Button) findViewById(R.id.login_view_email_signin_button);
		signInEmailButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					EmailSignInFragment fragment = new EmailSignInFragment();
					fragment.show(getSupportFragmentManager(), null);
				}
				return true;
			}
		});

		Button signUpEmailButton = (Button) findViewById(R.id.login_view_sign_up_button);
		signUpEmailButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					EmailSignUpFragment fragment = new EmailSignUpFragment();
					fragment.show(getSupportFragmentManager(), null);
				}
				return true;
			}
		});

	}

	private void authorizeSignin(String email, String password) {
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
				.setConverter(new GsonConverter(new Gson())).build();
		// 콜백함수에서 사용할 수 있도록 email을 지역변수에 저장
		SnapShopService service = restAdapter.create(SnapShopService.class);
		service.login(email, password, new Callback<LoginResponse>() {

			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
				Log.i("Login", error.toString());
			}

			@Override
			public void success(LoginResponse loginResponse, Response response) {
				int status = loginResponse.getStatus();

				switch (status) {
				case SnapConstants.SUCCESS: {
					SnapPreference pref = new SnapPreference(getApplicationContext());
					Toast.makeText(getApplicationContext(),
							"Welcome back " + pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0) + " - " + pref.getValue(SnapPreference.PREF_CURRENT_USER_EMAIL, "NO EMAIL"), Toast.LENGTH_LONG)
							.show();

					intentTabHostActivity();

					break;
				}
				case SnapConstants.EMAIL_ERROR: {
					Toast.makeText(getApplicationContext(), "Email Error " + loginResponse.getId(), Toast.LENGTH_LONG)
							.show();
					break;
				}
				case SnapConstants.PASSWORD_ERROR: {
					Toast.makeText(getApplicationContext(), "Password Error " + loginResponse.getId(),
							Toast.LENGTH_LONG).show();
					break;
				}

				case SnapConstants.ERROR: {
					Toast.makeText(getApplicationContext(), "Unrecognized Server Error!" + loginResponse.getId(),
							Toast.LENGTH_LONG).show();
					break;
				}

				default:
					break;
				}

			}
		});
	}

	private void intentTabHostActivity() {
		Intent intent = new Intent(this, MainTabHostView.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		AppEventsLogger.deactivateApp(this);
	}
}
