package com.alex.hours.utilities;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alex.hours.R;
import com.alex.hours.Restaurant;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

	protected Context mContext;
	protected List<Restaurant> mRestaurants;
	protected Restaurant mRestaurant;
	protected ImageView mFavoriteImage;
	protected List<Restaurant>mFavorites;

	public RestaurantAdapter(Context context, List<Restaurant> restaurants, List<Restaurant>favorites) {
		super(context, R.layout.list_item_restaurant, restaurants);
		mContext = context;
		mRestaurants = restaurants;
		mFavorites = favorites;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = View.inflate(getContext(),
					R.layout.list_item_restaurant, null);
		}

		mRestaurant = mRestaurants.get(position);

		// Set Title
		TextView titleTextView = (TextView) convertView
				.findViewById(R.id.restaurant_list_item_titleTextView);
		titleTextView.setText(mRestaurant.getTitle());

		// Set Address
		TextView addressTextView = (TextView) convertView
				.findViewById(R.id.restaurant_list_item_addressTextView);
		addressTextView.setText(mRestaurant.getAddress());

		// Set day abbreviations
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

		ImageView favoriteImage = (ImageView) convertView.findViewById(R.id.favourite_image);
		favoriteImage.setBackgroundResource(R.drawable.ic_rating_not_important);
		for(Restaurant r : mFavorites){
			if (r.getObjectId().equals(mRestaurant.getObjectId())){
				favoriteImage.setBackgroundResource(R.drawable.ic_rating_important);
			}
		}
//		ParseUser user = ParseUser.getCurrentUser();
//		ParseRelation<Restaurant> relation = user.getRelation(ParseConstants.RELATION_FAVORITE);
//		ParseQuery <Restaurant> query = relation.getQuery();
//		query.whereEqualTo("objectId", mRestaurant.getObjectId());
//		
//		query.findInBackground(new FindCallback<Restaurant>() {
//			
//			@Override
//			public void done(List<Restaurant> favorites, ParseException e) {
//				// TODO Auto-generated method stub
//				if(favorites.size()>0){
//					
//					favoriteImage.setBackgroundResource(R.drawable.ic_rating_important);
//				}
//				
//			}
//		});
		
		// Set text to red if not open
		if (mRestaurant.getSunday() == false) {
			sunday.setTextColor(0xffbbbbbb);
		} else {
			sunday.setTextColor(0xff000000);
		}

		if (mRestaurant.getMonday() == false) {
			monday.setTextColor(0xffbbbbbb);
		} else {
			monday.setTextColor(0xff000000);
		}

		if (mRestaurant.getTuesday() == false) {
			tuesday.setTextColor(0xffbbbbbb);
		} else {
			tuesday.setTextColor(0xff000000);
		}

		if (mRestaurant.getWednesday() == false) {
			wednesday.setTextColor(0xffbbbbbb);
		} else {
			wednesday.setTextColor(0xff000000);
		}

		if (mRestaurant.getThursday() == false) {
			thursday.setTextColor(0xffbbbbbb);
		} else {
			thursday.setTextColor(0xff000000);
		}

		if (mRestaurant.getFriday() == false) {
			friday.setTextColor(0xffbbbbbb);
		} else {
			friday.setTextColor(0xff000000);
		}

		if (mRestaurant.getSaturday() == false) {
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
