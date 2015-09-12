package com.bhavya.durga;

import java.util.List;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CallActivity extends AppCompatActivity{
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
		wireListener(listView);
	}
	
	private void wireListener(ListView listView) {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ContactsDetailsModel contactsDetailsModel = adapter.getItem(arg2);
				makeCall(contactsDetailsModel.getContactNo());
			}
		});

	}
	
	private void setListView(ListView listView) {
		List<ContactsDetailsModel> allContacts = ContactsDetailsModel.getAllContacts();
		this.adapter = new ContactListAdapter(getApplicationContext(), allContacts, false);
		listView.setAdapter(adapter);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void makeCall(String number) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ number));
		startActivity(intent);

	}

}