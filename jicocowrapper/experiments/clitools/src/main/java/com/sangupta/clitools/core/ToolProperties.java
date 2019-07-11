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

package com.sangupta.clitools.core;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.print.ConsoleTable;
import com.sangupta.jerry.print.ConsoleTable.ConsoleTableLayout;
import com.sangupta.jerry.store.UserLocalStore;
import com.sangupta.jerry.util.AssertUtils;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

/**
 * A simple tool to manage properties within the user local store.
 * 
 * @author sangupta
 *
 */
@Command(name = "prop", description = "Provides access to user properties")
public class ToolProperties implements CliTool {
	
	@Inject
	private HelpOption helpOption;
	
	@Option(name = { "-l", "--list" }, description = "List all currently stored properties")
	private boolean list = false;

	@Option(name = { "-r", "--remove" }, description = "Remove property referred by name")
	private boolean delete = false;
	
	@Arguments(title = "Property name and/or value")
	private List<String> arguments;
	
	public static void main(String[] args) {
		ToolProperties tp = SingleCommand.singleCommand(ToolProperties.class).parse(args);
		
		if(tp.helpOption.showHelpIfRequested()) {
			return;
		}
		
		tp.run();
	}
	
	public void run() {
		// read the store
		UserLocalStore localStore = CliToolsUtils.getUserLocalStore();
		
		// work up
		if(this.list) {
			Collection<String> keys = localStore.getAllKeys();
			if(AssertUtils.isEmpty(keys)) {
				System.out.println("No property stored within the data store");
				return;
			}
			
			ConsoleTable table = new ConsoleTable(ConsoleTableLayout.MULTI_LINE);
			table.addHeaderRow("Name", "Value");
			table.setColumnSize(0, 30);
			table.setColumnSize(1, 40);
			
			for(String key : keys) {
				table.addRow(key, localStore.get(key));
			}
			
			table.write(System.out);
			return;
		}
		
		if(AssertUtils.isEmpty(this.arguments)) {
			Help.help(this.helpOption.commandMetadata);
			return;
		}
		
		if(this.delete) {
			localStore.delete(arguments.get(0));
			return;
		}
		
		// add workflow
		if(arguments.size() != 2) {
			Help.help(this.helpOption.commandMetadata);
			return;
		}
		
		localStore.put(arguments.get(0), arguments.get(1));
	}
}