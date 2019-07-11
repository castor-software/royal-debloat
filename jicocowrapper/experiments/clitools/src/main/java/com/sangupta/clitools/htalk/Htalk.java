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

package com.sangupta.clitools.htalk;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.gson.annotations.SerializedName;
import com.sangupta.clitools.CliTool;
import com.sangupta.clitools.WebInvoker;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.print.ConsoleTable;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.ConsoleUtils;
import com.sangupta.jerry.util.DesktopUtils;
import com.sangupta.jerry.util.GsonUtils;
import com.sangupta.jerry.util.StringUtils;
import com.sangupta.jerry.util.UriUtils;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

/**
 * A simple command line tool to search HackerNews (http://news.ycombinator.com) submissions
 * via the Algolia HN search API.
 * 
 * @author sangupta
 *
 */
@Command(name = "htalk", description = "Search hackernews threads")
public class Htalk implements Runnable, CliTool {
	
	@Inject
	public HelpOption helpOption;
	
	@Option(name = { "-s", "--sort" }, description = "Sort option: rel (by relevancy, then points, then number of comment) or date (by date, most recent first), default is date"
			, allowedValues = { "date", "rel" })
	public String sortOption = "date";
	
	@Option(name = { "-f", "--filter" }, description = "Filtering option: one of story, comment, poll, pollopt, show_hn, ask_hn. Default is story"
			, allowedValues = { "story", "comment", "poll", "pollopt", "show_hn", "ask_hn" })
	public String filter = "story";
	
	@Arguments(description = "the URL or keyword to search on")
	public String urlOrKeyword;
	
	public static void main(String[] args) {
		Htalk htalk = SingleCommand.singleCommand(Htalk.class).parse(args);
		
		if(htalk.helpOption.showHelpIfRequested()) {
			return;
		}
		
		htalk.run();
	}

	@Override
	public void run() {
		if(AssertUtils.isEmpty(urlOrKeyword)) {
			Help.help(this.helpOption.commandMetadata);
			return;
		}
		
		doSearch();
	}
	
	/**
	 * Run the search
	 * 
	 */
	private void doSearch() {
		String url;
		if("date".equalsIgnoreCase(this.sortOption)) {
			url = "https://hn.algolia.com/api/v1/search_by_date?query=";
		} else {
			url = "https://hn.algolia.com/api/v1/search?query=";
		}
		
		// add query
		url += UriUtils.encodeURIComponent(urlOrKeyword);
		
		// get response
		WebResponse response = WebInvoker.getResponse(url);
		if(response == null) {
			System.out.println("Unable to hit the search API, the internet may be down!");
			return;
		}
		
		if(!response.isSuccess()) {
			System.out.println("Unable to search via the API, the search server may be down!");
			return;
		}
		
		SearchResult result = GsonUtils.getGson().fromJson(response.getContent(), SearchResult.class);
		if(result == null) {
			System.out.println("Unable to parse results returned from the server");
			return;
		}
		
		if(AssertUtils.isEmpty(result.hits)) {
			System.out.println("No search results found!");
			return;
		}
		
		displaySearchResults(result.hits);
	}

	/**
	 * Display search results in a table in a nice way
	 * 
	 * @param hits
	 */
	private void displaySearchResults(Hit[] hits) {
		hits = clearEmpty(hits);
		if(AssertUtils.isEmpty(hits)) {
			System.out.println("No search results found!");
			return;
		}
		
		ConsoleTable table = new ConsoleTable();
		table.addHeaderRow("S.No", "Point", "Comments", "Submitted", "Title");
		for(int index = 0; index < hits.length; index++) {
			Hit hit = hits[index];
			table.addRow(index, 0, hit.num_comments, "", hit.title);
		}
		
		table.write(System.out);
		
		String open = ConsoleUtils.readLine("\n\nEnter URL to open: ", true);
		if(AssertUtils.isEmpty(open)) {
			return;
		}
		
		int index = StringUtils.getIntValue(open, -1);
		if(index <= 0 || index > hits.length) {
			return;
		}
		
		try {
			DesktopUtils.openURL(new URI(hits[index].url));
		} catch (URISyntaxException e) {
			// eat up - its a valid url
		}
	}

	/**
	 * Clear any result where URL is not found
	 * 
	 * @param hits
	 * @return
	 */
	private Hit[] clearEmpty(Hit[] hits) {
		List<Hit> list = new ArrayList<Htalk.Hit>();
		for(Hit hit : hits) {
			if(AssertUtils.isNotEmpty(hit.url)) {
				list.add(hit);
			}
		}
		
		return list.toArray(new Hit[] { });
	}

	private static class SearchResult {
		
		private Hit[] hits;
		
	}
	
	private static class Hit {
		
		public String title;
		
		public String url;
		
		@SerializedName("num_comments")
		public int num_comments;
	}
	
}