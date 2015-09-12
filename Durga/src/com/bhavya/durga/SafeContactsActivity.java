package com.bhavya.durga;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class SafeContactsActivity extends AppCompatActivity{
	private static final int PICK_CONTACT  = 100;
	private ContactListAdapter adapter;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_from_contacts);
		init();
	}

	private void init() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ListView listView = (ListView) findViewById(R.id.listView1);
		setListView(listView);
	}
	
	private void setListView(ListView listView) {
		List<ContactsDetailsModel> allContacts = ContactsDetailsModel.getAllContacts();
		this.adapter = new ContactListAdapter(getApplicationContext(), allContacts, true);
		listView.setAdapter(adapter);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_contacts, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (PICK_CONTACT):
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();
				String[] projection = { Phone.NUMBER, Phone.DISPLAY_NAME };

				Cursor cursor = getContentResolver().query(uri, projection,
						null, null, null);
				cursor.moveToFirst();

				int numberColumnIndex = cursor.getColumnIndex(Phone.NUMBER);
				String number = cursor.getString(numberColumnIndex);

				int nameColumnIndex = cursor.getColumnIndex(Phone.DISPLAY_NAME);
				String name = cursor.getString(nameColumnIndex);
				if (number != null) {
					ContactsDetailsModel.save(name, number);
					refreshAdapter();
				}
			}
			break;
		}
	}
	
	private void refreshAdapter() {
		List<ContactsDetailsModel> allContacts = ContactsDetailsModel.getAllContacts();
		adapter.refreshAdapter(allContacts);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.actionAddContacts){
			pickFromContact();
		}else if(item.getItemId() == android.R.id.home){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void pickFromContact() {
		Uri uri = Uri.parse("content://contacts");
		Intent intent = new Intent(Intent.ACTION_PICK, uri);
		intent.setType(Phone.CONTENT_TYPE);
		startActivityForResult(intent, PICK_CONTACT);

	}


}
