package com.manoh.coding.addressbook_api;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;
import com.manoh.coding.addressbook_api.Contact;


public class ElasticsearchInteractionTest {
	
	ElasticsearchInteraction esiObj = new ElasticsearchInteraction();
	
	@Test
	public void testGetContact() throws IOException {
		Contact contact = esiObj.getContact("test123");
		
		assertNotNull(contact);
		assertEquals(contact.getName(), "test123");
	}
	
	@Test
	public void testCreateContact() throws IOException {
		Contact contact = new Contact();
		
		Random rand = new Random(); 
		int value = rand.nextInt(50); 
		
		// set the values
		contact.setName("maddy"+value);
		contact.setPhone(1234567890);
		contact.setEmail("maddy@gmail.com");
		contact.setAddress("Mesa, Arizona");		
		
		boolean result = esiObj.createContact(contact);
		
		assertTrue(result);
	}
	
	@Test
	public void testCreateContact_alreadyExistsCondition() throws IOException {
		Contact contact = new Contact();
		
		// set the values
		contact.setName("test123");
		contact.setPhone(1234567890);
		contact.setEmail("maddy@gmail.com");
		contact.setAddress("Mesa, Arizona");		
		
		boolean result = esiObj.createContact(contact);
		
		assertFalse("Added existing Contact", result);
	}
	
	@Test
	public void testUpdateContact() throws IOException, InterruptedException {
		// Create contact
		Contact contact = new Contact();
		// set the values
		contact.setName("Dandy");
		contact.setPhone(1234567890);
		contact.setEmail("dandy@gmail.com");
		contact.setAddress("Mesa, Arizona");		
		
		esiObj.createContact(contact);
		
		Thread.sleep(2000);
		
		Contact contact2 = esiObj.getContact("Dandy");
		
		// Update the values
		contact2.setName("Sandy");
		contact2.setPhone(1111111111);
		contact2.setEmail("sandy@gmail.com");
		contact2.setAddress("Tempe, Arizona");
		
		boolean result2 = esiObj.updateContact("Dandy", contact);
		
		assertTrue(result2);
	}
	
	@Test
	public void testGetContactList_fromAndSizeCondition() throws IOException, InterruptedException {
		// Create contact
		Contact contact = new Contact();
		// set the values
		contact.setName("Randy");
		contact.setPhone(1234567890);
		contact.setEmail("randy@gmail.com");
		contact.setAddress("Mesa, Arizona");		
				
		esiObj.createContact(contact);
		
		Thread.sleep(2000);
		
		ArrayList<Contact> contactList = new ArrayList<Contact>(); 
		contactList = esiObj.getContactList(2, 1, "*");
		boolean valueFound = false;
		for (Contact c : contactList) {
			c.getName().equals("Randy");
			valueFound = true;
			break;
		}
		
		assertNotNull(contactList);
		assertTrue("Search based on Page and PageSize failed", contactList.size() > 0);
		assertTrue(valueFound);
	}

	@Test
	public void testGetContactList_queryStringCondition() throws IOException, InterruptedException {
		// Create contact
		Contact contact = new Contact();
		// set the values
		contact.setName("Candy");
		contact.setPhone(1234567890);
		contact.setEmail("candy@gmail.com");
		contact.setAddress("Phoenix, Arizona");		
						
		esiObj.createContact(contact);
		
		Thread.sleep(2000);
				
		ArrayList<Contact> contactList;
		contactList = new ArrayList<Contact>();
		contactList = esiObj.getContactList(2, 0, "+name:Candy OR address:Phoenix, Arizona*");
		assertNotNull(contactList);
		assertTrue("Search based on Query String Query failed", contactList.size() > 0);
	}

	@Test
	public void testDeleteContact() throws IOException, InterruptedException {
		// Create contact
		Contact contact = new Contact();
		// set the values
		contact.setName("Baddy");
		contact.setPhone(1234567890);
		contact.setEmail("baddy@gmail.com");
		contact.setAddress("Tempe, Arizona");		
						
		esiObj.createContact(contact);
		
		Thread.sleep(2000);
				
		boolean result = esiObj.deleteContact("Baddy");
		assertTrue(result);
	}
}
