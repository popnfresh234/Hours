package com.alex.hours;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

public class MainActivityFragment extends Fragment implements
		OnFocusChangeListener {

	private EditText mSearchField;
	private Button mSearchButton;
	private Button mAllRestaurantsButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		// Check if logged in, if not boot to login screen
		if (ParseUser.getCurrentUser() == null) {
			navigateToLogin();
		}

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.home, menu);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setProgressBarIndeterminateVisibility(false);
		mSearchField.setText("");
		


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		FragmentManager fm = getFragmentManager();
		Log.i("Fragment Counter", String.valueOf(fm.getBackStackEntryCount()));

		mSearchField = (EditText) v.findViewById(R.id.searchField);
		mSearchField.setOnFocusChangeListener(this);

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
						.replace(R.id.content_frame, myRestaurants)
						.addToBackStack(null).commit();

			}
		});

		mAllRestaurantsButton = (Button) v
				.findViewById(R.id.allRestaurantsButton);
		mAllRestaurantsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Bundle args = new Bundle();
				args.putString(RestaurantListFragment.QUERY_CODE,
						RestaurantListFragment.ALL_RESTAURATNS);
				RestaurantListFragment allRestaurants = new RestaurantListFragment();
				allRestaurants.setArguments(args);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, allRestaurants)
						.addToBackStack(null).commit();

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
		Bundle args = new Bundle();
		switch (item.getItemId()) {
		case R.id.menu_item_new_restaurant:
			args.putString(RestaurantListFragment.QUERY_CODE,
					RestaurantFragment.NEW_RESTAURANT_FROM_HOME);
			RestaurantFragment restaurantFragment = new RestaurantFragment();
			restaurantFragment.setArguments(args);
			fragmentManager = getFragmentManager();

			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, restaurantFragment)
					.addToBackStack(null).commit();

			break;
		case R.id.action_logout:
			ParseUser.logOut();
			navigateToLogin();
			break;
		case R.id.action_quit:
			getActivity().finish();
			break;
		// case R.id.action_my_restaurants:
		// // attach code so next fragment knows how it was created
		//
		// args.putString(RestaurantListFragment.QUERY_CODE,
		// RestaurantListFragment.MY_RESTAURATNS);
		// RestaurantListFragment myRestaurants = new RestaurantListFragment();
		// myRestaurants.setArguments(args);
		// fragmentManager = getFragmentManager();
		// fragmentManager.beginTransaction()
		// .replace(R.id.content_frame, myRestaurants)
		// .addToBackStack(null).commit();
		// break;
		// case R.id.action_all_restaurants:
		// // attach code so next fragment knows how it was created
		// args.putString(RestaurantListFragment.QUERY_CODE,
		// RestaurantListFragment.ALL_RESTAURATNS);
		// RestaurantListFragment allRestaurants = new RestaurantListFragment();
		// allRestaurants.setArguments(args);
		// fragmentManager = getFragmentManager();
		// fragmentManager.beginTransaction()
		// .replace(R.id.content_frame, allRestaurants)
		// .addToBackStack(null).commit();
		//
		// break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void navigateToLogin() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	// set the search icon when focused on the edittext
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id = v.getId();
		switch (id) {
		case R.id.searchField:
			if (hasFocus) {
				mSearchField.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						android.R.drawable.ic_menu_search, 0);
			} else {
				mSearchField
						.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			}
			break;
		}

	}
}
