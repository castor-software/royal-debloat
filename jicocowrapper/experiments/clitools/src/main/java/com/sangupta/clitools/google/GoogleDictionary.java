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

package com.sangupta.clitools.google;

import javax.inject.Inject;

import com.sangupta.clitools.CliTool;
import com.sangupta.clitools.WebInvoker;
import com.sangupta.clitools.core.CliToolsUtils;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.print.ConsoleTable;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.GsonUtils;
import com.sangupta.jerry.util.UriUtils;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.HelpOption;
import io.airlift.airline.SingleCommand;

/**
 * Return the meaning of the word via Google Dictionary
 * 
 * @author sangupta
 *
 */
@Command(name = "gd", description = "Google dictionary from command line")
public class GoogleDictionary implements Runnable, CliTool {
	
	@Inject
	public HelpOption helpOption;
	
	@Arguments(description = "the keyword to search for")
	public String keyword;

	public static void main(String[] args) {
		GoogleDictionary gd = SingleCommand.singleCommand(GoogleDictionary.class).parse(args);
		if(gd.helpOption.showHelpIfRequested()) {
			return;
		}
		
		gd.run();
	}
	
	public void run() {
		if(AssertUtils.isEmpty(this.keyword)) {
			Help.help(this.helpOption.commandMetadata);
			return;
		}
		
		String key = CliToolsUtils.getUserLocalStore().get("google.dict.api.key");
		if(AssertUtils.isEmpty(key)) {
			System.out.println("Set up 'google.dict.api.key' before executing the tool");
			return;
		}
		
		String url = "https://www.googleapis.com/scribe/v1/research?key=" + key + "&dataset=dictionary&dictionaryLanguage=en&query=" 
						+ UriUtils.encodeURIComponent(this.keyword);
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
			System.out.println("Unable to handle server response!");
			return;
		}
		
		System.out.println("Google Dictionary Results:\n");
		
		ConsoleTable table = new ConsoleTable();
		table.setColumnSize(0, 2);
		table.setColumnSeparator(" ");
		
		if(AssertUtils.isNotEmpty(result.data)) {
			for(Data data : result.data) {
				if(data.dictionary != null && AssertUtils.isNotEmpty(data.dictionary.definitionData)) {
					for(DefinitionData dd : data.dictionary.definitionData) {
						table.addRow("", dd.word + " (" + dd.pos + ")");
						if(AssertUtils.isNotEmpty(dd.meanings)) {
							for(Meaning m : dd.meanings) {
								table.addRow("", "\t* " + m.meaning);
							}
						}
						
						// blank row
						table.addRow("");
					}
				}
			}
		}
		
		table.write(System.out);
	}
	
	private static class SearchResult {
		
		public Data[] data;
		
	}
	
	private static class Data {
		
		public Dictionary dictionary;
		
	}
	
	private static class Dictionary {
		
		public DefinitionData[] definitionData;
		
	}
	
	private static class DefinitionData {
		
		public String word;
		
		public String pos;
		
		public Meaning[] meanings;
		
	}
	
	private static class Meaning {
		
		public String meaning;
		
	}
}