package com.alex.hours;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;

import com.alex.hours.utilities.ParseConstants;
import com.alex.hours.utilities.RestaurantAdapter;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class RestaurantListFragment extends ListFragment {

	private static final String ADMIN = "e7So6F4ytk";

	// Query codes for arguments for identifying how fragment was created
	public static final String QUERY_CODE = "query_code";
	public static final String MY_RESTAURATNS = "my_restaurants";
	public static final String ALL_RESTAURATNS = "all_restaurants";
	public static final String RECENT_RESTAURANTS_ONE_DAY = "recent_restaurants_one_day";
	public static final String RECENT_RESTAURANTS_ONE_WEEK = "recent_restaurants_one_week";
	public static final String RECENT_UPDATE = "recent_update";

	// Codes for data passed in and out from fragment
	public static final String QUERY = "query";
	public static final String DATE_INCREMENT = "date_increment";
	public static final String SEARCH = "search";
	public static final String CRITERIA = "criteria";
	public static final String CRITERIA_CREATED_AT = "createdAt";
	public static final String CRITERIA_UPDATED_AT = "updatedAt";

	// Query codes for querying parse
	private static final int ALL_RESTAURANT_QUERY_CODE = 0;
	private static final int MY_RESTAURANT_QUERY_CODE = 1;
	private static final int SEARCH_QUERY_CODE = 2;
	private static final int DATE_QUERY_CODE = 3;
	private static final int CLEAR_QUERY_CODE = 4;

	private Button mAddRestaurantButton;
	protected List<Restaurant> mRestaurants;
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	private ParseUser mCurrentUser;
	private String mCurrentUserId;
	private int mQueryCode;
	private boolean mIsMobile;

	@Override
	public void onPause() {
		super.onPause();
		getActivity().setProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onResume() {
		// Update list after coming back from creating new restaurant
		super.onResume();
		if (getArguments() != null
				&& !getArguments().getString(QUERY_CODE).equals(SEARCH)) {
			if (getArguments() != null
					&& getArguments().getString(QUERY_CODE).equals(
							MY_RESTAURATNS)) {
				mQueryCode = MY_RESTAURANT_QUERY_CODE;
				getActivity().setProgressBarIndeterminateVisibility(true);
				queryParse(1, null, null);
			} else {
				mQueryCode = ALL_RESTAURANT_QUERY_CODE;
				getActivity().setProgressBarIndeterminateVisibility(true);
				queryParse(1, null, null);
			}
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.restaurant_list, menu);
		// Get search view
		SearchManager searchManager = (SearchManager) getActivity()
				.getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getActivity().getComponentName()));
		searchView.setQuery("", false);
		searchView.setIconified(true);
		searchView.clearFocus();

		// Get the search icon ID and set its icon
		int searchIconId = searchView.getContext().getResources()
				.getIdentifier("android:id/search_button", null, null);
		ImageView searchIcon = (ImageView) searchView
				.findViewById(searchIconId);
		searchIcon.setImageResource(R.drawable.ic_action_search);

		// Set the background
		int searchPlateId = searchView.getContext().getResources()
				.getIdentifier("android:id/search_plate", null, null);
		searchView.findViewById(searchPlateId).setBackgroundResource(
				R.drawable.apptheme_textfield_activated_holo_light);

		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				getActivity().setProgressBarIndeterminateVisibility(true);
				mQueryCode = SEARCH_QUERY_CODE;
				queryParse(1, null, query);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// mActionBarSearchQuery = newText.toLowerCase();
				mQueryCode = SEARCH_QUERY_CODE;
				queryParse(1, null, newText.toLowerCase());
				return false;
			}
		});

		searchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				getActivity().setProgressBarIndeterminateVisibility(true);
				mQueryCode = ALL_RESTAURANT_QUERY_CODE;
				queryParse(1, null, null);
				return false;
			}
		});

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Turn on action bar
		this.setRetainInstance(true);
		setHasOptionsMenu(true);
		// Set parse analytics
		ParseAnalytics.trackAppOpened(getActivity().getIntent());

		// Check if user is logged in, if not then boot to login screen
		ParseUser currentUser = ParseUser.getCurrentUser();
		mCurrentUser = currentUser;
		if (currentUser == null) {
			navigateToLogin();

		} else {
			mCurrentUserId = ParseUser.getCurrentUser().getObjectId()
					.toString();

		}

		// check for network
		ConnectivityManager check = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nWifi = check.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo nMobile = check
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isWifi = nWifi.isConnected();
		if (nMobile != null) {
			mIsMobile = nMobile.isConnected();
		}
		if(nMobile == null){
			mIsMobile = false;
		}
		if (!isWifi && !mIsMobile) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.connection_error)
					.setTitle(R.string.connection_error_title)
					.setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();

		}
		// turn on progress indicator when fragment is created
		getActivity().setProgressBarIndeterminateVisibility(true);
		// If no argument is passed, show all restaurants
		if (getArguments() == null) {
			queryParse(1, null, null);
		}
		// If an argument is passed, check its query code and query accordingly
		if (getArguments() != null) {
			String queryCode = (String) getArguments().get(QUERY_CODE);
			if (queryCode.equals(MY_RESTAURATNS)) {
				mQueryCode = MY_RESTAURANT_QUERY_CODE;
				queryParse(1, null, null);
			}
			if (queryCode.equals(ALL_RESTAURATNS)) {
				mQueryCode = ALL_RESTAURANT_QUERY_CODE;
				queryParse(1, null, null);
			}
			if (queryCode.equals(SEARCH)) {
				mQueryCode = SEARCH_QUERY_CODE;
				String query = getArguments().getString(QUERY);
				queryParse(1, null, query);
			}
			if (queryCode.equals(RECENT_RESTAURANTS_ONE_DAY)
					|| queryCode.equals(RECENT_RESTAURANTS_ONE_WEEK)
					|| queryCode.equals(RECENT_UPDATE)) {
				mQueryCode = DATE_QUERY_CODE;
				int dateIncrement = getArguments().getInt(DATE_INCREMENT);
				String criteria = getArguments().getString(CRITERIA);
				queryParse(dateIncrement, criteria, null);
			}
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_restaurant_list, container,
				false);
		// Set up swipe refresh layout and set colors
		mSwipeRefreshLayout = (SwipeRefreshLayout) v
				.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.swipeRefresh1,
				R.color.swipeRefresh2, R.color.swipeRefresh3,
				R.color.swipeRefresh4);

		// Listener for empty view add restaurant button
		mAddRestaurantButton = (Button) v
				.findViewById(R.id.add_new_restaurant_button);
		mAddRestaurantButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startRestaurantActivity();
			}
		});
		if (mCurrentUser != null) {
			if (mCurrentUserId.equals(ADMIN)
					|| mCurrentUserId.equals("45LT0GnGVU")) {
				ListView listView = (ListView) v
						.findViewById(android.R.id.list);
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					registerForContextMenu(listView);
				} else {
					listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
					listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

						public boolean onPrepareActionMode(ActionMode mode,
								Menu menu) {
							return false;
							// not used
						}

						public void onDestroyActionMode(ActionMode mode) {
							// not used

						}

						public boolean onCreateActionMode(ActionMode mode,
								Menu menu) {
							MenuInflater inflater = mode.getMenuInflater();
							inflater.inflate(
									R.menu.restaurant_list_item_context, menu);
							return true;
						}

						public boolean onActionItemClicked(ActionMode mode,
								MenuItem item) {
							switch (item.getItemId()) {
							case R.id.menu_item_delete_restaurant:
								// get adapter
								RestaurantAdapter adapter = (RestaurantAdapter) getListAdapter();
								// go through items in adapter and delete if
								// checked
								for (int i = adapter.getCount() - 1; i >= 0; i--) {
									if (getListView().isItemChecked(i)) {
										Restaurant r = (adapter.getItem(i));
										getActivity()
												.setProgressBarIndeterminateVisibility(
														true);

										r.deleteInBackground(new DeleteCallback() {

											@Override
											public void done(ParseException e) {
												if (getArguments() != null
														&& getArguments()
																.getString(
																		QUERY_CODE)
																.equals(MY_RESTAURATNS)) {
													mQueryCode = MY_RESTAURANT_QUERY_CODE;
													queryParse(1, null, null);
												} else {
													mQueryCode = ALL_RESTAURANT_QUERY_CODE;
													queryParse(1, null, null);
												}
											}
										});

									}
								}
								mode.finish();
								return true;
							default:
								return false;
							}
						}

						public void onItemCheckedStateChanged(ActionMode mode,
								int position, long id, boolean checked) {
							// Not used
						}
					});
				}
			}
		}

		return v;
	}

	// Setup Options Menu

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// clear the menu
		menu.clear();
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.restaurant_list, menu);

	}

	// Responses to options menu in action bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		switch (itemId) {
		case R.id.action_logout:
			ParseUser.logOut();
			navigateToLogin();
			break;
		// case R.id.action_my_restaurants:
		// getActivity().setProgressBarIndeterminateVisibility(true);
		// mQueryCode = MY_RESTAURANT_QUERY_CODE;
		// queryParse(1, null, null);
		// break;
		// case R.id.action_all_restaurants:
		// getActivity().setProgressBarIndeterminateVisibility(true);
		// mQueryCode = ALL_RESTAURANT_QUERY_CODE;
		// queryParse(1, null, null);
		// break;
		case R.id.menu_item_new_restaurant:
			startRestaurantActivity();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Restaurant r = ((RestaurantAdapter) getListAdapter()).getItem(position);
		Bundle args = new Bundle();
		if (getArguments() != null) {
			String queryCode = getArguments().getString(QUERY_CODE);
			args.putString(QUERY_CODE, queryCode);

		}

		args.putString(RestaurantFragment.EXTRA_RESTAURANT_ID, r.getObjectId());
		RestaurantFragment restaurantFragment = new RestaurantFragment();
		restaurantFragment.setArguments(args);
		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 1) {
			fragmentManager.popBackStack();
		}
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, restaurantFragment)
				.addToBackStack(null).commit();
	}

	private void queryParse(int dateIncrement, String criteria, String search) {
		// create query
		ParseQuery<Restaurant> query = new ParseQuery<Restaurant>("Restaurant");
		if (ParseUser.getCurrentUser() != null) {

			// Query for Date related searches
			if (mQueryCode == DATE_QUERY_CODE) {

				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_YEAR, dateIncrement);
				Date newDate = calendar.getTime();

				// Query parse for data, store in array

				query.whereGreaterThan(criteria, newDate);
				query.addAscendingOrder(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE);
			}

			// Query for my restaurants
			if (mQueryCode == MY_RESTAURANT_QUERY_CODE) {
				query.whereEqualTo(ParseConstants.KEY_AUTHOR,
						ParseUser.getCurrentUser());
				query.addAscendingOrder(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE);
			}

			// query for all restaurants
			if (mQueryCode == ALL_RESTAURANT_QUERY_CODE) {
				query.whereExists(ParseConstants.KEY_RESTAURANT_TITLE);
				query.addAscendingOrder(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE);
			}

			// query for search
			if (mQueryCode == SEARCH_QUERY_CODE) {

				ParseQuery<Restaurant> queryTitle = new ParseQuery<Restaurant>(
						"Restaurant");
				queryTitle.whereContains(
						ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE, search);

				ParseQuery<Restaurant> queryAddress = new ParseQuery<Restaurant>(
						"Restaurant");
				queryAddress.whereContains(
						ParseConstants.KEY_ADDRESS_LOWER_CASE, search);

				List<ParseQuery<Restaurant>> queries = new ArrayList<ParseQuery<Restaurant>>();
				queries.add(queryTitle);
				queries.add(queryAddress);

				query = ParseQuery.or(queries);
				query.addAscendingOrder(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE);

			}
			if (getArguments() == null) {
				query.whereExists(ParseConstants.KEY_RESTAURANT_TITLE);
				query.addAscendingOrder(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE);
			}
			// execute query
			query.findInBackground(new FindCallback<Restaurant>() {
				@Override
				public void done(List<Restaurant> restaurants, ParseException e) {
					mQueryCode = CLEAR_QUERY_CODE;
					if (getActivity() != null) {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
					}
					// Turn off swipe refresh indicator
					if (mSwipeRefreshLayout.isRefreshing()) {
						mSwipeRefreshLayout.setRefreshing(false);
					}

					if (e == null) {
						// We found restaurants!
						mRestaurants = restaurants;

						String[] titles = new String[mRestaurants.size()];
						int i = 0;
						for (Restaurant restaurant : mRestaurants) {
							titles[i] = restaurant
									.getString(ParseConstants.KEY_RESTAURANT_TITLE);
							i++;
						}
						if (getActivity() != null) {
							if (getListView().getAdapter() == null) {
								RestaurantAdapter adapter = new RestaurantAdapter(
										getListView().getContext(),
										mRestaurants);
								setListAdapter(adapter);
							} else {
								// refill the adapter!
								((RestaurantAdapter) getListView().getAdapter())
										.refill(mRestaurants);
							}
						}
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setMessage(e.getMessage())
								.setTitle(R.string.general_error_title)
								.setPositiveButton(android.R.string.ok, null);
						AlertDialog dialog = builder.create();
						dialog.show();
					}
				}

			});
		}
	}

	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			if (getArguments() != null
					&& getArguments().getString(QUERY_CODE).equals(
							MY_RESTAURATNS)) {
				mQueryCode = MY_RESTAURANT_QUERY_CODE;
				queryParse(1, null, null);
			} else {
				mQueryCode = ALL_RESTAURANT_QUERY_CODE;
				queryParse(1, null, null);
			}

		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			// If a new post has been added, update
			// the list of posts
			if (getArguments() != null
					&& getArguments().getString(QUERY_CODE).equals(
							MY_RESTAURATNS)) {
				mQueryCode = MY_RESTAURANT_QUERY_CODE;
				queryParse(1, null, null);
			} else {
				mQueryCode = ALL_RESTAURANT_QUERY_CODE;
				queryParse(1, null, null);
			}
		}
	}

	private void startRestaurantActivity() {
		Bundle args = new Bundle();
		args.putString(QUERY_CODE, RestaurantFragment.NEW_RESTAURANT_FROM_LIST);
		RestaurantFragment restaurantFragment = new RestaurantFragment();
		restaurantFragment.setArguments(args);
		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 1) {
			fragmentManager.popBackStack();
		}
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, restaurantFragment)
				.addToBackStack(null).commit();
	}

	// Send user back to login screen
	private void navigateToLogin() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

}
