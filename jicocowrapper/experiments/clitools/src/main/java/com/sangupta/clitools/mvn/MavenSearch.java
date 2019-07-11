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

package com.sangupta.clitools.mvn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.sangupta.clitools.CliTool;
import com.sangupta.clitools.WebInvoker;
import com.sangupta.jerry.print.ConsoleTable;
import com.sangupta.jerry.print.ConsoleTable.ConsoleTableLayout;
import com.sangupta.jerry.print.ConsoleTableRow;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.GsonUtils;
import com.sangupta.jerry.util.UriUtils;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.HelpOption;
import io.airlift.airline.SingleCommand;

/**
 * Command line tool to search for maven artifacts
 * 
 * @author sangupta
 *
 */
@Command(name = "mvnsearch", description = "Search Maven artifacts")
public class MavenSearch implements Runnable, CliTool {
	
	private static final SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
	
	@Inject
	public HelpOption helpOption;
	
	@Arguments(description = "the keyword to search for")
	public String keyword;

	public static void main(String[] args) {
		MavenSearch mavenSearch = SingleCommand.singleCommand(MavenSearch.class).parse(args);
		
		if(mavenSearch.helpOption.showHelpIfRequested()) {
			return;
		}
		
		mavenSearch.run();
	}
	
	public void run() {
		if(AssertUtils.isEmpty(this.keyword)) {
			Help.help(this.helpOption.commandMetadata);
			return;
		}
		
		String uri = "http://search.maven.org/solrsearch/select?rows=10&wt=json&q=" + UriUtils.encodeURIComponent(this.keyword);
		String response = WebInvoker.fetchResponse(uri);
		if (AssertUtils.isEmpty(response)) {
			System.out.println("Unable to fetch response from Maven central");
			return;
		}

		MavenSearchResults results = GsonUtils.getGson().fromJson(response, MavenSearchResults.class);
		System.out.print("Total results found: " + results.response.numFound);
		System.out.println("; starting at: " + results.response.start);

		// one new line
		System.out.println();
		
		int count = 0;
		
		ConsoleTable table = new ConsoleTable(ConsoleTableLayout.MULTI_LINE);
		table.addHeaderRow("S.No.", "GroupId", "ArtifactId", "Version", "Updated", "Download");
		
		for (Doc document : results.response.docs) {
			ConsoleTableRow row = table.addRow(++count, document.g, document.a, document.latestVersion + ", All(" + document.versionCount + ")", format.format(new Date(document.timestamp)));
			
			if (AssertUtils.isNotEmpty(document.ec)) {
				String value = "";
				for (String ec : document.ec) {
					value += ec.substring(1) + " ";
				}
				
				row.addColumn(value);
			}
		}
		
		// fix sizes
		table.setColumnSize(1, 20);
		table.setColumnSize(2, 15);
		table.setColumnSize(4, 15);
		table.setColumnSize(5, 15);
		table.write(System.out);
	}

	private static class MavenSearchResults {

		private Response response;
	}

	private static class Response {

		public int numFound;

		public int start;

		public List<Doc> docs;
	}

	private static class Doc {

		// neverused variable
		// public String id;

		public String g;

		public String a;

		public String latestVersion;

		public long timestamp;

		public int versionCount;

		public List<String> ec;
	}
}