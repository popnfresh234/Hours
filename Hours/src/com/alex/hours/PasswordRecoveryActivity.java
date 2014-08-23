package com.alex.hours;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class PasswordRecoveryActivity extends Activity {

	private EditText mEmailField;
	private Button mRecoveryButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_recovery);

		mEmailField = (EditText) findViewById(R.id.emailField);

		mRecoveryButton = (Button) findViewById(R.id.recoverPasswordButton);
		mRecoveryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				requestPasswordReset(mEmailField.getText().toString());

			}
		});

	}

	private void requestPasswordReset(String email) {
		ParseUser.requestPasswordResetInBackground(email,
				new RequestPasswordResetCallback() {
					public void done(ParseException e) {
						if (e == null) {
							mEmailField.setText("");
							Toast.makeText(getApplicationContext(),
									R.string.password_recovery_e_mail_sent,
									Toast.LENGTH_LONG).show();
						} else {
							if (e.getCode() == 125) {
								Log.i("Invalid Email", e.toString());
								Toast.makeText(getApplicationContext(),
										R.string.invalid_e_mail,
										Toast.LENGTH_LONG).show();
							}
							else if (e.getCode() == 205) {
								Log.i("Email not found", e.toString());
								Toast.makeText(getApplicationContext(),
										R.string.e_mail_not_found,
										Toast.LENGTH_LONG).show();
							}
							else{
								Log.i("BOOi", e.toString());	
							}
						}
					}
				});
	}
}
