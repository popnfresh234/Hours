package com.alex.hours;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

public class MainActivityFragment extends Fragment {

	private EditText mSearchField;
	private Button mSearchButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		mSearchField = (EditText) v.findViewById(R.id.searchField);

		mSearchButton = (Button) v.findViewById(R.id.searchButton);
		mSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String searchQuery = mSearchField.getText().toString()
						.toLowerCase();
				Bundle args = new Bundle();
				args.putString(RestaurantListFragment.QUERY_CODE,
						RestaurantListFragment.SEARCH);
				args.putString(RestaurantListFragment.QUERY, searchQuery);
				RestaurantListFragment myRestaurants = new RestaurantListFragment();
				myRestaurants.setArguments(args);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, myRestaurants).commit();

			}
		});

		return v;
	}
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.home, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentManager fragmentManager;
		switch (item.getItemId()) {
		case R.id.menu_item_new_restaurant:
			RestaurantFragment restaurantFragment = new RestaurantFragment();
			fragmentManager = getFragmentManager();
			fragmentManager.popBackStack();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, restaurantFragment)
					.addToBackStack(null).commit();

			break;
		case R.id.action_logout:
			ParseUser.logOut();
			navigateToLogin();
			break;
		case R.id.action_my_restaurants:
			// TODO
			Bundle args = new Bundle();
			args.putString(RestaurantListFragment.QUERY_CODE,
					RestaurantListFragment.MY_RESTAURATNS);
			RestaurantListFragment myRestaurants = new RestaurantListFragment();
			myRestaurants.setArguments(args);
			fragmentManager = getFragmentManager();
			fragmentManager.popBackStack();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, myRestaurants)
					.addToBackStack(null).commit();
			break;
		case R.id.action_all_restaurants:
			RestaurantListFragment allRestaurants = new RestaurantListFragment();
			fragmentManager = getFragmentManager();
			fragmentManager.popBackStack();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, allRestaurants)
					.addToBackStack(null).commit();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void navigateToLogin() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
}
