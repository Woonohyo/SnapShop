package com.l3cache.snapshop.login;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.l3cache.snapshop.MainTabHostView;
import com.l3cache.snapshop.R;
import com.l3cache.snapshop.SnapPreference;
import com.l3cache.snapshop.app.AppController;
import com.l3cache.snapshop.app.AppController.TrackerName;
import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.retrofit.SnapShopService;

public class LoginView extends FragmentActivity {
	public static final String EXTRA_MESSAGE = "message";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static String TAG = LoginView.class.getSimpleName();
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String SENDER_ID = "447902358753";
	private GoogleCloudMessaging gcm;
	private String regid;
	Context context;
	private SnapPreference pref;
	private ProgressDialog dialogProgress;
	private String mEmail;
	private String mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_view);
		// Get tracker.
		Tracker t = ((AppController) getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName(LoginView.class.getSimpleName());
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());

		context = getApplicationContext();
		pref = new SnapPreference(context);

		if (pref.getValue(SnapPreference.PREF_CURRENT_USER_EMAIL, null) != null) {
			mEmail = pref.getValue(SnapPreference.PREF_CURRENT_USER_EMAIL, null);
			mPassword = pref.getValue(SnapPreference.PREF_CURRENT_USER_PASSWORD, null);
			authorizeSignin();
		} else {

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

		// Check device for Play Services APK.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId();

			if (regid.isEmpty()) {
				registerInBackground();
			} else {
				Log.i(TAG, "No valid Google Play Services APK Found");
			}
		}
	}

	private String getRegistrationId() {
		String registrationId = pref.getValue(SnapPreference.PROPERTY_REG_ID, "");
		return registrationId;
	}

	private void registerInBackground() {
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected String doInBackground(Object... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;
					Log.i(TAG, msg);

					storeRegistrationId(context, regid);
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regid) {
		pref.put(SnapPreference.PROPERTY_REG_ID, regid);
	}

	private void authorizeSignin() {
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SnapConstants.SERVER_URL)
				.setConverter(new GsonConverter(new Gson())).build();
		SnapShopService service = restAdapter.create(SnapShopService.class);
		service.login(mEmail, mPassword, new Callback<SignInResponse>() {

			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
				Log.i("Login", error.toString());
			}

			@Override
			public void success(SignInResponse loginResponse, Response response) {
				int status = loginResponse.getStatus();
				switch (status) {
				case SnapConstants.SUCCESS: {
					SnapPreference pref = new SnapPreference(getApplicationContext());
					Toast.makeText(
							getApplicationContext(),
							"Welcome back " + pref.getValue(SnapPreference.PREF_CURRENT_USER_ID, 0) + " - "
									+ pref.getValue(SnapPreference.PREF_CURRENT_USER_EMAIL, "NO EMAIL"),
							Toast.LENGTH_LONG).show();

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
		checkPlayServices();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		AppEventsLogger.deactivateApp(this);
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}
}
