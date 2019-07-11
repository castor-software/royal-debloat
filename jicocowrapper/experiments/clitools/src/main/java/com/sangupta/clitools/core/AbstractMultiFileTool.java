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

import io.airlift.airline.Option;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.util.FileUtils;

public abstract class AbstractMultiFileTool implements CliTool {
	
	@Option(name = { "--recursive", "-r" }, description = "Scan child folders as well" )
	private boolean recursive;

	public void execute(String[] args) {
		preProcess();
		
		if(args.length == 0) {
			System.out.println("Provide the name(s) of files");
			return;
		}
		
		// read a list of all files that need to be worked upon
		for(String arg : args) {
			List<File> files = FileUtils.listFiles(new File(".").getAbsoluteFile(), arg, this.recursive);
		
			if(files == null) {
				System.out.println("No file found");
				return;
			}
			
			if(files.size() == 1) {
				File file = files.get(0);
				if(!file.exists()) {
					System.out.println("No file found");
					return;
				}
				
				if(file.isDirectory()) {
					System.out.println("File is a folder");
					return;
				}
			}
			
			boolean cont = true;
			for(File file : files) {
				
				try {
					cont = processFile(file);
				} catch(IOException e) {
					// unable to process file
					e.printStackTrace();
				}
				
				if(!cont) {
					break;
				}
			}
		}
		
		postProcess();
	}
	
	/**
	 * The worker method that processes the file from the list of arguments. Must return <code>true</code>
	 * if the next file needs to be processed, or <code>false</code> if execution needs to break right away.
	 * 
	 * @param file
	 * @return
	 */
	protected abstract boolean processFile(File file) throws IOException;
	
	/**
	 * Method that is invoked before any file starts getting processed.
	 * 
	 */
	protected void preProcess() {
		
	}
	
	/**
	 * Method that is invoked once all the files have been processed.
	 * 
	 */
	protected void postProcess() {
		
	}
}