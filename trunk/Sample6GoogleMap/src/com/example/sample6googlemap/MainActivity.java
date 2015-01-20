package com.example.sample6googlemap;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends ActionBarActivity implements
	GoogleMap.OnCameraChangeListener {

	GoogleMap mMap;

	LocationManager mLM;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupMapIfNeeded();
		mLM = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Button btn = (Button)findViewById(R.id.btn_zoom_in);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CameraUpdate update = CameraUpdateFactory.zoomIn();
				mMap.animateCamera(update);
			}
		});
		
		btn = (Button)findViewById(R.id.btn_zoom_out);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CameraUpdate update = CameraUpdateFactory.zoomOut();
				mMap.animateCamera(update);
			}
		});
		
	}
	
	LocationListener mListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			moveMap(location);
			mLM.removeUpdates(mListener);
		}
	};
	
	private void moveMap(Location location) {
		if (mMap != null) {
			CameraPosition.Builder builder = new CameraPosition.Builder();
			builder.target(new LatLng(location.getLatitude(), location.getLongitude()));
			builder.zoom(15.5f);
//			builder.bearing(20);
//			builder.tilt(30);
			CameraUpdate update = CameraUpdateFactory.newCameraPosition(builder.build());
//			mMap.moveCamera(update);
			mMap.animateCamera(update);
		}
	}
	
	String mProvider = LocationManager.GPS_PROVIDER;
	
	@Override
	protected void onStart() {
		super.onStart();
		Location location = mLM.getLastKnownLocation(mProvider);
		if (location != null) {
			mListener.onLocationChanged(location);
		}
		mLM.requestLocationUpdates(mProvider, 0, 0, mListener);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mLM.removeUpdates(mListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setupMapIfNeeded();
	}

	private void setupMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment1)).getMap();
			if (mMap != null) {
				setupMap();
			}
		}
	}

	private void setupMap() {
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(true);
		mMap.getUiSettings().setCompassEnabled(true);
		mMap.setOnCameraChangeListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		LatLng latlng = position.target;
		Log.i("MainActivity","lat : " + latlng.latitude + ", lng : " + latlng.longitude);
	}
}
