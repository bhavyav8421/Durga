package com.bhavya.durga;

import java.util.ArrayList;
import java.util.Iterator;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DurgaStationActivity extends FragmentActivity implements
ConnectionCallbacks,
OnConnectionFailedListener,
LocationListener,
OnMyLocationButtonClickListener,
OnMapReadyCallback {


	private GoogleApiClient mGoogleApiClient;
	private GoogleMap map;
	private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_durga_station);
		
		SupportMapFragment mapFragment =
				(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addApi(LocationServices.API)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.build();
	}

	private ArrayList<Location> getLocations() {
		ArrayList<Location> locationArray = new ArrayList<Location>();
		Location location1 = new Location("dummyprovider1");
		location1.setLatitude(12.90970401);
		location1.setLongitude(77.63267756);

		locationArray.add(location1);
		return locationArray;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGoogleApiClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onMapReady(GoogleMap map) {
		this.map = map;
		map.setMyLocationEnabled(true);
		map.setOnMyLocationButtonClickListener(this);
		ArrayList<Location> locations = getLocations();
		addMarkersOnMaps(locations);
	}

	/**
	 * Button to get current Location. This demonstrates how to get the current Location as required
	 * without needing to register a LocationListener.
	 */
	public void showMyLocation(View view) {
		if (mGoogleApiClient.isConnected()) {
			String msg = "Location = "
					+ LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Implementation of {@link LocationListener}.
	 */
	@Override
	public void onLocationChanged(Location location) {
		zoomToLocation(location);
		ArrayList<Location> locations = getLocations();
		addMarkersOnMaps(locations);
	}

	/**
	 * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient,
				REQUEST,
				this);  // LocationListener
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnectionSuspended(int cause) {
		// Do nothing
	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}

	public void addMarkersOnMaps(ArrayList<Location> locations){
		for (Iterator iterator = locations.iterator(); iterator.hasNext();) {
			Location location = (Location) iterator.next();
			map.addMarker(new MarkerOptions()
			.position(new LatLng(location.getLatitude() ,  location.getLongitude()))
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
			.title("Durga Station"));
			zoomToLocation(location);
		}
	}

	public void zoomToLocation(Location location){
		CameraUpdate center=
				CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
						location.getLongitude()));
		CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

		map.moveCamera(center);
		map.animateCamera(zoom);
	}
}