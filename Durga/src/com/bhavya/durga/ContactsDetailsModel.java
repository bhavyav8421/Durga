package com.bhavya.durga;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

public class ContactsDetailsModel extends Model{

	public static final String NAME = "name";
	public static final String CONTACT_NO = "contact_no";

	@Column(name = NAME)
	private String name;
	
	@Column(name = CONTACT_NO)
	private String contactNo;

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static List<ContactsDetailsModel> getAllContacts() {
		return new Select().from(ContactsDetailsModel.class).execute();

	}
	
	public static ContactsDetailsModel getContact(String contactNo) {
		return new Select().from(ContactsDetailsModel.class).where(NAME +" = ?", contactNo).executeSingle();
	}
	
	public static void save(String name , String number) {
		ContactsDetailsModel model = getContact(number);
		if(model == null){
			model = new ContactsDetailsModel();
		}
		model.setName(name);
		model.setContactNo(number);
		model.save();
	}
}
