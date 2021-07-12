package com.davcamalv.filmApp.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Utils {

	public static boolean isValidEmail(String email) {
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static String makeSafeMessage(String message) {
		return message.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;")
				.replace("&", "&amp;");
	}
	
	public static WebDriver createWebDriver() {
		ChromeOptions options = new ChromeOptions();
		options.setHeadless(true);
		options.addArguments("window-size=1920,1080");
		WebDriver webDriver = new ChromeDriver(options);
		return webDriver;
	}
	
	public static String createHtmlTag(String tagName, String value, Map<String, String> attributes) {
		String res = "<" + tagName;
		for (Entry<String, String> attribute : attributes.entrySet()) {
			res = res + " " +  attribute.getKey() + "='" + attribute.getValue() + "'";
		}
		res = res + ">" + value + "</" + tagName +">";
		return res;
	}
}
