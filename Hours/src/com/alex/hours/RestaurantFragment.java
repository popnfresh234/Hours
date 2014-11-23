package com.alex.hours;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alex.hours.utilities.FileHelper;
import com.alex.hours.utilities.ParseConstants;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class RestaurantFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener, OnFocusChangeListener {

	public static final String NEW_RESTAURANT_FROM_LIST = "new_restaurant_from_list";
	public static final String NEW_RESTAURANT_FROM_HOME = "new_restaurant_from_home";
	public static final String NEW_RESTAURANT_FROM_MAP = "new_restaurant_from_map";
	public static final String ADDRESS_FROM_MAP = "address_from_map";
	public static final String CITY_FROM_MAP = "city_from_map";
	public static final String NAME_FROM_MAP = "name_from_map";
	public static final String MAP_CODE = "com.alex.hours.extra.map.code";
	public static final String EXTRA_RESTAURANT_ID = "com.alex.hours.extra.restaurant.id";
	private static final String mFileType = ParseConstants.KEY_FILE_TYPE;
	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int PICK_PHOTO_REQUEST = 1;
	public static final int MEDIA_TYPE_IMAGE = 2;
	public static final String MY_RESTAURNT = "myrestaurant";
	public static final String ALL_RESTAURANTS = "allrestauratns";

	private ParseACL parseACL;

	private Restaurant mRestaurant;
	protected Uri mMediaUri;
	// Declare views
	private EditText mTitleField;
	private EditText mAddressField;
	private ImageButton mMapButton;
	private EditText mCityField;
	private EditText mPhoneField;
	private ImageButton mPhoneButton;
	private CheckBox mSunday;
	private CheckBox mMonday;
	private CheckBox mTuesday;
	private CheckBox mWednesday;
	private CheckBox mThursday;
	private CheckBox mFriday;
	private CheckBox mSaturday;
	private Button mMasterOpenButton;
	private Button mMasterCloseButton;
	private Button mSundayOpenButton;
	private Button mSundayCloseButton;
	private Button mMondayOpenButton;
	private Button mMondayCloseButton;
	private Button mTuesdayOpenButton;
	private Button mTuesdayCloseButton;
	private Button mWednesdayOpenButton;
	private Button mWednesdayCloseButton;
	private Button mThursdayOpenButton;
	private Button mThursdayCloseButton;
	private Button mFridayOpenButton;
	private Button mFridayCloseButton;
	private Button mSaturdayOpenButton;
	private Button mSaturdayCloseButton;
	private EditText mNotesField;
	private Button mTakePictureButton;
	private ImageView mRestaurantImageView;
	private Button mSaveButton;
	private Button mCancelButton;

	private int mResultCode;
	private int mRequestCode;
	private Intent mData;
	private boolean mFromCamera = false;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			Log.i("args", getArguments().toString());
		}

		// setup ACL
		parseACL = new ParseACL(ParseUser.getCurrentUser());
		parseACL.setPublicReadAccess(true);
		parseACL.setPublicWriteAccess(false);
		parseACL.setWriteAccess("e7So6F4ytk", true);
		parseACL.setWriteAccess("45LT0GnGVU", true);

		// get restaurant ID from arguments

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(getActivity());
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_restaurant, container,
				false);

		// Set the home button as enabled so caret appears
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		Log.i("OnCreateView", "called");
		// Setup fields
		mTitleField = (EditText) v.findViewById(R.id.restaurant_title);
		mTitleField.setOnFocusChangeListener(this);

		mAddressField = (EditText) v.findViewById(R.id.restaurant_address);
		mAddressField.setOnFocusChangeListener(this);
		mMapButton = (ImageButton) v.findViewById(R.id.map_button);
		mMapButton.setOnClickListener(this);

		mCityField = (EditText) v.findViewById(R.id.restaurant_city);
		mCityField.setOnFocusChangeListener(this);

		mPhoneField = (EditText) v.findViewById(R.id.restaurant_phone);
		mPhoneButton = (ImageButton) v.findViewById(R.id.phone_button);
		mPhoneButton.setOnClickListener(this);

		mSunday = (CheckBox) v.findViewById(R.id.sunday_check);
		mSunday.setOnCheckedChangeListener(this);

		mMonday = (CheckBox) v.findViewById(R.id.monday_check);
		mMonday.setOnCheckedChangeListener(this);

		mTuesday = (CheckBox) v.findViewById(R.id.tuesday_check);
		mTuesday.setOnCheckedChangeListener(this);

		mWednesday = (CheckBox) v.findViewById(R.id.wednesday_check);
		mWednesday.setOnCheckedChangeListener(this);

		mThursday = (CheckBox) v.findViewById(R.id.thursday_check);
		mThursday.setOnCheckedChangeListener(this);

		mFriday = (CheckBox) v.findViewById(R.id.friday_check);
		mFriday.setOnCheckedChangeListener(this);

		mSaturday = (CheckBox) v.findViewById(R.id.saturday_check);
		mSaturday.setOnCheckedChangeListener(this);

		mMasterOpenButton = (Button) v.findViewById(R.id.master_hours_open);
		mMasterOpenButton.setOnClickListener(this);
		mMasterCloseButton = (Button) v.findViewById(R.id.master_hours_close);
		mMasterCloseButton.setOnClickListener(this);

		mSundayOpenButton = (Button) v.findViewById(R.id.sunday_hours_open);
		mSundayOpenButton.setOnClickListener(this);
		mSundayCloseButton = (Button) v.findViewById(R.id.sunday_hours_close);
		mSundayCloseButton.setOnClickListener(this);

		mMondayOpenButton = (Button) v.findViewById(R.id.monday_hours_open);
		mMondayOpenButton.setOnClickListener(this);
		mMondayCloseButton = (Button) v.findViewById(R.id.monday_hours_close);
		mMondayCloseButton.setOnClickListener(this);

		mTuesdayOpenButton = (Button) v.findViewById(R.id.tuesday_hours_open);
		mTuesdayOpenButton.setOnClickListener(this);
		mTuesdayCloseButton = (Button) v.findViewById(R.id.tuesday_hours_close);
		mTuesdayCloseButton.setOnClickListener(this);

		mWednesdayOpenButton = (Button) v
				.findViewById(R.id.wednesday_hours_open);
		mWednesdayOpenButton.setOnClickListener(this);
		mWednesdayCloseButton = (Button) v
				.findViewById(R.id.wednesday_hours_close);
		mWednesdayCloseButton.setOnClickListener(this);

		mThursdayOpenButton = (Button) v.findViewById(R.id.thursday_hours_open);
		mThursdayOpenButton.setOnClickListener(this);
		mThursdayCloseButton = (Button) v
				.findViewById(R.id.thursday_hours_close);
		mThursdayCloseButton.setOnClickListener(this);

		mFridayOpenButton = (Button) v.findViewById(R.id.friday_hours_open);
		mFridayOpenButton.setOnClickListener(this);
		mFridayCloseButton = (Button) v.findViewById(R.id.friday_hours_close);
		mFridayCloseButton.setOnClickListener(this);

		mSaturdayOpenButton = (Button) v.findViewById(R.id.saturday_hours_open);
		mSaturdayOpenButton.setOnClickListener(this);
		mSaturdayCloseButton = (Button) v
				.findViewById(R.id.saturday_hours_close);
		mSaturdayCloseButton.setOnClickListener(this);

		mNotesField = (EditText) v.findViewById(R.id.restaurant_notes);
		mNotesField.setOnFocusChangeListener(this);

		mTakePictureButton = (Button) v.findViewById(R.id.takePictureButton);
		mTakePictureButton.setOnClickListener(this);

		mRestaurantImageView = (ImageView) v
				.findViewById(R.id.restaurantImageView);

		mSaveButton = (Button) v.findViewById(R.id.saveButton);
		mSaveButton.setOnClickListener(this);

		mCancelButton = (Button) v.findViewById(R.id.cancelButton);
		mCancelButton.setOnClickListener(this);

		// Method includes check to see if we're editing a restaurant or
		// creating a new one, only loads fields if we're editing

		return v;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("onResume", "called");

		// Load restaurant information if we didn't come from camera intent
		if (!mFromCamera) {
			Log.i("OnResuemLoad", "loading Rest");
			loadRestaurant();
		}

		if (getArguments() != null) {
			if (getArguments().getString(MapFragment.NEW_RESTAURANT_MAP) != null) {
				if (getArguments().getString(MapFragment.NEW_RESTAURANT_MAP)
						.equals(MapFragment.NEW_RESTAURANT_MAP)) {
					mTitleField
							.setText(getArguments().getString(NAME_FROM_MAP));
					mAddressField.setText(getArguments().getString(
							ADDRESS_FROM_MAP));
					mCityField.setText(getArguments().getString(CITY_FROM_MAP));
				}
			}
		}
	}

	// Handle Results from Camera
	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// If user pressed OK from either take or chooe photo request
		Log.i("onResults", "onResults");
		mFromCamera = true;
		mResultCode = resultCode;
		mRequestCode = requestCode;
		mData = data;

		// Load restaurant with new picture
		// Activity results must be carried out within parseobjects save
		// callback or it will create a new restaurant
		// becausethe data hasn't been loaded yet so mRestaurant == null
		if (getArguments() != null) {
			if (getArguments().getString(EXTRA_RESTAURANT_ID) != null) {
				String restaurantId = (String) getArguments().getString(
						EXTRA_RESTAURANT_ID);
				// Fetch restaurant based on ID
				// Disable buttons so user can't save or take a picture in the
				// middle of fetching the restaurant
				mSaveButton.setEnabled(false);
				mCancelButton.setEnabled(false);
				mTakePictureButton.setEnabled(false);
				getActivity().setProgressBarIndeterminateVisibility(true);
				// query parse for the restaurant based on the ID passed in
				ParseQuery<Restaurant> query = ParseQuery
						.getQuery("Restaurant");
				query.getInBackground(restaurantId,
						new GetCallback<Restaurant>() {
							public void done(Restaurant restaurant,
									ParseException e) {
								if (e == null) {
									// Re-enable buttons

									mRestaurant = restaurant;
									if (mRestaurant != null) {
										Log.i("Restaurant",
												mRestaurant.toString());
									}
									mSaveButton.setEnabled(true);
									mCancelButton.setEnabled(true);
									mTakePictureButton.setEnabled(true);
									// If restaurant contains no image, turn off
									// progress
									// bar here, otherwise turn off when image
									// is done
									// loading in loadFields
									if (mRestaurant.getImage() == null) {
										if (getActivity() != null) {
											getActivity()
													.setProgressBarIndeterminateVisibility(
															false);
										}
									}

									// set fields to restaurant object data
									loadFields();

									// Deal with results from camera
									if (mResultCode == getActivity().RESULT_OK) {
										// If choosing a photo, get it's URI
										// from the data
										if (mRequestCode == PICK_PHOTO_REQUEST) {
											if (mData == null) {
												Log.i("DATA", "DATA ERROR");
											} else {
												mMediaUri = mData.getData();
											}
										}
										// If taking a photo,do something funky
										// with media scanner
										if (mRequestCode == TAKE_PHOTO_REQUEST) {
											Intent mediaScanIntent = new Intent(
													Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
											mediaScanIntent.setData(mMediaUri);
											getActivity().sendBroadcast(
													mediaScanIntent);
										}
										// TODO fix this
										resizeImageForUpload();

									}

									else if (mResultCode != getActivity().RESULT_CANCELED) {
										Toast.makeText(getActivity(), "Error",
												Toast.LENGTH_LONG).show();
									}
								} else {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									builder.setMessage(e.getMessage())
											.setTitle(
													R.string.general_error_title)
											.setPositiveButton(
													android.R.string.ok, null);
									AlertDialog dialog = builder.create();
									dialog.show();

								}
							}
						});
			}
			// Creating a new restaurant
			else {
				if (resultCode == getActivity().RESULT_OK) {
					// If choosing a photo, get it's URI
					// from the data
					if (requestCode == PICK_PHOTO_REQUEST) {
						if (data == null) {
							Log.i("DATA", "DATA ERROR");
						} else {
							mMediaUri = data.getData();
						}
					}
					// If taking a photo,do something funky
					// with media scanner
					if (requestCode == TAKE_PHOTO_REQUEST) {
						Intent mediaScanIntent = new Intent(
								Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
						mediaScanIntent.setData(mMediaUri);
						getActivity().sendBroadcast(mediaScanIntent);
					}
					// TODO fix this
					resizeImageForUpload();
				}
			}
		}

	}

	protected void resizeImageForUpload() {
		Log.i("RESIZE", "resize");
		byte[] fileBytes = FileHelper.getByteArrayFromFile(getActivity(),
				mMediaUri);

		if (fileBytes == null) {
			Log.i("Doom", "doom");
		} else {

			fileBytes = FileHelper.reduceImageForUpload(fileBytes);

			String fileName = FileHelper.getFileName(getActivity(), mMediaUri,
					mFileType);
			ParseFile file = new ParseFile(fileName, fileBytes);

			// If the restaurant already exists, replace it's image with the new
			// one

			if (mRestaurant != null) {
				mRestaurant.setImage(file);
				getActivity().setProgressBarIndeterminateVisibility(true);
				mSaveButton.setEnabled(false);
				mCancelButton.setEnabled(false);
				mTakePictureButton.setEnabled(false);
				updateFields();
				mRestaurant.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {
							mSaveButton.setEnabled(true);
							mCancelButton.setEnabled(true);
							mTakePictureButton.setEnabled(true);
							loadFields();
						} else {
							Log.i("SaveError", "Save Error");
						}
					}
				});
			} else {
				Log.i("Creating New Restaurant", "damn");
				// If the restaurant doesn't exist, create a new one and set
				// upload the chosen or taken image

				mRestaurant = createRestaurant();
				mRestaurant.setImage(file);
				getActivity().setProgressBarIndeterminateVisibility(true);
				mSaveButton.setEnabled(false);
				mCancelButton.setEnabled(false);
				mTakePictureButton.setEnabled(false);
				mRestaurant.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {
							mSaveButton.setEnabled(true);
							mCancelButton.setEnabled(true);
							mTakePictureButton.setEnabled(true);
							loadFields();
						} else {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setMessage(e.getMessage())
									.setTitle(R.string.general_error_title)
									.setPositiveButton(android.R.string.ok,
											null);
							AlertDialog dialog = builder.create();
							dialog.show();
						}
					}
				});
			}
		}
	}

	// Handle clicks on the various buttons in the fragment
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

		case R.id.map_button:
			// if(mAddressField.getText().toString().equals("")){
			// Toast.makeText(getActivity(), R.string.toast_no_address,
			// Toast.LENGTH_SHORT).show();
			// }
			// else{
			// String address = mAddressField.getText().toString();
			// String map = "http://maps.google.com/maps?q="+ address;
			// Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
			// startActivity(i);
			// }

			// Start the map fragment
			Bundle args = new Bundle();
			if (getArguments() != null) {
				if (getArguments().getString(EXTRA_RESTAURANT_ID) != null) {
					String restaurantId = (String) getArguments().getString(
							EXTRA_RESTAURANT_ID);
					args.putString(EXTRA_RESTAURANT_ID, restaurantId);
				}
			}
			if (getArguments() != null) {
				if (getArguments().getString(RestaurantListFragment.QUERY_CODE) != null) {
					if (getArguments().getString(
							RestaurantListFragment.QUERY_CODE).equals(
							NEW_RESTAURANT_FROM_LIST)) {
						args.putString(RestaurantListFragment.QUERY_CODE,
								NEW_RESTAURANT_FROM_LIST);
					}
				}
			}
			if (getArguments() != null) {
				if (getArguments().getString(RestaurantListFragment.QUERY_CODE) != null) {
					if (getArguments().getString(
							RestaurantListFragment.QUERY_CODE).equals(
							NEW_RESTAURANT_FROM_HOME)) {
						args.putString(RestaurantListFragment.QUERY_CODE,
								NEW_RESTAURANT_FROM_HOME);
					}
				}
			}
			MapFragment mapFragment = new MapFragment();
			mapFragment.setArguments(args);
			FragmentManager fragmentManager;
			fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().addToBackStack(null)
					.replace(R.id.content_frame, mapFragment).commit();

			break;

		case R.id.phone_button:
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"
					+ mPhoneField.getText().toString()));
			startActivity(callIntent);
			break;

		case R.id.sunday_hours_open:
			Log.i("Test", "Test");
			TimePickerDialog dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mSunday.setChecked(true);
							mSundayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 7, 0, true);
			dialog.show();
			break;
		case R.id.sunday_hours_close:
			Log.i("Test", "Test");
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mSunday.setChecked(true);
							mSundayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 21, 0, true);
			dialog.show();
			break;

		case R.id.monday_hours_open:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mMonday.setChecked(true);
							mMondayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 7, 0, true);
			dialog.show();
			break;

		case R.id.monday_hours_close:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mMonday.setChecked(true);
							mMondayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 21, 0, true);
			dialog.show();
			break;

		case R.id.tuesday_hours_open:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mTuesday.setChecked(true);
							mTuesdayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 7, 0, true);
			dialog.show();
			break;

		case R.id.tuesday_hours_close:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mTuesday.setChecked(true);
							mTuesdayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 21, 0, true);
			dialog.show();
			break;

		case R.id.wednesday_hours_open:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mWednesday.setChecked(true);
							mWednesdayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 7, 0, true);
			dialog.show();
			break;

		case R.id.wednesday_hours_close:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mWednesday.setChecked(true);
							mWednesdayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 21, 0, true);
			dialog.show();
			break;
		case R.id.thursday_hours_open:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mThursday.setChecked(true);
							mThursdayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 7, 0, true);
			dialog.show();
			break;

		case R.id.thursday_hours_close:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mThursday.setChecked(true);
							mThursdayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 21, 0, true);
			dialog.show();
			break;
		case R.id.friday_hours_open:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mThursday.setChecked(true);
							mFridayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 7, 0, true);
			dialog.show();
			break;

		case R.id.friday_hours_close:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mFriday.setChecked(true);
							mFridayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 21, 0, true);
			dialog.show();
			break;
		case R.id.saturday_hours_open:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mSaturday.setChecked(true);
							mSaturdayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 7, 0, true);
			dialog.show();
			break;

		case R.id.saturday_hours_close:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mSaturday.setChecked(true);
							mSaturdayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 21, 0, true);
			dialog.show();
			break;
		case R.id.master_hours_open:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mSunday.setChecked(true);
							mMonday.setChecked(true);
							mTuesday.setChecked(true);
							mWednesday.setChecked(true);
							mThursday.setChecked(true);
							mFriday.setChecked(true);
							mSaturday.setChecked(true);

							mSundayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mMondayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mTuesdayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mWednesdayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mThursdayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mFridayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mSaturdayOpenButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 7, 0, true);
			dialog.show();
			break;

		case R.id.master_hours_close:
			dialog = new TimePickerDialog(getActivity(),
					new OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mSundayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mMondayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mTuesdayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mWednesdayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mThursdayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mFridayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));
							mSaturdayCloseButton.setText(new StringBuilder()
									.append(padding_str(hourOfDay)).append(":")
									.append(padding_str(minute)));

						}
					}, 21, 0, true);
			dialog.show();
			break;

		case R.id.saveButton:

			mSaveButton.setEnabled(false);
			mCancelButton.setEnabled(false);
			mTakePictureButton.setEnabled(false);

			// If mRestaurant == null we're creating a new restaurant
			if (mRestaurant == null) {
				mRestaurant = createRestaurant();
				getActivity().setProgressBarIndeterminateVisibility(true);

				mRestaurant.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (getActivity() != null) {
							getActivity()
									.setProgressBarIndeterminateVisibility(
											false);
						}
						if (e == null) {
							mSaveButton.setEnabled(true);
							mCancelButton.setEnabled(true);
							mTakePictureButton.setEnabled(true);
							// check to make sure activity exists still, protect
							// against backing out of app
							if (getActivity() != null) {
								getActivity().setResult(Activity.RESULT_OK);

								// Check if a query code was passed in, and and
								// if so what it was
								if (getArguments() != null) {
									String queryCode = getArguments()
											.getString(
													RestaurantListFragment.QUERY_CODE);

									// If the request came from the home
									// fragment, pop the backstack and create
									// all restaurant view fragment
									if (queryCode
											.equals(NEW_RESTAURANT_FROM_HOME)) {
										FragmentManager fm = getFragmentManager();
										fm.popBackStack();
										RestaurantListFragment allRestaurants = new RestaurantListFragment();
										fm.beginTransaction()
												.replace(R.id.content_frame,
														allRestaurants)
												.addToBackStack(null).commit();
									} else {
										// If the request didn't come from the
										// homepage, it came from restaurant
										// list activity
										// remove current fragment to return to
										// list
										removeFragment();
									}
								}

							}
						}

					}
				});
			}
			// If mRestaurant is not null, then we're updating an old listing
			else {

				// When saving check if current user is author and therefore has
				// write access, or is one of the listed administrators
				if (ParseUser
						.getCurrentUser()
						.getObjectId()
						.toString()
						.equals(mRestaurant.getAuthor().getObjectId()
								.toString())
						|| ParseUser.getCurrentUser().getObjectId().toString()
								.equals("e7So6F4ytk")
						|| ParseUser.getCurrentUser().getObjectId().toString()
								.equals("45LT0GnGVU")) {
					updateFields();
					getActivity().setProgressBarIndeterminateVisibility(true);

					mRestaurant.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException e) {
							if (getActivity() != null) {
								getActivity()
										.setProgressBarIndeterminateVisibility(
												false);
							}
							if (e == null) {
								mSaveButton.setEnabled(true);
								mCancelButton.setEnabled(true);
								mTakePictureButton.setEnabled(true);
								if (getActivity() != null) {
									getActivity().setResult(Activity.RESULT_OK);
									// Check if a query code was passed in, and
									// what it was
									if (getArguments() != null) {
										String queryCode = getArguments()
												.getString(
														RestaurantListFragment.QUERY_CODE);
										// If the query came from home, pop
										// backstack and create restaurant list
										// fragment
										if (queryCode
												.equals(NEW_RESTAURANT_FROM_HOME)) {
											FragmentManager fm = getFragmentManager();
											fm.popBackStack();
											RestaurantListFragment allRestaurants = new RestaurantListFragment();
											fm.beginTransaction()
													.replace(
															R.id.content_frame,
															allRestaurants)
													.addToBackStack(null)
													.commit();
										}
										// Otherwise we came from a listview,
										// remove fragment to return to it
										else {
											removeFragment();
										}
									}
								}
							}

						}
					});

				}

				// If the user doesn't have write permission, display alert
				// error
				else {

					mCancelButton.setEnabled(true);
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setMessage(R.string.edit_error_message)
							.setTitle(R.string.edit_error_title)
							.setPositiveButton(android.R.string.ok, null);
					AlertDialog permissionsDialog = builder.create();
					permissionsDialog.show();
				}
			}

			break;

		case R.id.cancelButton:
			if (getArguments() != null) {
				String queryCode = getArguments().getString(
						RestaurantListFragment.QUERY_CODE);
				// If we came from home, pop the backstack to return to the
				// homepage
				if (queryCode.equals(NEW_RESTAURANT_FROM_HOME)) {
					FragmentManager fm = getFragmentManager();
					fm.popBackStack();
				}
			}
			// Otherwise we came from a listview, remove fragment to return
			removeFragment();

			break;

		case R.id.takePictureButton:
			// Create a dialog to choose whether to take a picture or choose one
			// from the gallery
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setItems(R.array.camera_choices, mDialogListener);
			AlertDialog dialog_camera = builder.create();
			dialog_camera.show();

			break;

		}

	}

	// Listeners will wipe the button text if they are unchecked
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		switch (id) {
		case R.id.sunday_check:
			if (!mSunday.isChecked()) {
				mSundayOpenButton.setText("");
				mSundayCloseButton.setText("");
			}
			break;

		case R.id.monday_check:
			if (!mMonday.isChecked()) {
				mMondayOpenButton.setText("");
				mMondayCloseButton.setText("");
			}
			break;

		case R.id.tuesday_check:
			if (!mTuesday.isChecked()) {
				mTuesdayOpenButton.setText("");
				mTuesdayCloseButton.setText("");
			}
			break;

		case R.id.wednesday_check:
			if (!mWednesday.isChecked()) {
				mWednesdayOpenButton.setText("");
				mWednesdayCloseButton.setText("");
			}
			break;

		case R.id.thursday_check:
			if (!mThursday.isChecked()) {
				mThursdayOpenButton.setText("");
				mThursdayCloseButton.setText("");
			}
			break;

		case R.id.friday_check:
			if (!mFriday.isChecked()) {
				mFridayOpenButton.setText("");
				mFridayCloseButton.setText("");
			}
			break;

		case R.id.saturday_check:
			if (!mSaturday.isChecked()) {
				mSaturdayOpenButton.setText("");
				mSaturdayCloseButton.setText("");
			}
			break;
		}

	}

	// FocusChangeListeners
	// These add the pencil edit icon when edit text has focus
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id = v.getId();
		switch (id) {
		case R.id.restaurant_title:
			if (hasFocus) {
				mTitleField.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						android.R.drawable.ic_menu_edit, 0);
			} else {
				mTitleField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			}
			break;

		case R.id.restaurant_address:
			if (hasFocus) {
				mAddressField.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						android.R.drawable.ic_menu_edit, 0);
			} else {
				mAddressField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						0);
			}
			break;

		case R.id.restaurant_city:
			if (hasFocus) {
				mCityField.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						android.R.drawable.ic_menu_edit, 0);
			} else {
				mCityField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			}
			break;

		case R.id.restaurant_notes:
			if (hasFocus) {
				mNotesField.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						android.R.drawable.ic_menu_edit, 0);
			} else {
				mNotesField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			}
			break;
		}
	}

	// Method for creating new restaurant
	// Restaurant object is created with information from the restaurant
	// fragment fields

	private Restaurant createRestaurant() {
		Restaurant restaurant = new Restaurant();

		restaurant.setACL(parseACL);
		restaurant.setAuthor();

		restaurant.setTitle(mTitleField.getText().toString());
		restaurant.setLowerCaseTitle(mTitleField.getText().toString()
				.toLowerCase());

		restaurant.setAddress(mAddressField.getText().toString());
		restaurant.setLowerCaseAddress(mAddressField.getText().toString()
				.toLowerCase());

		restaurant.setCity(mCityField.getText().toString().trim());
		restaurant.setLowerCaseCity(mCityField.getText().toString()
				.toLowerCase().trim());

		restaurant.setPhone(PhoneNumberUtils.formatNumber(mPhoneField.getText()
				.toString()));

		restaurant.setSunday(mSunday.isChecked());
		restaurant.setSundayOpenHours(mSundayOpenButton.getText().toString());
		restaurant.setSundayCloseHours(mSundayCloseButton.getText().toString());

		restaurant.setMonday(mMonday.isChecked());
		restaurant.setMondayOpenHours(mMondayOpenButton.getText().toString());
		restaurant.setMondayCloseHours(mMondayCloseButton.getText().toString());

		restaurant.setTuesday(mTuesday.isChecked());
		restaurant.setTuesdayOpenHours(mTuesdayOpenButton.getText().toString());
		restaurant.setTuesdayCloseHours(mTuesdayCloseButton.getText()
				.toString());

		restaurant.setWednesday(mWednesday.isChecked());
		restaurant.setWednesdayOpenHours(mWednesdayOpenButton.getText()
				.toString());
		restaurant.setWednesdayCloseHours(mWednesdayCloseButton.getText()
				.toString());

		restaurant.setThursday(mThursday.isChecked());
		restaurant.setThursdayOpenHours(mThursdayOpenButton.getText()
				.toString());
		restaurant.setThursdayCloseHours(mThursdayCloseButton.getText()
				.toString());

		restaurant.setFriday(mFriday.isChecked());
		restaurant.setFridayOpenHours(mFridayOpenButton.getText().toString());
		restaurant.setFridayCloseHours(mFridayCloseButton.getText().toString());

		restaurant.setSaturday(mSaturday.isChecked());
		restaurant.setSaturdayOpenHours(mSaturdayOpenButton.getText()
				.toString());
		restaurant.setSaturdayCloseHours(mSaturdayCloseButton.getText()
				.toString());

		restaurant.setNotes(mNotesField.getText().toString());
		return restaurant;
	}

	// Loads data from restaurant object into restaurant fragment fields

	private void loadFields() {

		mTitleField.setText(mRestaurant.getTitle());
		mTitleField.clearFocus();

		if (getArguments() != null) {
			if (getArguments().getString(MAP_CODE) != null
					&& getArguments().getString(NAME_FROM_MAP) != null) {
				mTitleField.setText(getArguments().getString(NAME_FROM_MAP));
				mTitleField.clearFocus();
			}
		}

		mAddressField.setText(mRestaurant.getAddress());
		mAddressField.clearFocus();

		if (getArguments() != null) {
			if (getArguments().getString(MAP_CODE) != null
					&& getArguments().getString(ADDRESS_FROM_MAP) != null) {
				mAddressField.setText(getArguments()
						.getString(ADDRESS_FROM_MAP));
				mAddressField.clearFocus();
			}
		}

		mCityField.setText(mRestaurant.getCity());
		mCityField.clearFocus();

		if (getArguments() != null) {
			if (getArguments().getString(MAP_CODE) != null
					&& getArguments().getString(CITY_FROM_MAP) != null) {
				mCityField.setText(getArguments().getString(CITY_FROM_MAP));
				mCityField.clearFocus();
			}
		}

		mPhoneField.setText(mRestaurant.getPhone());
		mPhoneField.clearFocus();

		mSunday.setChecked(mRestaurant.getSunday());
		mMonday.setChecked(mRestaurant.getMonday());
		mTuesday.setChecked(mRestaurant.getTuesday());
		mWednesday.setChecked(mRestaurant.getWednesday());
		mThursday.setChecked(mRestaurant.getThursday());
		mFriday.setChecked(mRestaurant.getFriday());
		mSaturday.setChecked(mRestaurant.getSaturday());

		mSundayOpenButton.setText(mRestaurant.getSundayOpenHours());
		mSundayCloseButton.setText(mRestaurant.getSundayCloseHours());

		mMondayOpenButton.setText(mRestaurant.getMondayOpenHours());
		mMondayCloseButton.setText(mRestaurant.getMondayCloseHours());

		mTuesdayOpenButton.setText(mRestaurant.getTuesdayOpenHours());
		mTuesdayCloseButton.setText(mRestaurant.getTuesdayCloseHours());

		mWednesdayOpenButton.setText(mRestaurant.getWednesdayOpenHours());
		mWednesdayCloseButton.setText(mRestaurant.getWednesdayCloseHours());

		mThursdayOpenButton.setText(mRestaurant.getThursdayOpenHours());
		mThursdayCloseButton.setText(mRestaurant.getThursdayCloseHours());

		mFridayOpenButton.setText(mRestaurant.getFridayOpenHours());
		mFridayCloseButton.setText(mRestaurant.getFridayCloseHours());

		mSaturdayOpenButton.setText(mRestaurant.getSaturdayOpenHours());
		mSaturdayCloseButton.setText(mRestaurant.getSaturdayCloseHours());

		mNotesField.setText(mRestaurant.getNotes());
		mNotesField.clearFocus();

		// If an image file exists add it to the restaurant fragment
		ParseFile file = mRestaurant.getImage();
		if (file != null) {
			Uri fileUri = Uri.parse(file.getUrl());
			Log.i("file", "file" + fileUri);
			if (getActivity() != null) {
				Picasso.with(getActivity()).load(fileUri.toString())
						.into(mRestaurantImageView, new Callback() {

							@Override
							public void onSuccess() {
								if (getActivity() != null) {
									getActivity()
											.setProgressBarIndeterminateVisibility(
													false);
								}
							}

							@Override
							public void onError() {

							}
						});
			}
		}
	}

	// Update a restaurant object with data from restaurant fragment

	private void updateFields() {
		mRestaurant.setTitle(mTitleField.getText().toString());
		mRestaurant.setLowerCaseTitle(mTitleField.getText().toString()
				.toLowerCase());

		mRestaurant.setAddress(mAddressField.getText().toString());
		mRestaurant.setLowerCaseAddress(mAddressField.getText().toString()
				.toLowerCase());

		mRestaurant.setCity(mCityField.getText().toString().trim());
		mRestaurant.setLowerCaseCity(mCityField.getText().toString()
				.toLowerCase().trim());

		mRestaurant.setPhone(PhoneNumberUtils.formatNumber(mPhoneField
				.getText().toString()));

		mRestaurant.setSunday(mSunday.isChecked());
		mRestaurant.setMonday(mMonday.isChecked());
		mRestaurant.setTuesday(mTuesday.isChecked());
		mRestaurant.setWednesday(mWednesday.isChecked());
		mRestaurant.setThursday(mThursday.isChecked());
		mRestaurant.setFriday(mFriday.isChecked());
		mRestaurant.setSaturday(mSaturday.isChecked());

		mRestaurant.setSundayOpenHours(mSundayOpenButton.getText().toString());
		mRestaurant
				.setSundayCloseHours(mSundayCloseButton.getText().toString());

		mRestaurant.setMondayOpenHours(mMondayOpenButton.getText().toString());
		mRestaurant
				.setMondayCloseHours(mMondayCloseButton.getText().toString());

		mRestaurant
				.setTuesdayOpenHours(mTuesdayOpenButton.getText().toString());
		mRestaurant.setTuesdayCloseHours(mTuesdayCloseButton.getText()
				.toString());

		mRestaurant.setWednesdayOpenHours(mWednesdayOpenButton.getText()
				.toString());
		mRestaurant.setWednesdayCloseHours(mWednesdayCloseButton.getText()
				.toString());

		mRestaurant.setThursdayOpenHours(mThursdayOpenButton.getText()
				.toString());
		mRestaurant.setThursdayCloseHours(mThursdayCloseButton.getText()
				.toString());

		mRestaurant.setFridayOpenHours(mFridayOpenButton.getText().toString());
		mRestaurant
				.setFridayCloseHours(mFridayCloseButton.getText().toString());

		mRestaurant.setSaturdayOpenHours(mSaturdayOpenButton.getText()
				.toString());
		mRestaurant.setSaturdayCloseHours(mSaturdayCloseButton.getText()
				.toString());

		mRestaurant.setNotes(mNotesField.getText().toString());
	}

	//
	// protected void save(Restaurant restaurant) {
	// restaurant.saveInBackground(new SaveCallback() {
	// @Override
	// public void done(ParseException e) {
	// if (e == null) {
	//
	// // success!
	// // Toast.makeText(getActivity(), R.string.success_message,
	// // Toast.LENGTH_LONG).show();
	// } else {
	// AlertDialog.Builder builder = new AlertDialog.Builder(
	// getActivity());
	// builder.setMessage(R.string.error_saving)
	// .setTitle(R.string.error_saving_title)
	// .setPositiveButton(android.R.string.ok, null);
	// AlertDialog dialog = builder.create();
	// dialog.show();
	// }
	// }
	// });
	// }

	// Query parse for restaurant based on ID passed in from listview
	private void loadRestaurant() {
		// if an argument is passed in and is a restaurant ID, get it and store
		// it
		if (getArguments() != null) {
			if (getArguments().getString(EXTRA_RESTAURANT_ID) != null) {
				String restaurantId = (String) getArguments().getString(
						EXTRA_RESTAURANT_ID);
				// Fetch restaurant based on ID
				// Disable buttons so user can't save or take a picture in the
				// middle of fetching the restaurant
				mSaveButton.setEnabled(false);
				mCancelButton.setEnabled(false);
				mTakePictureButton.setEnabled(false);
				getActivity().setProgressBarIndeterminateVisibility(true);
				// query parse for the restaurant based on the ID passed in
				ParseQuery<Restaurant> query = ParseQuery
						.getQuery("Restaurant");
				query.getInBackground(restaurantId,
						new GetCallback<Restaurant>() {
							public void done(Restaurant restaurant,
									ParseException e) {
								if (e == null) {
									// Re-enable buttons

									mRestaurant = restaurant;
									if (mRestaurant != null) {
										Log.i("Restaurant",
												mRestaurant.toString());
									}
									mSaveButton.setEnabled(true);
									mCancelButton.setEnabled(true);
									mTakePictureButton.setEnabled(true);
									// If restaurant contains no image, turn off
									// progress
									// bar here, otherwise turn off when image
									// is done
									// loading in loadFields
									if (mRestaurant.getImage() == null) {
										if (getActivity() != null) {
											getActivity()
													.setProgressBarIndeterminateVisibility(
															false);
										}
									}
									// Check if current user is author or admin,
									// if not, disable save and picture button
									// and display warning
									String currentUser = ParseUser
											.getCurrentUser().getObjectId()
											.toString();
									String author = mRestaurant.getAuthor()
											.getObjectId().toString();
									if (!currentUser.equals(author)
											&& !currentUser
													.equals("e7So6F4ytk")
											&& !currentUser
													.equals("45LT0GnGVU")) {
										// if (currentUser.equals("e7So6F4ytk")
										// == false) {
										if (getActivity() != null) {
											Toast.makeText(
													getActivity(),
													R.string.edit_error_message,
													Toast.LENGTH_LONG).show();
										}
										// todo
										// mMapButton.setEnabled(false);
										mSaveButton.setEnabled(false);
										mTakePictureButton.setEnabled(false);
										// }
									}
									// set fields to restaurant object data
									loadFields();
								} else {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									builder.setMessage(e.getMessage())
											.setTitle(
													R.string.general_error_title)
											.setPositiveButton(
													android.R.string.ok, null);
									AlertDialog dialog = builder.create();
									dialog.show();

								}
							}
						});
			}
		}
	}

	// Adds proper padding and spacing to time for time buttons, adds 0 if less
	// hour is less than 10
	private static String padding_str(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	// Handle clicks to the dialog for choosing or taking pictures
	protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0: // Take picture
				Intent takePhotoIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				// save file path
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				// if mMediaUri is null, storage error
				if (mMediaUri == null) {
					Toast.makeText(getActivity(),
							R.string.external_storage_error, Toast.LENGTH_LONG)
							.show();
				} else {
					// pass photo path to camera
					takePhotoIntent
							.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
				}
				break;
			case 1: // Take video
				Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				choosePhotoIntent.setType("image/*");
				startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
				break;
			}
		}
	};

	private Uri getOutputMediaFileUri(int mediaType) {
		// check if external storage is mounted
		if (isExternalStorageAvailable()) {
			Log.i("test", "testing" + isExternalStorageAvailable());
			// get URI

			// 1. Get the external storage directory
			String appName = getActivity().getString(R.string.app_name);
			File mediaStorageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					appName);

			// 2. Create our subdirectory
			if (!mediaStorageDir.exists()) {
				// create directory, mkdirs returns boolean false if failed,
				// return nullif failed
				if (!mediaStorageDir.mkdirs()) {
					Log.i("DIR ERROR", "Failed to create directory");
					return null;
				}
			}
			// 3. Create a file name
			// 4 Create a file

			File mediaFile;
			Date now = new Date();
			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
					Locale.US).format(now);

			String path = mediaStorageDir.getPath() + File.separator;
			// Check media type and create file
			if (mediaType == MEDIA_TYPE_IMAGE) {
				mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
			} else {
				return null;
			}
			Log.i("Filename", "file " + Uri.fromFile(mediaFile));
			// 5 Return the file's URI
			return Uri.fromFile(mediaFile);
		} else {
			return null;
		}

	}

	// Removes current fragment, pops backstack if unwanted fragments are added
	public void removeFragment() {
		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 1) {
			fragmentManager.popBackStack();
		}
		fragmentManager.beginTransaction().remove(this).commit();
	}

	private boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mMediaUri != null) {
			outState.putString("cameraImageUri", mMediaUri.toString());
			if (mRestaurant != null) {
				outState.putString("restaurant", mRestaurant.getObjectId());
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("cameraImageUri")) {
				mMediaUri = Uri.parse(savedInstanceState
						.getString("cameraImageUri"));
			}
			if (savedInstanceState.containsKey("restaurant")) {
				// TODO fix rotation issue
			}

		}
	}
}
