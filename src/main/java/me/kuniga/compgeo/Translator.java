package me.kuniga.compgeo;

import java.util.Locale;
import java.util.ResourceBundle;

public class Translator {

	ResourceBundle messages = null;

	/**
	 * Loads a bundle with localized messages
	 * 
	 * @param language
	 * @param country
	 * @return
	 */
	public Translator(String language, String country){
		
		// Default values
		if(language == null)
			language = "en";
		if(country == null)
			country = "US";
		
		Locale locale = new Locale(language, country);
		messages = ResourceBundle.getBundle("MessagesBundle", locale);
	};
	
	public String localize(String original){
		return messages.getString(original);
	}
	
	
}
