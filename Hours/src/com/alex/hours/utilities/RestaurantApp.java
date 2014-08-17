package com.alex.hours.utilities;

import android.app.Application;

import com.alex.hours.Restaurant;
import com.parse.Parse;
import com.parse.ParseObject;

public class RestaurantApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ParseObject.registerSubclass(Restaurant.class);
		Parse.initialize(this, "hBnh7k8KhNrDiQIyXELw5tnfvBa4eLXBjgzmHOjL",
				"5KLdf0eGFBB0LPcZ1fFHaq6IEE2TtrH6yJjoJ21m");
	}
}
