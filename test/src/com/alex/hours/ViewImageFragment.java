package com.alex.hours;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.alex.hours.R;
import com.squareup.picasso.Picasso;

public class ViewImageFragment extends Fragment {

	private ImageView mImageView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.view_image_fragment, container,
				false);
		mImageView = (ImageView) v.findViewById(R.id.imageView);

		Uri imageUri = getActivity().getIntent().getData();

		Picasso.with(getActivity()).load(imageUri.toString()).into(mImageView);

		return v;
	}

}
