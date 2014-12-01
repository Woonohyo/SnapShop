package com.l3cache.snapshop.login;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.l3cache.snapshop.R;

public class LoginView extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_view);

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
	}
}
