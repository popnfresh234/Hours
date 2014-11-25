package com.alex.hours;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMarkerClickListener,
		OnInfoWindowClickListener {

	private MapView mMapView;
	private GoogleMap mMap;
	private EditText mSearchField;
	private Button mSearchButton;
	private Button mDoneButton;
	private LocationManager mLocMan;
	private Marker mUserMarker;
	private Marker[] placeMarkers;
	private MarkerOptions[] places;
	private int mUserIcon;
	private final int MAX_PLACES = 20;

	public static final String NEW_RESTAURANT_MAP = "new_restaurant_map";

	private String mTitle;
	private String mAddress;
	private String mCity;
	private String mPhoneNumber;
	private String mPlaceId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MapsInitializer.initialize(getActivity());
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_map, container, false);

		// Gets the MapView from the XML layout and creates it

		mMapView = (MapView) v.findViewById(R.id.mapview);
		mMapView.onCreate(savedInstanceState);

		// Gets to GoogleMap from the MapView and does initialization stuff

		mUserIcon = R.drawable.yellow_point;
		placeMarkers = new Marker[MAX_PLACES];

		mSearchField = (EditText) v.findViewById(R.id.searchField);
		mSearchButton = (Button) v.findViewById(R.id.button_search);

		mLocMan = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		mSearchButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				try {
					String rawname = mSearchField.getText().toString();
					String name = rawname.replace(" ", "+");
					Location lastLoc = getLastKnownLocation();
					double lat = lastLoc.getLatitude();
					double lng = lastLoc.getLongitude();

					String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/"
							+ "json?location="
							+ lat
							+ ","
							+ lng
							+ "&radius=20000&sensor=true"
							+ "&types=food"
							+ "&name="
							+ name
							+ "&key=AIzaSyDlwnG0Kb5ZNLd_WQ-bEcdP9QII_Ti820I";

					new GetPlaces().execute(placesSearchStr);
				} catch (Exception e) {
					Log.i("Map Error", e.toString());
				}
			}
		});

		mDoneButton = (Button) v.findViewById(R.id.button_done);
		mDoneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Getting new information for existing restaurant
				if (getArguments() != null) {
					if (getArguments().getString(
							RestaurantFragment.EXTRA_RESTAURANT_ID) != null) {
						String restaurantId = (String) getArguments()
								.getString(
										RestaurantFragment.EXTRA_RESTAURANT_ID);
						Log.i("RestaurantID", restaurantId);

						Bundle args = new Bundle();
						args.putString(RestaurantFragment.EXTRA_RESTAURANT_ID,
								restaurantId);
						args.putString(RestaurantFragment.MAP_CODE,
								RestaurantFragment.MAP_CODE);
						args.putString(RestaurantFragment.CITY_FROM_MAP, mCity);
						args.putString(RestaurantFragment.ADDRESS_FROM_MAP,
								mAddress);
						args.putString(RestaurantFragment.NAME_FROM_MAP, mTitle);
						args.putString(RestaurantFragment.PHONE_FROM_MAP,
								mPhoneNumber);
						args.putString(RestaurantListFragment.QUERY_CODE,
								RestaurantFragment.MAP_CODE);
						RestaurantFragment restaurantFragment = new RestaurantFragment();
						restaurantFragment.setArguments(args);
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.popBackStack();
						fragmentManager
								.beginTransaction()
								.replace(R.id.content_frame, restaurantFragment)
								.commit();
					}
				}
				// Creating new restaurant
				if (getArguments() != null) {
					if (getArguments().getString(
							RestaurantListFragment.QUERY_CODE) != null) {
						Bundle args = new Bundle();

						args.putString(RestaurantFragment.MAP_CODE,
								RestaurantFragment.MAP_CODE);
						args.putString(RestaurantFragment.CITY_FROM_MAP, mCity);
						args.putString(RestaurantFragment.ADDRESS_FROM_MAP,
								mAddress);
						args.putString(RestaurantFragment.NAME_FROM_MAP, mTitle);
						args.putString(RestaurantFragment.PHONE_FROM_MAP,
								mPhoneNumber);

						Log.i("MAP FRAGMENT",
								getArguments().getString(
										RestaurantListFragment.QUERY_CODE));
						if (getArguments().getString(
								RestaurantListFragment.QUERY_CODE).equals(
								RestaurantFragment.NEW_RESTAURANT_FROM_HOME)) {
							args.putString(
									RestaurantListFragment.QUERY_CODE,
									RestaurantFragment.NEW_RESTAURANT_FROM_HOME_AND_MAP);
						}
						if (getArguments().getString(
								RestaurantListFragment.QUERY_CODE).equals(
								RestaurantFragment.NEW_RESTAURANT_FROM_LIST)) {
							args.putString(
									RestaurantListFragment.QUERY_CODE,
									RestaurantFragment.NEW_RESTAURANT_FROM_LIST_AND_MAP);
						}
						// args.putString(
						// RestaurantListFragment.QUERY_CODE,
						// getArguments().getString(
						// RestaurantListFragment.QUERY_CODE));
						args.putString(NEW_RESTAURANT_MAP, NEW_RESTAURANT_MAP);
						RestaurantFragment restaurantFragment = new RestaurantFragment();
						restaurantFragment.setArguments(args);
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.popBackStack();
						fragmentManager
								.beginTransaction()
								.replace(R.id.content_frame, restaurantFragment)
								.commit();
					}
				}

			}
		});

		if (getArguments() != null) {
			if (getArguments()
					.getString(RestaurantFragment.EXTRA_RESTAURANT_ID) != null) {
				String restaurantId = (String) getArguments().getString(
						RestaurantFragment.EXTRA_RESTAURANT_ID);
				Log.i("RestaurantID", restaurantId);
			}
		}

		return v;
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		// In case Google Play services has since become available.
		setUpMapIfNeeded();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.i("click", "click");

		String detailsSearchStr = "https://maps.googleapis.com/maps/api/place/details/json?placeid="
				+ mPlaceId + "&key=AIzaSyDlwnG0Kb5ZNLd_WQ-bEcdP9QII_Ti820I";

		new GetDetails().execute(detailsSearchStr);

		LatLng location = marker.getPosition();

		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(getActivity(), Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(location.latitude,
					location.longitude, 1);
			String address = addresses.get(0).getAddressLine(0);
			String city = addresses.get(0).getLocality();
			// String country = addresses.get(0).getAddressLine(2);

			Toast.makeText(getActivity(), address, Toast.LENGTH_SHORT).show();
			Log.i("Address", marker.getSnippet().toString());
			Log.i("Name", marker.getTitle().toString());
			Log.i("City", city);
			mAddress = marker.getSnippet().toString();
			mTitle = marker.getTitle().toString();
			mCity = city;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		Log.i("click", "click");

	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = mMapView.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// setUpMap();
				updatePlaces();
			}
		}

	}

	private void updatePlaces() {

		// Set Listeners
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);

		// update location
		mLocMan = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		try {
			Location lastLoc = getLastKnownLocation();
			double lat = lastLoc.getLatitude();
			double lng = lastLoc.getLongitude();
			LatLng lastLatLng = new LatLng(lat, lng);
			if (mUserMarker != null) {
				mUserMarker.remove();
			}

			mUserMarker = mMap.addMarker(new MarkerOptions()
					.position(lastLatLng).title("You Are Here")
					.icon(BitmapDescriptorFactory.fromResource(mUserIcon))
					.snippet("Your last recorded location"));

			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,
					lng), 11));
		} catch (Exception e) {
			Log.i(" map error", e.toString());

		}

	}

	private Location getLastKnownLocation() {
		List<String> providers = mLocMan.getProviders(true);
		Location bestLocation = null;
		for (String provider : providers) {
			Location l = mLocMan.getLastKnownLocation(provider);

			if (l == null) {
				continue;
			}
			if (bestLocation == null
					|| l.getAccuracy() < bestLocation.getAccuracy()) {

				bestLocation = l;
			}
		}
		if (bestLocation == null) {
			return null;
		}
		return bestLocation;
	}

	private class GetPlaces extends AsyncTask<String, Void, String> {
		// fetch and parse place data
		@Override
		protected String doInBackground(String... placesURL) {
			// fetch places
			StringBuilder placesBuilder = new StringBuilder();
			for (String placeSearchUrl : placesURL) {
				// execute search
				HttpClient placesClient = new DefaultHttpClient();
				try {
					HttpGet placesGet = new HttpGet(placeSearchUrl);
					HttpResponse placesResponse = placesClient
							.execute(placesGet);
					StatusLine placeSearchStatus = placesResponse
							.getStatusLine();
					if (placeSearchStatus.getStatusCode() == 200) {
						// we have an OK response
						HttpEntity placesEntity = placesResponse.getEntity();
						InputStream placesContent = placesEntity.getContent();
						InputStreamReader placesInput = new InputStreamReader(
								placesContent);
						BufferedReader placesReader = new BufferedReader(
								placesInput);

						String lineIn;
						while ((lineIn = placesReader.readLine()) != null) {
							placesBuilder.append(lineIn);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Log.i("test", placesBuilder.toString());
			return placesBuilder.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			// parse place data returned from Google Places
			if (placeMarkers != null) {
				for (int pm = 0; pm < placeMarkers.length; pm++) {
					if (placeMarkers[pm] != null) {
						placeMarkers[pm].remove();
					}
				}
			}

			try {
				JSONObject resultObject = new JSONObject(result);
				JSONArray placesArray = resultObject.getJSONArray("results");
				places = new MarkerOptions[placesArray.length()];

				// loop through places
				for (int p = 0; p < placesArray.length(); p++) {
					// parse each place
					boolean missingValue = false;
					LatLng placeLL = null;
					String placeName = "";
					String vicinity = "";
					int currIcon = mUserIcon;
					try {
						// attempt to retrieve place data values
						missingValue = false;
						JSONObject placeObject = placesArray.getJSONObject(p);
						JSONObject loc = placeObject.getJSONObject("geometry")
								.getJSONObject("location");

						placeLL = new LatLng(Double.valueOf(loc
								.getString("lat")), Double.valueOf(loc
								.getString("lng")));

						JSONArray types = placeObject.getJSONArray("types");

						for (int t = 0; t < types.length(); t++) {
							// what type is it
							String thisType = types.get(t).toString();
							if (thisType.contains("food")) {
								currIcon = mUserIcon;
								break;
							} else if (thisType.contains("bar")) {
								currIcon = mUserIcon;
								break;
							} else if (thisType.contains("store")) {
								currIcon = mUserIcon;
								break;
							}
						}
						vicinity = placeObject.getString("vicinity");
						placeName = placeObject.getString("name");
						mPlaceId = placeObject.getString("place_id");

					} catch (JSONException jse) {
						missingValue = true;
						jse.printStackTrace();
					}
					if (missingValue) {
						places[p] = null;
					} else {
						places[p] = new MarkerOptions()
								.position(placeLL)
								.title(placeName)
								.icon(BitmapDescriptorFactory
										.fromResource(currIcon))
								.snippet(vicinity);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (places != null && placeMarkers != null) {
				for (int p = 0; p < places.length && p < placeMarkers.length; p++) {
					// will be null if a value was missing
					if (places[p] != null)
						placeMarkers[p] = mMap.addMarker(places[p]);
				}
			}
		}
	}

	private class GetDetails extends AsyncTask<String, Void, String> {
		// fetch and parse place data
		@Override
		protected String doInBackground(String... detailsURL) {
			// fetch places
			StringBuilder detailsBuilder = new StringBuilder();
			for (String detailsSearchUrl : detailsURL) {
				// execute search
				HttpClient placesClient = new DefaultHttpClient();
				try {
					HttpGet placesGet = new HttpGet(detailsSearchUrl);
					HttpResponse placesResponse = placesClient
							.execute(placesGet);
					StatusLine placeSearchStatus = placesResponse
							.getStatusLine();
					if (placeSearchStatus.getStatusCode() == 200) {
						// we have an OK response
						HttpEntity placesEntity = placesResponse.getEntity();
						InputStream placesContent = placesEntity.getContent();
						InputStreamReader placesInput = new InputStreamReader(
								placesContent);
						BufferedReader placesReader = new BufferedReader(
								placesInput);

						String lineIn;
						while ((lineIn = placesReader.readLine()) != null) {
							detailsBuilder.append(lineIn);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return detailsBuilder.toString();
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				JSONObject resultObject = new JSONObject(result);
				JSONObject resulty = resultObject.getJSONObject("result");
				Log.i("detail attempt", resultObject.toString());

				Log.i("poooop", resulty.toString());
				Log.i("peeeee", resulty.getString("formatted_phone_number"));
				mPhoneNumber = resulty.getString("formatted_phone_number");

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
