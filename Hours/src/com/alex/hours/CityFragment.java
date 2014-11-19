package com.alex.hours;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CityFragment extends Fragment implements OnClickListener {

	private Button mVancouverButton;
	private Button mTaipeiButton;
	private Button mCitySearchButton;
	private EditText mCitySearchField;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_city, container, false);

		mVancouverButton = (Button) v.findViewById(R.id.city_button_vancouver);
		mVancouverButton.setOnClickListener(this);

		mTaipeiButton = (Button) v.findViewById(R.id.city_button_taipei);
		mTaipeiButton.setOnClickListener(this);
		
		mCitySearchField = (EditText)v.findViewById(R.id.city_search_field);

		mCitySearchButton = (Button) v.findViewById(R.id.city_button_search);
		mCitySearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String searchQuery = mCitySearchField.getText().toString()
						.toLowerCase();
				Bundle args = new Bundle();
				args.putString(RestaurantListFragment.QUERY_CODE,
						RestaurantListFragment.SEARCH);
				args.putString(RestaurantListFragment.QUERY, searchQuery);
				RestaurantListFragment myRestaurants = new RestaurantListFragment();
				myRestaurants.setArguments(args);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, myRestaurants).addToBackStack(null).commit();

			}
		});

		return v;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		FragmentManager fragmentManager;
		String searchQuery;
		Bundle args = new Bundle();
		RestaurantListFragment myRestaurants = new RestaurantListFragment();
		switch (id) {
		case R.id.city_button_vancouver:
			searchQuery = "vancouver";
			args.putString(RestaurantListFragment.QUERY_CODE,
					RestaurantListFragment.SEARCH);
			args.putString(RestaurantListFragment.QUERY, searchQuery);

			myRestaurants.setArguments(args);
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, myRestaurants).addToBackStack(null).commit();

			break;

		case R.id.city_button_taipei:
			searchQuery = "taipei";
			args.putString(RestaurantListFragment.QUERY_CODE,
					RestaurantListFragment.SEARCH);
			args.putString(RestaurantListFragment.QUERY, searchQuery);

			myRestaurants.setArguments(args);
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, myRestaurants).addToBackStack(null).commit();
			break;

		}

	}
}
