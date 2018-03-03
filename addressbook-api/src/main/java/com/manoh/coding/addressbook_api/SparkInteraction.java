package com.manoh.coding.addressbook_api;

import static spark.Spark.*;
import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.manoh.coding.addressbook_api.Validation.ContactValidation;

/**
 * Spark API interaction logic
 */
public class SparkInteraction {
	public static void main(String[] args) throws IOException {
        
		ElasticsearchInteraction elasticObj = new ElasticsearchInteraction();
		
        get("/contact/:name", (request, response)->{
        	String name = request.params(":name");
            Contact contact = elasticObj.getContact(name);
            return new Gson().toJson(contact, Contact.class);
        });
        
        get("/contact", (request, response)->{
            ArrayList<Contact> contactList = new ArrayList<Contact>();
        	if (request.queryParams() != null) {
        		int pageSize = 10;
        		int page = 0;
        		String query = "*";
        		if (request.queryParamsValues("pageSize") != null && 
        				request.queryParamsValues("pageSize").length > 0) {
        			pageSize = Integer.parseInt(request.queryParamsValues("pageSize")[0]);
        		}
        		if (request.queryParamsValues("pageSize") != null && 
        				request.queryParamsValues("pageSize").length > 0) {
        			page = Integer.parseInt(request.queryParamsValues("page")[0]);
        		}
        		if (request.queryParamsValues("pageSize") != null && 
        				request.queryParamsValues("pageSize").length > 0) {
        			query = request.queryParamsValues("query")[0];
        		}
                contactList = elasticObj.getContactList(pageSize, page, query);
        	}
            return new Gson().toJson(contactList);
        });
        
        post("/contact", (request, response) -> {
            response.type("application/json");
        	Contact contact = new Gson().fromJson(request.body(), Contact.class);
        	// Validate received data
        	boolean isValid = new ContactValidation().validate(contact);
        	if (isValid) {
        		boolean result = elasticObj.createContact(contact);
                if (result) {
                	return new Gson().toJson("Contact created");
                }
                else {
                	return new Gson().toJson("Contact already exists");
                }
        	}
        	else {
        		response.status(404);
        		return new Gson().toJson("Contact data validation failed");
        	}
        });
        
        put("/contact/:name", (request, response) -> {
            response.type("application/json");
            String name = request.params(":name");
            Contact contact = new Gson().fromJson(request.body(), Contact.class);
        	// Validate received data
            boolean isValid = new ContactValidation().validate(contact);
            if (isValid) {
            	boolean result = elasticObj.updateContact(name, contact);
                if (result) {
                	return new Gson().toJson("Contact updated");
                }
                else {
                	response.status(404);
                	return new Gson().toJson("Contact does not exist");
                }
            }
            else {
            	response.status(404);
        		return new Gson().toJson("Contact data validation failed");
            }
        });
        
        delete("/contact/:name", (request, response) -> {
            response.type("application/json");
            String name = request.params(":name");
            boolean result = elasticObj.deleteContact(name);
            if (result) {
            	return new Gson().toJson("Contact deleted");
            }
            else {
            	response.status(404);
            	return new Gson().toJson("Contact does not exist");
            }
        });
	}
}
