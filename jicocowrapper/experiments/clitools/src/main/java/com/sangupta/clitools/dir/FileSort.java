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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.sangupta.clitools.core.AbstractMultiFileTool;
import com.sangupta.jerry.util.AssertUtils;

/**
 * Sort files in a directory and rename them to prepend a number to signify the
 * same.
 * 
 * @author sangupta
 *
 */
@Command(name = "filesort", description = "Tool to sort files in a directory by prefixing numerals")
public class FileSort extends AbstractMultiFileTool {

	@Option(name = { "--descending", "-d" }, description = "Sort in descending order")
	private boolean descending;
	
	@Option(name = { "--on", "-o" }, description = "Sort on which field: date, size, name. Default is date")
	private String on = "date";

	@Arguments(required = true)
	private List<String> pattern;
	
	@Inject
	private HelpOption helpOption;
	
	/**
	 * Holds list of files that have to sorted and renamed
	 */
	private List<File> files = new ArrayList<>();
	
	public static void main(String[] args) {
		if(AssertUtils.isEmpty(args)) {
			args = new String[] { "--help" };
		}
		
		FileSort fileSort = SingleCommand.singleCommand(FileSort.class).parse(args);
		
		if(fileSort.helpOption.showHelpIfRequested()) {
			return;
		}
		
		fileSort.execute(fileSort.pattern.toArray(com.sangupta.jerry.util.StringUtils.EMPTY_STRING_LIST));
	}
	
	@Override
	protected boolean processFile(File file) throws IOException {
		files.add(file);
		return true;
	}

	@Override
	protected void postProcess() {
		// number of files
		final int num = files.size();
		
		// now sort these files
		Comparator<File> comparator;
		if("date".equalsIgnoreCase(this.on)) {
			comparator = new FileDateComparator();
		} else if("size".equalsIgnoreCase(this.on)) {
			comparator = new FileSizeComparator();
		} else if("name".equalsIgnoreCase(this.on)) {
			comparator = new FileNameComparator();
		} else {
			System.out.println("Unrecognized sort option: " + this.on);
			return;
		}
		
		// sort
		Collections.sort(this.files, comparator);
		
		// now depending on the asc/desc order rename
		if(this.descending) {
			Collections.reverse(this.files);
		}
		
		// iterate
		int count = 1;
		int maxStringSize = String.valueOf(num).length();
		for(File file : this.files) {
			File baseDir = file.getParentFile();
			String newName = format(maxStringSize, count++) + "-" + file.getName();
			System.out.println("Renaming file " + file.getAbsolutePath() + " to " + newName);
			file.renameTo(new File(baseDir, newName));
		}
	}
	
	private String format(int size, int value) {
		return StringUtils.leftPad(String.valueOf(value), size, '0');
	}

	private static class FileSizeComparator implements Comparator<File> {

		@Override
		public int compare(File o1, File o2) {
			final long l1 = o1.length();
			final long l2 = o2.length();
			
			if(l1 < l2) {
				return -1;
			}
			
			if(l2 > l1) {
				return 1;
			}
			
			return o1.getName().compareTo(o2.getName());
		}
		
	}
	
	private static class FileDateComparator implements Comparator<File> {

		@Override
		public int compare(File o1, File o2) {
			if(o1 == o2) {
				return 0;
			}
			
			if(o1.getAbsoluteFile().equals(o2.getAbsoluteFile())) {
				return 0;
			}
			
			final long l1 = o1.lastModified();
			final long l2 = o2.lastModified();
			
			if(l1 < l2) {
				return -1;
			}
			
			if(l1 > l2) {
				return 1;
			}

			return o1.getName().compareTo(o2.getName());
		}
		
	}
	
	private static class FileNameComparator implements Comparator<File> {

		@Override
		public int compare(File o1, File o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
}
