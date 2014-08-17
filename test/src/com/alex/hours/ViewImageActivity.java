package com.alex.hours;

import android.support.v4.app.Fragment;

public class ViewImageActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new ViewImageFragment();
	}

}
