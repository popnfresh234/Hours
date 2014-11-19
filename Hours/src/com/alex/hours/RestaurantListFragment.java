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
import android.util.Log;
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
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RestaurantListFragment extends ListFragment {

	private static final String ADMIN = "e7So6F4ytk";

	// Query codes for arguments for identifying how fragment was created
	public static final String QUERY_CODE = "query_code";
	public static final String MY_RESTAURATNS = "my_restaurants";
	public static final String ALL_RESTAURATNS = "all_restaurants";
	public static final String RECENT_RESTAURANTS_ONE_DAY = "recent_restaurants_one_day";
	public static final String RECENT_RESTAURANTS_ONE_WEEK = "recent_restaurants_one_week";
	public static final String RECENT_UPDATE = "recent_update";
	public static final String FAVORITES = "favorites";

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
	private static final int FAVORITES_QUERY_CODE = 4;
	private static final int CLEAR_QUERY_CODE = 5;

	private Button mAddRestaurantButton;
	protected List<Restaurant> mRestaurants;
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	private ParseUser mCurrentUser;
	private String mCurrentUserId;
	private int mQueryCode;
	private boolean mIsMobile;
	private Restaurant mRestaurant;
	private ParseRelation<Restaurant> mCurrentUserRelation;


	@Override
	public void onPause() {
		super.onPause();
		// If for some reason the progress bar is on, shut it off
		getActivity().setProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		// Query parse depending on what code was passed in
		// Moved to onResume so that list updates after adding new restaurant
		// turn on progress indicator when fragment is created
		getActivity().setProgressBarIndeterminateVisibility(true);
		updateListView();

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

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
		if (nMobile == null) {
			mIsMobile = false;
		}
		if (!isWifi && !mIsMobile) {
			// Display warning if no network connection
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.connection_error)
					.setTitle(R.string.connection_error_title)
					.setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();

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
		// If user is admin, set up admin context menu and listeners
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
									R.menu.restaurant_list_item_context_admin,
									menu);
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
			} else {
				// Set contextual action bar for the rest of the users
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
							case R.id.menu_item_favourite_restaurant:

								// get adapter
								RestaurantAdapter adapter = (RestaurantAdapter) getListAdapter();
								// go through items in adapter and add to
								// favorites if
								// checked
								for (int i = adapter.getCount() - 1; i >= 0; i--) {
									if (getListView().isItemChecked(i)) {
										mRestaurant = (adapter.getItem(i));
										ParseUser user = ParseUser
												.getCurrentUser();
										ParseRelation<Restaurant> relation = user
												.getRelation(ParseConstants.RELATION_FAVORITE);
										mCurrentUserRelation = relation;
										mCurrentUserRelation.add(mRestaurant);
										getActivity()
												.setProgressBarIndeterminateVisibility(
														true);
										mCurrentUser
												.saveInBackground(new SaveCallback() {

													@Override
													public void done(
															ParseException arg0) {
														// TODO
														updateListView();
													}
												});

									}
								}
								mode.finish();
								return true;
							case R.id.menu_item_unfavourite_restaurant:

								// get adapter
								RestaurantAdapter adapter1 = (RestaurantAdapter) getListAdapter();
								// go through items in adapter and remove from
								// favorites
								// checked
								for (int i = adapter1.getCount() - 1; i >= 0; i--) {
									if (getListView().isItemChecked(i)) {
										mRestaurant = (adapter1.getItem(i));
										ParseUser user = ParseUser
												.getCurrentUser();
										ParseRelation<Restaurant> relation = user
												.getRelation(ParseConstants.RELATION_FAVORITE);
										mCurrentUserRelation = relation;
										mCurrentUserRelation
												.remove(mRestaurant);
										getActivity()
												.setProgressBarIndeterminateVisibility(
														true);
										mCurrentUser
												.saveInBackground(new SaveCallback() {

													@Override
													public void done(
															ParseException arg0) {
														// TODO
														updateListView();
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
		// TODO
		// Moved here from on prepare options menu
		super.onCreateOptionsMenu(menu, inflater);
		// clear the menu
		menu.clear();
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.restaurant_list, menu);

		// Log.i("Preparing Options", "Preparing Options");
		// super.onPrepareOptionsMenu(menu);
		menu.clear();

		inflater.inflate(R.menu.restaurant_list, menu);

		// Get a reference to the menu and disable buttons so they can't be
		// pressed when loading list
		// Re-enabled after the list is loaded in query parse method


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

			// Query on text submitted
			@Override
			public boolean onQueryTextSubmit(String query) {
				getActivity().setProgressBarIndeterminateVisibility(true);
				mQueryCode = SEARCH_QUERY_CODE;
				queryParse(1, null, query);
				return false;
			}

			// Also query on text changed, immediate results
			@Override
			public boolean onQueryTextChange(String newText) {
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

	// Responses to options menu in action bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		switch (itemId) {
		case R.id.menu_item_new_restaurant:
			startRestaurantActivity();
			break;
		case R.id.action_logout:
			ParseUser.logOut();
			navigateToLogin();
			break;
		case R.id.action_quit:
			getActivity().finish();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Get restaurant from pressed list item, create fragment with
		// restaurants ID
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
		Log.i("QueryCode", "code: " + mQueryCode);
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
				
				ParseQuery<Restaurant> queryCity = new ParseQuery<Restaurant>(
						"Restaurant");
				queryCity.whereContains(
						ParseConstants.KEY_CITY_LOWERCASE, search);

				List<ParseQuery<Restaurant>> queries = new ArrayList<ParseQuery<Restaurant>>();
				queries.add(queryTitle);
				queries.add(queryAddress);
				queries.add(queryCity);

				query = ParseQuery.or(queries);
				query.addAscendingOrder(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE);

			}
			// Query for favorites
			if (mQueryCode == FAVORITES_QUERY_CODE) {
				ParseUser user = ParseUser.getCurrentUser();
				ParseRelation<Restaurant> relation = user
						.getRelation(ParseConstants.RELATION_FAVORITE);
				query = relation.getQuery();
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
					// Query for restaurants complete, now execute another query
					// to get the list of favorited restaurants
					// Add to the adapter for processing so that stars can be
					// set
					mRestaurants = restaurants;
					ParseUser user = ParseUser.getCurrentUser();
					ParseRelation<Restaurant> relation = user
							.getRelation(ParseConstants.RELATION_FAVORITE);
					ParseQuery<Restaurant> relationQuery = relation.getQuery();
					relationQuery
							.findInBackground(new FindCallback<Restaurant>() {

								@Override
								public void done(List<Restaurant> favorites,
										ParseException e) {

									mQueryCode = CLEAR_QUERY_CODE;
									if (getActivity() != null) {
										getActivity()
												.setProgressBarIndeterminateVisibility(
														false);
									}
									// Turn off swipe refresh indicator
									if (mSwipeRefreshLayout.isRefreshing()) {
										mSwipeRefreshLayout
												.setRefreshing(false);
									}

									if (e == null) {
										// We found restaurants!
										if (getActivity() != null) {
							
											RestaurantAdapter adapter = new RestaurantAdapter(
													getActivity(),
													mRestaurants, favorites);
											setListAdapter(adapter);

										}
									} else {
										AlertDialog.Builder builder = new AlertDialog.Builder(
												getActivity());
										builder.setMessage(e.getMessage())
												.setTitle(
														R.string.general_error_title)
												.setPositiveButton(
														android.R.string.ok,
														null);
										AlertDialog dialog = builder.create();
										dialog.show();
									}

								}
							});
				}

			});
		}
	}

	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			updateListView();

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

	private void updateListView() {
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
			if (queryCode.equals(FAVORITES)) {
				mQueryCode = FAVORITES_QUERY_CODE;
				queryParse(1, null, null);
			}
		} else {
			mQueryCode = ALL_RESTAURANT_QUERY_CODE;
			queryParse(1, null, null);
		}
	}

}
