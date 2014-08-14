package com.alex.hours;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

public class RestaurantActivity extends SingleFragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		
	}

	@Override
	protected Fragment createFragment() {
		String restaurantId = (String)getIntent().getStringExtra(RestaurantActivityFragment.EXTRA_RESTAURANT_ID);
		return RestaurantActivityFragment.newInstance(restaurantId);
	
	}
}
