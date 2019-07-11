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

package com.sangupta.clitools.basic;

import com.sangupta.clitools.CliTool;

import io.airlift.airline.Command;

/**
 * Pause the shell for a given time
 * 
 * @author sangupta
 *
 */
@Command(name = "sleep", description = "Pause for NUMBER seconds")
public class Sleep implements CliTool {

	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Must provide a valid number of seconds to pause");
			return;
		}
		
		Long time = parseTimeInterval(args[0]);
		if(time == null) {
			System.out.println("Invalid time interval '" + args[0] + "'");
			return;
		}
		
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			// eat up
		}
	}

	/**
	 * Parses time interval from the given string argument
	 * and converts it into number of seconds.
	 * 
	 * @param string
	 * @return
	 */
	private static Long parseTimeInterval(String arg) {
		char c = arg.charAt(arg.length() - 1);
		c = Character.toLowerCase(c);
		if(c == 's' || c == 'm' || c == 'h' || c == 'd') {
			// parse the number
			arg = arg.substring(0, arg.length() - 1);
		}

		// try and parse it as a number
		long time;
		try {
			time = Long.parseLong(arg);
		} catch(NumberFormatException e) {
			return null;
		}
		
		switch(c) {
			case 's':
				return time;
				
			case 'm':
				return time * 60;
				
			case 'h':
				return time * 3600;
				
			case 'd':
				return time * 86400;
		}
		
		return time;
	}

}
