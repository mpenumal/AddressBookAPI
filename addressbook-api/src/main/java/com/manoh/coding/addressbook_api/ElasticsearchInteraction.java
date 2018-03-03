package com.manoh.coding.addressbook_api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Elasticsearch data store interaction logic.
 *
 */
public class ElasticsearchInteraction {
	
	// Get paginated list of all the documents under "contact" index.
	public ArrayList<Contact> getContactList(int pageSize, int page, String query) throws IOException {
		RestClient restClient = createElasticRestClient();
		checkIndex(restClient);
		restClient.close();
		
		RestHighLevelClient rhlClient = createElasticRestHighLevelClient();		
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		QueryStringQueryBuilder qsqBuilder = new QueryStringQueryBuilder(query);
		
		qsqBuilder.defaultField("name");
		sourceBuilder.query(qsqBuilder);
		sourceBuilder.from(page);
		sourceBuilder.size(pageSize);
		searchRequest.source(sourceBuilder);
		
		SearchResponse searchResponse = rhlClient.search(searchRequest);
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        
		if (searchResponse != null) {
			SearchHits hits = searchResponse.getHits();
			long totalHits = hits.getTotalHits();
			SearchHit[] searchHits = hits.getHits();
	        if (totalHits >= 1) {
	        	for (SearchHit hit : searchHits) {
	        		Contact contact = new Contact();
	        		Map<String, Object> sourceAsMap = hit.getSourceAsMap();
	        		if (sourceAsMap != null) {
	        			contact.setId(searchHits[0].getId());
		        		contact.setName((String) sourceAsMap.get("name"));
		        		contact.setPhone(Long.parseLong(sourceAsMap.get("phone").toString()));
		        		contact.setEmail((String) sourceAsMap.get("email"));
		        		contact.setAddress((String) sourceAsMap.get("address"));

		        		contactList.add(contact);
	        		}
	        	}
	        }
		}
        rhlClient.close();
        return contactList;
	}

	// Get document under "contact" index for given unique name.
	public Contact getContact(String name) throws IOException {
		RestClient restClient = createElasticRestClient();
		checkIndex(restClient);
		restClient.close();		
		
		RestHighLevelClient rhlClient = createElasticRestHighLevelClient();
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		MatchQueryBuilder query = QueryBuilders.matchQuery("name", name);
		
		sourceBuilder.query(query);
		searchRequest.source(sourceBuilder);
        
        SearchResponse searchResponse = rhlClient.search(searchRequest);
		Contact contact = new Contact();
        if (searchResponse != null) {
        	SearchHits hits = searchResponse.getHits();
			long totalHits = hits.getTotalHits();
			SearchHit[] searchHits = hits.getHits();
	        if (totalHits >= 1) {
        		Map<String, Object> sourceAsMap = searchHits[0].getSourceAsMap();
        		if (sourceAsMap != null) {
	        		contact.setId(searchHits[0].getId());
	        		contact.setName((String) sourceAsMap.get("name"));
	        		contact.setPhone(Long.parseLong(sourceAsMap.get("phone").toString()));
	        		contact.setEmail((String) sourceAsMap.get("email"));
	        		contact.setAddress((String) sourceAsMap.get("address"));	        			
        		}
	        }
        }
        rhlClient.close();
        return contact;
	}

	// Deleting document under "contact" index with given unique name.
    // Delete is successful if deleteCount >= 1.
	public boolean deleteContact(String name) throws IOException {
		RestClient restClient = createElasticRestClient();
		checkIndex(restClient);
		int total = isContact(restClient, name);
		if (total >= 1) {
			Response deleteResponse = restClient.performRequest(
	    			"POST", 
	    			"/contact/data/_delete_by_query?q=name:"+name+"&filter_path=deleted"
	    			);
	    	String jsonString = EntityUtils.toString(deleteResponse.getEntity());
	    	Gson gson = new Gson();
	    	JsonObject obj = gson.fromJson(jsonString, JsonObject.class).getAsJsonObject();
	        int deleteCount = obj.get("deleted").getAsInt();
	        restClient.close();
	        return (deleteCount >= 1);
		}
        restClient.close();
		return false;
	}

	// Updating document under "contact" index for given unique name.
	public boolean updateContact(String name, Contact contact) throws IOException {
		RestClient restClient = createElasticRestClient();
		checkIndex(restClient);
        int total = isContact(restClient, name);
        
        if (total >= 1) {
        	Contact existingContact = getContact(name);
        	
        	if (contact.getName() != null) {
        		existingContact.setName(contact.getName());
        	}
        	existingContact.setPhone(contact.getPhone());
        	if (contact.getEmail() != null) {
            	existingContact.setEmail(contact.getEmail());
        	}
        	if (contact.getAddress() != null) {
            	existingContact.setAddress(contact.getAddress());
        	}
        	
        	Map<String, String> putParams = Collections.emptyMap();
        	String jsonString = new Gson().toJson(existingContact, Contact.class);
        	jsonString = "{"+jsonString.substring(jsonString.indexOf(",")+1);
        	HttpEntity putEntity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
        	restClient.performRequest("PUT", "/contact/data/"+existingContact.getId(), putParams, putEntity);
        	restClient.close();
        	return true;
        }
        restClient.close();
        return false;
	}

	// Creating document under "contact" index, given that name is unique.
	public boolean createContact(Contact contact) throws IOException {
		RestClient restClient = createElasticRestClient();
		checkIndex(restClient);
        int total = isContact(restClient, contact.getName());
        
        if (total <=0) {
        	Map<String, String> postParams = Collections.emptyMap();
        	HttpEntity postEntity = new NStringEntity(new Gson().toJson(contact, Contact.class),
        												ContentType.APPLICATION_JSON);
        	restClient.performRequest("POST", "/contact/data", postParams, postEntity);
        	restClient.close();
        	return true;
        }
        restClient.close();
    	return false;
	}

    // Create elasticsearch REST client
	public RestClient createElasticRestClient() throws IOException {
		ReadConfigFile readC = new ReadConfigFile();
    	Properties p = readC.getElasticConfig();
    	String hostURL = p.getProperty("hosturl");
		int port = Integer.parseInt(p.getProperty("port"));
		String username = p.getProperty("username");
		String password = p.getProperty("password");
		
    	final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        
        RestClient restClient = RestClient.builder(new HttpHost(hostURL, port, "http"))
        				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
        					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder arg0) {                                    
        						return arg0.setDefaultCredentialsProvider(credentialsProvider);
        					}
        				})
        				.build();
		return restClient;
	}

    // Create elasticsearch High level REST client
	public RestHighLevelClient createElasticRestHighLevelClient() throws IOException {
		ReadConfigFile readC = new ReadConfigFile();
    	Properties p = readC.getElasticConfig();
    	String hostURL = p.getProperty("hosturl");
		int port = Integer.parseInt(p.getProperty("port"));
		String username = p.getProperty("username");
		String password = p.getProperty("password");
		
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        
		RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
				RestClient.builder(new HttpHost(hostURL, port, "http"))
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
				})
		        );
		
		return restHighLevelClient;
	}
	
	// Check if index="contact" exists. Create if not present.
	private void checkIndex(RestClient restClient) throws IOException {
		Response testIndexResponse = restClient.performRequest("HEAD", "/contact/");
		if (testIndexResponse.getStatusLine().getStatusCode() == 404) {
			HttpEntity entity = new NStringEntity(
					"{\n" +
					"    \"name\" : \"test123\",\n" +                                      
					"    \"phone\" : \"1000000000\"\n" +
					"    \"email\" : \"test@gmail.com\"\n" +
					"    \"address\" : \"Phoenix, Arizona\"\n" +
					"}", ContentType.APPLICATION_JSON);
			
			restClient.performRequest("PUT","/contact/data/1",Collections.<String, String>emptyMap(),entity);
		}
	}
	
	// Check if document exists under "contact" index with given unique name.
    // If total >= 1, then document exists.
	private int isContact(RestClient restClient, String name) throws IOException {
    	Response testResponse = restClient.performRequest(
    			"GET", 
    			"/contact/data/_search?q=name:"+name+"&filter_path=hits.total"
    			);
    	String jsonString = EntityUtils.toString(testResponse.getEntity());
    	Gson gson = new Gson();
    	JsonObject obj = gson.fromJson(jsonString, JsonObject.class).getAsJsonObject();
        JsonObject hits = obj.getAsJsonObject("hits");
        int total = hits.get("total").getAsInt();
    	return total;
	}
}