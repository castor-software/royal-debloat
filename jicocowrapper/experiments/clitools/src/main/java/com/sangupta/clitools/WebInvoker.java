package com.sangupta.clitools;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.config.CookieSpecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sangupta.jerry.http.WebRequest;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.jerry.http.service.impl.DefaultHttpServiceImpl;
import com.sangupta.jerry.util.DateUtils;

public class WebInvoker {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebInvoker.class);
	
	private static final HttpService HTTP_SERVICE = new DefaultHttpServiceImpl();
	
	/**
	 * Value to be used for connection timeout
	 */
	private static int connectionTimeout = (int) DateUtils.ONE_MINUTE;
	
	/**
	 * Value to be used for socket timeout
	 */
	private static int socketTimeout = (int) DateUtils.ONE_MINUTE;
	
	/**
	 * The cookie policy to use
	 */
	private static String cookiePolicy = CookieSpecs.DEFAULT;

	public static WebResponse getResponse(String url) {
		return HTTP_SERVICE.getResponse(url);
	}

	public static String fetchResponse(String url) {
		return HTTP_SERVICE.getTextResponse(url);
	}

	public static Map<String, String> getHeaders(String url, boolean b) {
		WebResponse response = getResponse(url);
		if(response == null) {
			return null;
		}
		
		return response.getHeaders();
	}

	public static WebResponse headRequest(String url, boolean followRedirects) {
		try {
			if(followRedirects) {
				return WebRequest.head(url).connectTimeout(connectionTimeout).socketTimeout(socketTimeout).cookiePolicy(cookiePolicy).followRedirects().execute().webResponse();
			}
			
			return WebRequest.head(url).connectTimeout(connectionTimeout).socketTimeout(socketTimeout).cookiePolicy(cookiePolicy).noRedirects().execute().webResponse();
		} catch(IOException e) {
			LOGGER.debug("Unable to fetch response headers from url: {}", url, e);
		}
		
		return null;
	}

}
