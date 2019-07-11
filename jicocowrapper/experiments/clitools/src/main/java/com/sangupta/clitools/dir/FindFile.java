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

package com.sangupta.clitools.dir;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.FileUtils;

@Command(name = "findfile", description = "Find files in a directory")
public class FindFile implements Runnable, CliTool {
	
	@Inject
	private HelpOption helpOption;

	@Option(name = { "--recursive", "-r" }, description = "Search recursively in sub-folders")
	private boolean recursive;
	
	@Arguments(description = "The path where to look files in, wild-cards accepted. On Windows, enclose the wildcard path in double-quotes")
	private String path;
	
	public static void main(String[] args) {
		if(AssertUtils.isEmpty(args)) {
			args = new String[] { "--help" };
		}
		
		FindFile findFile = SingleCommand.singleCommand(FindFile.class).parse(args);
		
		if(findFile.helpOption.showHelpIfRequested()) {
			return;
		}
		
		findFile.run();
	}
	
	@Override
	public void run() {
		List<File> files = FileUtils.listFiles(this.path, this.recursive);
		if(AssertUtils.isEmpty(files)) {
			System.out.println("No matching file found!");
			return;
		}
		
		for(File file : files) {
			System.out.println(file);
		}
	}
}
