package com.manoh.coding.addressbook_api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadConfigFile {
	public Properties getElasticConfig() throws IOException {
		Properties props = new Properties();
		try {
		    InputStream fileStream = new FileInputStream(new File("src/main/resources/config.properties"));
		    props.load(fileStream);
		} 
		catch (FileNotFoundException e) {
		} 
		catch (IOException e) {
		}
		return props;
	}
}
