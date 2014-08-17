package com.alex.hours;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

public class MainActivity extends SingleFragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Fragment createFragment() {
		return new MainActivityFragment();
	}
//TODO rewrite to use on new instance method
}
