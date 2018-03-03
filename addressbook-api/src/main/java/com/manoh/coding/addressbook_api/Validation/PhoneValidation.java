package com.manoh.coding.addressbook_api.Validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidation {
    Pattern pattern;
    Matcher matcher;
    
    private static final String PHONE_PATTERN = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";
    
    public PhoneValidation() {
		pattern = Pattern.compile(PHONE_PATTERN);
	}

    public boolean validate(final long phone) {
		matcher = pattern.matcher(Long.toString(phone));
		return matcher.matches();
	}
}
