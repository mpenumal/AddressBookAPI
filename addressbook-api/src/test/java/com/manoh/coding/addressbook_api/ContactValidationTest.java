package com.manoh.coding.addressbook_api;

import static org.junit.Assert.*;

import org.junit.Test;

import com.manoh.coding.addressbook_api.Validation.EmailValidation;
import com.manoh.coding.addressbook_api.Validation.PhoneValidation;

public class ContactValidationTest {

	@Test
	public void contactValidationTest_emailCondition() {
		EmailValidation ev = new EmailValidation();
		
		String email = "maddy@gmail.com";
		assertTrue(ev.validate(email));
		
		email = "maddy";
		assertFalse(ev.validate(email));
	}

	@Test
	public void contactValidationTest_phoneCondition() {
		PhoneValidation pv = new PhoneValidation();
		
		long phone = 1234567890;
		assertTrue(pv.validate(phone));
		
		phone = 12345;
		assertFalse(pv.validate(phone));
	}
}
