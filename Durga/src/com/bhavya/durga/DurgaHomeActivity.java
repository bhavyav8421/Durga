package com.bhavya.durga;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class DurgaHomeActivity extends AppCompatActivity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener , LocationListener{

	private GoogleApiClient googleClient;
	private Location mLastLocation;
	private String address;
	private boolean isConnected, isLocationReceived;
	private static final LocationRequest REQUEST = LocationRequest.create()
	            .setInterval(5000)         // 5 seconds
	            .setFastestInterval(16)    // 16ms = 60fps
	            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		this.googleClient = getGoogleClient();
	}

	@Override
	protected void onResume() {
		super.onResume();
		googleClient.connect();
	}

	private void init() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(toolbar);
		TextView durgaTextView = (TextView) findViewById(R.id.textViewDurgaStation);
		TextView messageButton = (TextView) findViewById(R.id.textViewMessage);
		TextView callButton = (TextView) findViewById(R.id.textViewDial);
		TextView policeButton = (TextView) findViewById(R.id.textViewPolice);
		TextView sirenButton = (TextView) findViewById(R.id.textViewSiren);
		messageButton.setOnClickListener(this);
		callButton.setOnClickListener(this);
		policeButton.setOnClickListener(this);
		sirenButton.setOnClickListener(this);
		durgaTextView.setOnClickListener(this);

	}

	private GoogleApiClient getGoogleClient() {
		return new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.durga_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.actionAddContacts) {
			startAddSafeContacts();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.textViewSiren:
			playPanicAlarm();
			isLocationReceived = false;
			if(isConnected){
				sendSMSWithLocationDetails();
			}else{
				sendMessage();
			}
			break;
			
		case R.id.textViewMessage:
			isLocationReceived = false;
			if(isConnected){
				sendSMSWithLocationDetails();
			}else{
				sendMessage();
			}
			break;	
			
		case R.id.textViewDial:
			startCall();
			break;	
			
		case R.id.textViewDurgaStation:
			startDurgaStationActivity();
			break;	

		case R.id.textViewPolice:
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ "9620181112"));
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(getApplicationContext(), "Location received",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Toast.makeText(getApplicationContext(), "Location received",
				Toast.LENGTH_SHORT).show();
		this.isConnected = true;
	}
	
	private void sendSMSWithLocationDetails() {
		LocationServices.FusedLocationApi.requestLocationUpdates(googleClient, REQUEST, this); 
	}

	@Override
	public void onConnectionSuspended(int cause) {

	}
	
	private void playPanicAlarm() {
		MediaPlayer mediaPlayer = MediaPlayer.create(
				getApplicationContext(), R.raw.panicalarm);
		mediaPlayer.start();

	}
	
	private void startAddSafeContacts() {
		Intent intent = new Intent(getApplicationContext(),
				SafeContactsActivity.class);
		startActivity(intent);

	}
	
	private void startDurgaStationActivity() {
		Intent intent = new Intent(getApplicationContext(),
				DurgaStationActivity.class);
		startActivity(intent);

	}
	
	private void startCall() {
		Intent intent = new Intent(getApplicationContext(),
				CallActivity.class);
		startActivity(intent);

	}

	private void retrieveAddressInAsync() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					address = retriveAddress(mLastLocation);
					address = address+ " Location "+mLastLocation.getLatitude()+" "+mLastLocation.getLongitude();
					sendMessage();
				} catch (IOException e) {
					address = "My Location "+mLastLocation.getLatitude()+" "+mLastLocation.getLongitude();
					sendMessage();
				}
				return null;
			}
		}.execute();

	}

	private String retriveAddress(Location location) throws IOException {
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());

		addresses = geocoder.getFromLocation(location.getLatitude(),
				location.getLongitude(), 1); // Here 1 represent max location
												// result to returned, by
												// documents it recommended 1 to
												// 5
		String address = addresses.get(0).getAddressLine(0); // If any
																// additional
																// address line
																// present than
																// only, check
																// with max
																// available
																// address lines
																// by
																// getMaxAddressLineIndex()
		String city = addresses.get(0).getLocality();
		String state = addresses.get(0).getAdminArea();
		String country = addresses.get(0).getCountryName();
		String postalCode = addresses.get(0).getPostalCode();
		String knownName = addresses.get(0).getFeatureName();

		return knownName + ", " + address + ", " + city + ", " + postalCode
				+ ", " + state + ", " + country;
	}
	
	private void sendMessage() {
		List<ContactsDetailsModel> allContacts = ContactsDetailsModel.getAllContacts();
		if(allContacts != null && allContacts.size() > 0){
			for (Iterator iterator = allContacts.iterator(); iterator.hasNext();) {
				ContactsDetailsModel contactsModel = (ContactsDetailsModel) iterator.next();
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage( contactsModel.getContactNo() , null, "I am in a trouble. Please help. My location "+address , null, null);
				
			}
		}else{
			Toast.makeText(getApplicationContext(), "You haven't set any emergency contacts", Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		this.mLastLocation = arg0;
		if(!isLocationReceived){
			isLocationReceived = true;
			retrieveAddressInAsync();
		}
	}


}
