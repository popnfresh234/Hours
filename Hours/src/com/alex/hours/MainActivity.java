package com.alex.hours;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.alex.hours.utilities.DrawerItemCustomAdapter;
import com.alex.hours.utilities.ObjectDrawerItem;

public class MainActivity extends FragmentActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private boolean mIsDrawerLocked = false;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	// private String[] mTitles;

	@Override
	protected void onResume() {
		super.onResume();

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
		if (((ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams()).leftMargin == (int) getResources()
				.getDimension(R.dimen.drawer_size)) {
			Log.i("LOCK", "LOCK");
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN,
					mDrawerList);
			mDrawerLayout.setFocusableInTouchMode(false);
			mDrawerLayout.setScrimColor(Color.TRANSPARENT);
			mIsDrawerLocked = true;
			getActionBar().setDisplayHomeAsUpEnabled(false);
			getActionBar().setHomeButtonEnabled(false);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("onCreate", "called");
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		// Create drawer items for the custom adapter
		ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[8];

		drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_av_home,
				getString(R.string.nav_drawer_home));
		drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_my_favorites,
				getString(R.string.nav_drawer_my_favorites));
		drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_all,
				getString(R.string.nav_drawer_all_restaurants));
		drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_my_restaurants,
				getString(R.string.nav_drawer_my_restaurants));
		drawerItem[4] = new ObjectDrawerItem(R.drawable.ic_added_today,
				getString(R.string.nav_drawer_added_today));
		drawerItem[5] = new ObjectDrawerItem(R.drawable.ic_added_week,
				getString(R.string.nav_drawer_added_this_week));
		drawerItem[6] = new ObjectDrawerItem(R.drawable.ic_updated,
				getString(R.string.nav_drawer_recently_updated));
		drawerItem[7] = new ObjectDrawerItem(R.drawable.ic_city,
				getString(R.string.nav_drawer_city));

		// mTitle = mDrawerTitle = getTitle();
		// mTitles = getResources().getStringArray(R.array.titles_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		// mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.drawer_list_item, mTitles));
		// Set custom adapter instead of standard adapter for icons
		DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this,
				R.layout.list_item_drawer, drawerItem);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_navigation_drawer, /*
										 * nav drawer image to replace 'Up'
										 * caret
										 */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};
		if (!mIsDrawerLocked) {
			mDrawerLayout.setDrawerListener(mDrawerToggle);
			if (savedInstanceState == null) {
				selectItem(0);
			}
		}
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons

		return super.onOptionsItemSelected(item);

	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		Log.i("Item selected", "item" + position);
		// update the main content by replacing fragments
		FragmentManager fragmentManager;
		Bundle args = new Bundle();
		switch (position) {
		case 0:
			MainActivityFragment mainActivity = new MainActivityFragment();
			fragmentManager = getSupportFragmentManager();

			for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
				fragmentManager.popBackStack();
			}
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, mainActivity).commit();
			break;
		case 1:

			args.putString(RestaurantListFragment.QUERY_CODE,
					RestaurantListFragment.FAVORITES);
			RestaurantListFragment favorites = new RestaurantListFragment();
			favorites.setArguments(args);
			fragmentManager = getSupportFragmentManager();

			fragmentManager.popBackStack();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, favorites)
					.addToBackStack(null).commit();

			break;
		case 2:

			args.putString(RestaurantListFragment.QUERY_CODE,
					RestaurantListFragment.ALL_RESTAURATNS);
			RestaurantListFragment allRestaurants = new RestaurantListFragment();
			allRestaurants.setArguments(args);
			fragmentManager = getSupportFragmentManager();
			if (fragmentManager.getBackStackEntryCount() <= 1) {
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, allRestaurants)
						.addToBackStack(null).commit();
			}
			if (fragmentManager.getBackStackEntryCount() >= 2) {
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, allRestaurants).commit();
			}
			break;
		case 3:

			args.putString(RestaurantListFragment.QUERY_CODE,
					RestaurantListFragment.MY_RESTAURATNS);
			RestaurantListFragment myRestaurants = new RestaurantListFragment();
			myRestaurants.setArguments(args);
			fragmentManager = getSupportFragmentManager();
			if (fragmentManager.getBackStackEntryCount() <= 1) {
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, myRestaurants)
						.addToBackStack(null).commit();
			}
			if (fragmentManager.getBackStackEntryCount() >= 2) {
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, myRestaurants).commit();
			}
			break;

		case 4:

			args.putString(RestaurantListFragment.QUERY_CODE,
					RestaurantListFragment.RECENT_RESTAURANTS_ONE_DAY);
			args.putInt(RestaurantListFragment.DATE_INCREMENT, -1);
			args.putString(RestaurantListFragment.CRITERIA,
					RestaurantListFragment.CRITERIA_CREATED_AT);
			RestaurantListFragment recentRestaurantsOneDay = new RestaurantListFragment();
			recentRestaurantsOneDay.setArguments(args);
			fragmentManager = getSupportFragmentManager();
			if (fragmentManager.getBackStackEntryCount() <= 1) {
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, recentRestaurantsOneDay)
						.addToBackStack(null).commit();
			}
			if (fragmentManager.getBackStackEntryCount() >= 2) {
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, recentRestaurantsOneDay)
						.commit();
			}
			break;

		case 5:

			args.putString(RestaurantListFragment.QUERY_CODE,
					RestaurantListFragment.RECENT_RESTAURANTS_ONE_WEEK);
			args.putInt(RestaurantListFragment.DATE_INCREMENT, -7);
			args.putString(RestaurantListFragment.CRITERIA,
					RestaurantListFragment.CRITERIA_CREATED_AT);
			RestaurantListFragment recentRestaurantsOneWeek = new RestaurantListFragment();
			recentRestaurantsOneWeek.setArguments(args);
			fragmentManager = getSupportFragmentManager();
			if (fragmentManager.getBackStackEntryCount() <= 1) {
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, recentRestaurantsOneWeek)
						.addToBackStack(null).commit();
			}
			if (fragmentManager.getBackStackEntryCount() >= 2) {
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, recentRestaurantsOneWeek)
						.commit();
			}
			break;

		case 6:

			args.putString(RestaurantListFragment.QUERY_CODE,
					RestaurantListFragment.RECENT_UPDATE);
			args.putInt(RestaurantListFragment.DATE_INCREMENT, -1);
			args.putString(RestaurantListFragment.CRITERIA,
					RestaurantListFragment.CRITERIA_UPDATED_AT);
			RestaurantListFragment recentUpdate = new RestaurantListFragment();
			recentUpdate.setArguments(args);
			fragmentManager = getSupportFragmentManager();
			if (fragmentManager.getBackStackEntryCount() <= 1) {
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, recentUpdate)
						.addToBackStack(null).commit();
			}
			if (fragmentManager.getBackStackEntryCount() >= 2) {
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, recentUpdate).commit();
			}
			break;

		case 7:

			CityFragment cityFragment = new CityFragment();
			fragmentManager = getSupportFragmentManager();

			Log.i("STACKMAIN:",
					String.valueOf(fragmentManager.getBackStackEntryCount()));
			if (fragmentManager.getBackStackEntryCount() >= 2) {
				fragmentManager.popBackStack();
			}
			// fragmentManager.popBackStack();
			if (fragmentManager.getBackStackEntryCount() < 2) {
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, cityFragment)
						.addToBackStack(null).commit();
			}
			

			break;

		}

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, false);
		// setTitle(mPlanetTitles[position]);
		if (!mIsDrawerLocked) {
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.

		mDrawerToggle.syncState();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (!mIsDrawerLocked) {
			if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			} else {
				super.onBackPressed();
			}
		}
		if (mIsDrawerLocked) {
			super.onBackPressed();
		}
	}

}
