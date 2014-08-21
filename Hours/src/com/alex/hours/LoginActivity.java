package com.alex.hours;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.alex.hours.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	protected EditText mUsername;
	protected EditText mPassword;
	protected Button mLoginButton;
	protected ProgressBar mProgressBar;
	protected AlertDialog mDialog;
	protected ConnectivityManager mCheck;
	protected Boolean mIsWifi;
	protected Boolean mIsMobile;

	protected TextView mSignUpTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);

		// check for network
		mCheck = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.connection_error)
				.setTitle(R.string.connection_error_title)
				.setPositiveButton(android.R.string.ok, null);
		mDialog = builder.create();

		// Hide the action bar
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		// setProgressBar
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

		mSignUpTextView = (TextView) findViewById(R.id.signUpText);
		mSignUpTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(LoginActivity.this,
						SignUpActivity.class);
				startActivity(intent);
			}
		});

		mUsername = (EditText) findViewById(R.id.usernameField);
		mPassword = (EditText) findViewById(R.id.passwordField);
		mLoginButton = (Button) findViewById(R.id.loginButton);
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				NetworkInfo nWifi = mCheck
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				NetworkInfo nMobile = mCheck
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				boolean isWifi = nWifi.isConnected();
				if (nMobile != null) {
					mIsMobile = nMobile.isConnected();
				}
				if(nMobile == null){
					mIsMobile = false;
				}
				if (!isWifi && !mIsMobile) {

					mDialog.show();

				} else {

					String username = mUsername.getText().toString();
					String password = mPassword.getText().toString();

					username = username.trim();
					password = password.trim();

					if (username.isEmpty() || password.isEmpty()) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								LoginActivity.this);
						builder.setMessage(R.string.login_error_message)
								.setTitle(R.string.login_error_title)
								.setPositiveButton(android.R.string.ok, null);
						AlertDialog dialog = builder.create();
						dialog.show();
					} else {
						// Login
						mProgressBar.setVisibility(View.VISIBLE);

						ParseUser.logInInBackground(username, password,
								new LogInCallback() {
									@Override
									public void done(ParseUser user,
											ParseException e) {
										mProgressBar
												.setVisibility(View.INVISIBLE);

										if (e == null) {
											// Success!
											Intent intent = new Intent(
													LoginActivity.this,
													MainActivity.class);
											intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
											startActivity(intent);
										} else {
											AlertDialog.Builder builder = new AlertDialog.Builder(
													LoginActivity.this);
											builder.setMessage(e.getMessage())
													.setTitle(
															R.string.login_error_title)
													.setPositiveButton(
															android.R.string.ok,
															null);
											AlertDialog dialog = builder
													.create();
											dialog.show();
										}
									}
								});
					}
				}
			}
		});
	}
}
