package com.bhavya.durga;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactListAdapter extends BaseAdapter{

	private List<ContactsDetailsModel>  contactDetails;
	private LayoutInflater inflater;
	private boolean isPickFromContactAdapter;

	public ContactListAdapter(Context context  , List<ContactsDetailsModel> models , boolean isPickFromContactAdapter) {
		this.contactDetails = models;
		this.isPickFromContactAdapter = isPickFromContactAdapter;
		this.inflater = (LayoutInflater)context.getSystemService
			      (Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contactDetails.size();
	}

	@Override
	public ContactsDetailsModel getItem(int position) {
		return contactDetails.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.list_item, parent, false);
			viewHolder = new ViewHolder(this);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.init(convertView);
		viewHolder.setViews(position, getItem(position));
		convertView.setTag(viewHolder);
		return convertView;
	}
	
	private void removeFromContact(int pos) {
		ContactsDetailsModel contactsDetailsModel = getItem(pos);
		contactsDetailsModel.delete();
		contactDetails.remove(pos);
		notifyDataSetChanged();
	}
	
	public boolean isPickFromContactAdapter() {
		return isPickFromContactAdapter;
	}
	
	class ViewHolder{
		private TextView nameTextView , numberTextView ;
		private ImageView imageView;
		private ContactListAdapter adapter;
		
		public ViewHolder(ContactListAdapter adapter) {
			this.adapter = adapter;
		}
		public void init(View view) {
			nameTextView = (TextView) view.findViewById(R.id.textView1);
			numberTextView = (TextView) view.findViewById(R.id.textView2);
			imageView = (ImageView) view.findViewById(R.id.imageViewDelete);
		}
		
		private void setViews(final int pos ,ContactsDetailsModel model) {
			nameTextView.setText(model.getName());
			numberTextView.setText(model.getContactNo());
			
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					adapter.removeFromContact(pos);
					
				}
			});
			if(adapter.isPickFromContactAdapter){
				imageView.setVisibility(View.VISIBLE);
			}else{
				imageView.setVisibility(View.INVISIBLE);
			}
		}
	}

	public  void refreshAdapter(List<ContactsDetailsModel> allContacts) {
		this.contactDetails = allContacts;
		notifyDataSetChanged();
	}

}
