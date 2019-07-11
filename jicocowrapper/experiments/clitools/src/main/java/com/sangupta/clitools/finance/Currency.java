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

package com.sangupta.clitools.finance;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.google.gson.FieldNamingPolicy;
import com.sangupta.clitools.CliTool;
import com.sangupta.clitools.WebInvoker;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.GsonUtils;
import com.sangupta.jerry.util.StringUtils;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.SingleCommand;

@Command(name = "curr", description = "Currency converter")
public class Currency implements Runnable, CliTool {
	
	@Inject
	private HelpOption helpOption;
	
	@Arguments
	private List<String> arguments;
	
	public static void main(String[] args) {
		Currency currency = SingleCommand.singleCommand(Currency.class).parse(args);
		
		if(currency.helpOption.showHelpIfRequested()) {
			return;
		}
		
		currency.run();
	}

	@Override
	public void run() {
		if(AssertUtils.isEmpty(arguments) || arguments.size() != 2) {
			System.out.println("Two 3-letter currency codes required!");
			return;
		}
		
		String curr1 = arguments.get(0).toUpperCase();
		String curr2 = arguments.get(1).toUpperCase();
		
		String url = "http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json";
		WebResponse response = WebInvoker.getResponse(url);
		if(response == null) {
			System.out.println("Unable to connect to internet to fetch currency rates!");
			return;
		}
		
		if(!response.isSuccess()) {
			System.out.println("Invalid server response!");
			return;
		}
		
		YahooResponse yr = GsonUtils.getGson(FieldNamingPolicy.IDENTITY).fromJson(response.getContent(), YahooResponse.class);
		if(yr == null) {
			System.out.println("Unable to decipher server response!");
			return;
		}
		
		String curr = curr1 + "/" + curr2;
		for(Resource resource : yr.list.resources) {
			if(curr.equals(resource.resource.fields.name)) {
				System.out.println("Name: " + curr);
				System.out.println("Rate: " + resource.resource.fields.price);
				System.out.println("Timestamp: " + resource.resource.fields.ts);
				long millis = StringUtils.getLongValue(resource.resource.fields.ts, 0) * 1000l;
				if(millis > 0) {
					System.out.println("Time: " + new Date(millis).toString());
				}
				
				return;
			}
		}
	}
	
	private static class YahooResponse {
		
		public ResponseList list;
		
	}
	
	private static class ResponseList {
		
		public Meta meta;
		
		public Resource[] resources;
		
	}
	
	private static class Meta {
		
		public String type;
		
		public int start;
		
		public int count;
	}
	
	private static class Resource {
		
		public ResourceInternal resource;
		
	}
	
	private static class ResourceInternal {
		
		public String className;
		
		public Fields fields;
		
	}
	
	private static class Fields {
		
		public String name;
		
		public String price;
		
		public String symbol;
		
		public String type;
		
		public String ts;
		
		public String utctime;
		
		public String volume;
		
	}
}
