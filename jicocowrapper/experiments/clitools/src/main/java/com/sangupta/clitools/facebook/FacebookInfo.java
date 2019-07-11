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

package com.sangupta.clitools.facebook;

import javax.inject.Inject;

import com.google.gson.FieldNamingPolicy;
import com.sangupta.clitools.CliTool;
import com.sangupta.clitools.WebInvoker;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.print.ConsoleTable;
import com.sangupta.jerry.print.ConsoleTable.ConsoleTableLayout;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.GsonUtils;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.SingleCommand;

@Command(name = "fbinfo", description = "Show information about a facebook account or page")
public class FacebookInfo implements Runnable, CliTool {
	
	@Arguments(description = "the account or page to show information for")
	public String account;
	
	@Inject
	private HelpOption helpOption;
	
	public static void main(String[] args) {
		if(AssertUtils.isEmpty(args)) {
			args = new String[] { "--help" };
		}
		
		FacebookInfo fbinfo = SingleCommand.singleCommand(FacebookInfo.class).parse(args);
		if(fbinfo.helpOption.showHelpIfRequested()) {
			return;
		}
		
		fbinfo.run();
	}
	
	@Override
	public void run() {
		if(AssertUtils.isEmpty(this.account)) {
			System.out.println("Account or page name is required.");
			return;
		}
		
		String url = "https://graph.facebook.com/" + this.account.toLowerCase();
		WebResponse response = WebInvoker.getResponse(url);
		if(response ==  null) {
			System.out.println("Unable to fetch response from server");
			return;
		}
		
		if(!response.isSuccess()) {
			System.out.println("Non-success response from server as: " + response.trace());
			System.out.println("May be user/page does not exist!");
			return;
		}
		
		FacebookInfoResponse info = GsonUtils.getGson(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).fromJson(response.getContent(), FacebookInfoResponse.class);
		
		// start display
		
		ConsoleTable table = new ConsoleTable(ConsoleTableLayout.MULTI_LINE);
		table.setColumnSeparator("  " );
		table.addRow("User Name", nonNull(info.username));
		table.addRow("ID", nonNull(info.id));
		table.addRow("Name", nonNull(info.name));
		table.addRow("Link", nonNull(info.link));
		table.addRow("Website", nonNull(info.website));
		table.addRow("Talking About", info.talkingAboutCount);
		table.addRow("Likes", info.likes);
		table.addRow("Bio", nonNull(info.bio));
		table.addRow("About", nonNull(info.about));
		table.addRow("Category", nonNull(info.category));
		table.addRow("Hometown", nonNull(info.hometown));
		
		table.write(System.out);
	}

	private static class FacebookInfoResponse {
		
		String id;
		String about;
		String bio;
		String category;
		String hometown;
		long likes;
		String link;
		String name;
		String username;
		String website;
		long talkingAboutCount;
		
	}
	
	private static String nonNull(String arg) {
		if(arg == null) {
			return "";
		}
		
		return arg.trim();
	}
}