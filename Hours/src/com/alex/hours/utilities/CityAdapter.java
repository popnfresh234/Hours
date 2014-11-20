package com.alex.hours.utilities;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alex.hours.R;

public class CityAdapter extends ArrayAdapter<String> {

	protected Context mContext;
	protected List<String> mCities;
	protected String mCity;


	public CityAdapter(Context context, List<String> cities) {
		super(context, R.layout.list_item_city, cities);
		mContext = context;
		mCities = cities;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = View.inflate(getContext(),
					R.layout.list_item_city, null);
		}

		mCity = mCities.get(position);

		// Set Title
		TextView titleTextView = (TextView) convertView
				.findViewById(R.id.city_list_item_titleTextView);
		titleTextView.setText(mCity);

		

		return convertView;
	}

	public void refill(List<String> cities) {
		mCities.clear();
		mCities.addAll(cities);
		notifyDataSetChanged();
		
	}
}
