package com.alex.hours;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alex.hours.utilities.CityAdapter;
import com.alex.hours.utilities.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class CityFragment extends ListFragment {

	private List<Restaurant> mRestaurants;
	private List<String> mUniqueCities;
	private ListView mListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		FragmentManager fm = getFragmentManager();
		//Log.i("STACKCITY", String.valueOf(fm.getBackStackEntryCount()));
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_city_list, container, false);

		mSwipeRefreshLayout = (SwipeRefreshLayout) v
				.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.swipeRefresh1,
				R.color.swipeRefresh2, R.color.swipeRefresh3,
				R.color.swipeRefresh4);

		mListView = (ListView) v.findViewById(android.R.id.list);

		queryParse();

		return v;
	}

	// @Override
	// public void onClick(View v) {
	// int id = v.getId();
	// FragmentManager fragmentManager;
	// String searchQuery;
	// Bundle args = new Bundle();
	// RestaurantListFragment myRestaurants = new RestaurantListFragment();
	// switch (id) {
	// case R.id.city_button_vancouver:
	// searchQuery = "vancouver";
	// args.putString(RestaurantListFragment.QUERY_CODE,
	// RestaurantListFragment.SEARCH);
	// args.putString(RestaurantListFragment.QUERY, searchQuery);
	//
	// myRestaurants.setArguments(args);
	// fragmentManager = getFragmentManager();
	// fragmentManager.beginTransaction()
	// .replace(R.id.content_frame, myRestaurants)
	// .addToBackStack(null).commit();
	//
	// break;
	//
	// case R.id.city_button_taipei:
	// searchQuery = "taipei";
	// args.putString(RestaurantListFragment.QUERY_CODE,
	// RestaurantListFragment.SEARCH);
	// args.putString(RestaurantListFragment.QUERY, searchQuery);
	//
	// myRestaurants.setArguments(args);
	// fragmentManager = getFragmentManager();
	// fragmentManager.beginTransaction()
	// .replace(R.id.content_frame, myRestaurants)
	// .addToBackStack(null).commit();
	// break;
	//
	// }
	//
	// }

	private void queryParse() {
		getActivity().setProgressBarIndeterminateVisibility(true);
		List<String> uniqueCities = new ArrayList<String>();
		mUniqueCities = uniqueCities;
		ParseQuery<Restaurant> query = new ParseQuery<Restaurant>("Restaurant");
		query.whereExists(ParseConstants.KEY_CITY);
		query.addAscendingOrder(ParseConstants.KEY_CITY);
		query.findInBackground(new FindCallback<Restaurant>() {

			@Override
			public void done(List<Restaurant> restaurants, ParseException e) {
				mRestaurants = restaurants;
				for (int i = 0; i < mRestaurants.size(); i++) {
					Restaurant restaurant = (Restaurant) mRestaurants.get(i);
					String cityName = restaurant.getCity();
					if (mUniqueCities.size() > 0
							&& mUniqueCities.contains(cityName)) {
						Log.i("not unique", "contained");
					} else {
						mUniqueCities.add(cityName);
					}
				}

				// Set Custom Adapter
				CityAdapter adapter = new CityAdapter(getActivity(),
						mUniqueCities);
				mListView.setAdapter(adapter);
				getActivity().setProgressBarIndeterminateVisibility(false);
				if (mSwipeRefreshLayout.isRefreshing()) {
					mSwipeRefreshLayout.setRefreshing(false);
				}
			}
		});

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		FragmentManager fragmentManager;
		String searchQuery;
		Bundle args = new Bundle();
		RestaurantListFragment myRestaurants = new RestaurantListFragment();

		// Get the name of the city from the listview
		searchQuery = l.getItemAtPosition(position).toString().toLowerCase();

		// put name into bundle and send to restaurant list fragment for parse
		// query
		args.putString(RestaurantListFragment.QUERY_CODE,
				RestaurantListFragment.SEARCH);
		args.putString(RestaurantListFragment.QUERY, searchQuery);

		myRestaurants.setArguments(args);
		fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, myRestaurants)
				.addToBackStack(null).commit();

	}

	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			queryParse();

		}

	};
}
