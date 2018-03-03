# AddressBookAPI

A RESTful API built on Spark for an address book with an Elasticsearch data store.

## Spark Configuration information

Spark API is running on port = 4567.

## Elasticsearch server Configuration

The host, port, username and password for Elasticsearch can be changed in the `config.properties` file present in `\src\main\resources`.

The existing values are below:
```
	hosturl=localhost
	
	port=9200
	
	username=elastic
	
	password=elastic
```

## API Definition

The endpoints (aka methods) available are:

1. GET /contact?pageSize={}&page={}&query={}

	Example URL: 
	`
	http://localhost:4567/contact?pageSize=2&page=0&query=+name:Tansen OR address:Phoenix, Arizona*
	`
	
2. POST /contact
	
	Example URL:
	`
	http://localhost:4567/contact
	`
	
	Example Json:
	
	```
	{
	"name":"Tansen",
	
	"phone": 1234567890,
	
	"email":"tansen@gmail.com",
	
	"address":"Phoenix, Arizona"
	}
	```
	
	
3. GET /contact/{name}
	
	Example URL:
	`
	http://localhost:4567/contact/Tansen
	`
	
4. PUT /contact/{name}
	
	Example URL:
	`
	http://localhost:4567/contact/Tansen
	`
	
	Example Json:
	
	```
	{
	"name":"Birbal",
	
	"phone": 1111111111,
	
	"email":"bribal@gmail.com",
	
	"address":"Phoenix, Arizona"
	}
	```
	
5. DELETE /contact/{name}
	
	Example URL:
	`
	http://localhost:4567/contact/Tansen
	`
	
