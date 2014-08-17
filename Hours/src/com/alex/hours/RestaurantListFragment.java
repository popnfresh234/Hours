package com.alex.hours;

import java.util.ArrayList;
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
import android.support.v4.app.FragmentTransaction;
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
import com.parse.ParseUser;

public class RestaurantListFragment extends ListFragment {

	private static final String ADMIN = "e7So6F4ytk";
	public static final String QUERY_CODE = "querycode";
	public static final String MY_RESTAURATNS = "my_restauratns";
	public static final String QUERY = "query";
	public static final String SEARCH = "search";

	private Button mAddRestaurantButton;
	protected List<Restaurant> mRestaurants;
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	private ParseUser mCurrentUser;
	private String mCurrentUserId;

	
	@Override
	public void onResume() {
		super.onResume();
		// check for network
		ConnectivityManager check = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nWifi = check.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo nMobile = check
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isWifi = nWifi.isConnected();
		boolean isMobile = nMobile.isConnected();
		if (!isWifi && !isMobile) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.connection_error)
					.setTitle(R.string.connection_error_title)
					.setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();

		}

		getActivity().setProgressBarIndeterminateVisibility(true);
		//If no argument is passed, show all restaurants
		if(getArguments()==null){
		queryParseAndSetAdapater();
		}
		//If an argument is passed, check its query code and query accordingly
		if(getArguments()!=null){
			String queryCode = (String)getArguments().get(QUERY_CODE);
			if(queryCode.equals(MY_RESTAURATNS)){
				queryParseForMyRestaurants();
			}
			if(queryCode.equals(SEARCH)){
				String query = getArguments().getString(QUERY);
				Log.i("QUERY", query);
				queryParseWithSearch(query);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Turn on action bar
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
			Log.i("TAG", currentUser.getUsername());
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
												queryParseAndSetAdapater();
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.main, menu);
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
				
				queryParseWithSearch(query);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				//mActionBarSearchQuery = newText.toLowerCase();
				Log.i("QUERY", "WTF");
				queryParseWithSearch(newText.toLowerCase());
				return false;
			}
		});

		searchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				getActivity().setProgressBarIndeterminateVisibility(true);
				queryParseAndSetAdapater();
				return false;
			}
		});

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
		case R.id.action_my_restaurants:
			queryParseForMyRestaurants();
			break;
		case R.id.action_all_restaurants:
			queryParseAndSetAdapater();
			break;
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
		args.putString(RestaurantFragment.EXTRA_RESTAURANT_ID,
				r.getObjectId());
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		RestaurantFragment raf = new RestaurantFragment();
		raf.setArguments(args);
		ft.replace(R.id.content_frame, raf);
		ft.addToBackStack("");
		ft.commit();
		// Log.i("NAME", r.getTitle());
		// Intent i = new Intent(getActivity(), RestaurantActivity.class);
		// i.putExtra(RestaurantFragment.EXTRA_RESTAURANT_ID,
		// r.getObjectId());
		// startActivityForResult(i, 1);
	}

	private void startRestaurantActivity() {
//		Intent i = new Intent(getActivity(), RestaurantActivity.class);
//		startActivityForResult(i, 0);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		RestaurantFragment raf = new RestaurantFragment();
		ft.replace(R.id.content_frame, raf);
		ft.addToBackStack("");
		ft.commit();
	}

	// Send user back to login screen
	private void navigateToLogin() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	// Query parse for all restaurant data
	private void queryParseAndSetAdapater() {
		if (ParseUser.getCurrentUser() != null) {

			// Query parse for data, store in array
			getActivity().setProgressBarIndeterminateVisibility(true);
			ParseQuery<Restaurant> query = new ParseQuery<Restaurant>(
					"Restaurant");
			query.whereExists(ParseConstants.KEY_RESTAURANT_TITLE);
			query.addAscendingOrder(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE);
			query.findInBackground(new FindCallback<Restaurant>() {
				@Override
				public void done(List<Restaurant> restaurants, ParseException e) {
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

	private void queryParseWithSearch(String search) {
		if (ParseUser.getCurrentUser() != null) {

			// Query parse for data, store in array
			// Compound query searches for TITLE or ADDRESS
			getActivity().setProgressBarIndeterminateVisibility(true);
			ParseQuery<Restaurant> queryTitle = new ParseQuery<Restaurant>(
					"Restaurant");
			queryTitle.whereContains(
					ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE,
					search);

			ParseQuery<Restaurant> queryAddress = new ParseQuery<Restaurant>(
					"Restaurant");
			queryAddress.whereContains(ParseConstants.KEY_ADDRESS_LOWER_CASE,
					search);

			List<ParseQuery<Restaurant>> queries = new ArrayList<ParseQuery<Restaurant>>();
			queries.add(queryTitle);
			queries.add(queryAddress);

			ParseQuery<Restaurant> mainQuery = ParseQuery.or(queries);
			mainQuery
					.addAscendingOrder(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE);
			mainQuery.findInBackground(new FindCallback<Restaurant>() {
				@Override
				public void done(List<Restaurant> restaurants, ParseException e) {
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

	private void queryParseForMyRestaurants() {
		if (ParseUser.getCurrentUser() != null) {

			// Query parse for data, store in array
			getActivity().setProgressBarIndeterminateVisibility(true);
			ParseQuery<Restaurant> query = new ParseQuery<Restaurant>(
					"Restaurant");
			query.whereEqualTo(ParseConstants.KEY_AUTHOR,
					ParseUser.getCurrentUser());
			query.addAscendingOrder(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE);
			query.findInBackground(new FindCallback<Restaurant>() {
				@Override
				public void done(List<Restaurant> restaurants, ParseException e) {
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
			queryParseAndSetAdapater();
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			// If a new post has been added, update
			// the list of posts
			queryParseAndSetAdapater();
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().setProgressBarIndeterminateVisibility(false);
	}

	// TODO Convert to fragments?

	// TODO on new instance, set arguments
}
