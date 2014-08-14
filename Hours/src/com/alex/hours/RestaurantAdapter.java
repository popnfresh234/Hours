package com.alex.hours;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.alex.hours.R;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

	protected Context mContext;
	protected List<Restaurant> mRestaurants;

	public RestaurantAdapter(Context context, List<Restaurant> restaurants) {
		super(context, R.layout.list_item_restaurant, restaurants);
		mContext = context;
		mRestaurants = restaurants;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = View.inflate(getContext(),
					R.layout.list_item_restaurant, null);
		}


		Restaurant restaurant = mRestaurants.get(position);

		// Set Title
		TextView titleTextView = (TextView) convertView
				.findViewById(R.id.restaurant_list_item_titleTextView);
		titleTextView.setText(restaurant.getTitle());
		
		//Set Address
		TextView addressTextView = (TextView) convertView
				.findViewById(R.id.restaurant_list_item_addressTextView);
		addressTextView.setText(restaurant.getAddress());
		
		//Set day abbreviations
		TextView sunday = (TextView) convertView
				.findViewById(R.id.restaurant_list_sunday);
		TextView monday = (TextView) convertView
				.findViewById(R.id.restaurant_list_monday);
		TextView tuesday = (TextView) convertView
				.findViewById(R.id.restaurant_list_tuesday);
		TextView wednesday = (TextView) convertView
				.findViewById(R.id.restaurant_list_wednesday);
		TextView thursday = (TextView) convertView
				.findViewById(R.id.restaurant_list_thursday);
		TextView friday = (TextView) convertView
				.findViewById(R.id.restaurant_list_friday);
		TextView saturday = (TextView) convertView
				.findViewById(R.id.restaurant_list_saturday);

		// Set text to red if not open
		if (restaurant.getSunday() == false) {
			sunday.setTextColor(0xffbbbbbb);
		} else {
			sunday.setTextColor(0xff000000);
		}

		if (restaurant.getMonday() == false) {
			monday.setTextColor(0xffbbbbbb);
		} else {
			monday.setTextColor(0xff000000);
		}

		if (restaurant.getTuesday() == false) {
			tuesday.setTextColor(0xffbbbbbb);
		} else {
			tuesday.setTextColor(0xff000000);
		}

		if (restaurant.getWednesday() == false) {
			wednesday.setTextColor(0xffbbbbbb);
		} else {
			wednesday.setTextColor(0xff000000);
		}

		if (restaurant.getThursday() == false) {
			thursday.setTextColor(0xffbbbbbb);
		} else {
			thursday.setTextColor(0xff000000);
		}

		if (restaurant.getFriday() == false) {
			friday.setTextColor(0xffbbbbbb);
		} else {
			friday.setTextColor(0xff000000);
		}

		if (restaurant.getSaturday() == false) {
			saturday.setTextColor(0xffbbbbbb);
		} else {
			saturday.setTextColor(0xff000000);
		}
		


		return convertView;
	}

	public void refill(List<Restaurant> restaurants) {
		mRestaurants.clear();
		mRestaurants.addAll(restaurants);
		notifyDataSetChanged();
	}
}
