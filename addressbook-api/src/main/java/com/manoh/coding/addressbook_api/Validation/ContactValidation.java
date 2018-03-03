package com.manoh.coding.addressbook_api.Validation;

import com.manoh.coding.addressbook_api.Contact;

public class ContactValidation {
	public boolean validate(Contact contact) {
		EmailValidation ev = new EmailValidation();
		if (!ev.validate(contact.getEmail())) {
			return false;
		}
		
		PhoneValidation pv = new PhoneValidation();
		if (!pv.validate(contact.getPhone())) {
			return false;
		}
		
		return true;
	}
}
