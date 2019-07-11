/**
 *
 * clitools - Simple command line tools
 * Copyright (c) 2014-2015, Sandeep Gupta
 * 
 * http://sangupta.com/projects/clitools
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.sangupta.clitools.net;

import java.util.Map;
import java.util.Map.Entry;

import com.sangupta.clitools.CliTool;
import com.sangupta.clitools.WebInvoker;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.UriUtils;

import io.airlift.airline.Command;

/**
 * Display response headers for the given url.
 * 
 * @author sangupta
 *
 */
@Command(name = "headers", description = "Display response headers for a given URL")
public class Headers implements CliTool {

	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("URL is a must");
			return;			
		}
		
		Map<String, String> headers;
		
		if(args.length == 1) {
			String url = UriUtils.normalizeUrl(args[0]);
			headers = WebInvoker.getHeaders(url, false);
			outputHeaders(headers);
			return;
		}
		
		if(args.length == 2) {
			String option = args[0];
			String url = UriUtils.normalizeUrl(args[1]);
			
			if("-l".equalsIgnoreCase(option)) {
				// follow redirects
				headers = WebInvoker.getHeaders(url, true);
				outputHeaders(headers);
				return;
			}
			
			if("-v".equalsIgnoreCase(option)) {
				// verbose
				
				boolean isRedirect = false;
				WebResponse response;
				
				do {
					isRedirect = false;
					response = WebInvoker.headRequest(url, false);
					
					headers = response.getHeaders();
					outputHeaders(headers);

					// check the response code to indicate a redirect
					int code = response.getResponseCode();
					if(code == 301 || code == 307) {
						for(Entry<String, String> header : headers.entrySet()) {
							if(header.getKey().equalsIgnoreCase("location")) {
								// found a location header
								isRedirect = true;
								
								url = header.getValue();
								System.out.print("\n--- ");
								System.out.println("Redirected to: " + url);
								break;
							}
						}
					}
				} while(isRedirect);
			}
		}
	}

	/**
	 * @param headers
	 */
	private static void outputHeaders(Map<String, String> headers) {
		if(AssertUtils.isEmpty(headers)) {
			System.out.println("No response headers.");
			return;
		}
		
		for(Entry<String, String> header : headers.entrySet()) {
			System.out.println(header.getKey() + ": " + header.getValue());
		}		
	}

}