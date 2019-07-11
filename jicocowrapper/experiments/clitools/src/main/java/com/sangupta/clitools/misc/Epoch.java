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

package com.sangupta.clitools.misc;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.SingleCommand;

import java.sql.Date;

import javax.inject.Inject;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.StringUtils;

/**
 * Show the current time in epoch millis.
 * 
 * @author sangupta
 *
 */
@Command(name = "epoch", description = "Show current time as epoch, millis in GMT")
public class Epoch implements CliTool {
	
	@Inject
	private HelpOption helpOption;
	
	@Arguments(description = "the timestamp value to convert into time string")
	private String arguments;

	public static void main(String[] args) {
		if(AssertUtils.isEmpty(args)) {
			System.out.println(System.currentTimeMillis());
			return;
		}
		
		if(args.length > 1) {
			args = new String[] { "--help" };
		}
		
		long time = StringUtils.getLongValue(args[0], -1);
		if(time < 0) {
			args = new String[] { "--help" };
		}
		
		Epoch epoch = SingleCommand.singleCommand(Epoch.class).parse(args);
		if(epoch.helpOption.showHelpIfRequested()) {
			return;
		}
		
		epoch.execute(time);
	}

	private void execute(long time) {
		Date date = new Date(time);
		System.out.println("Local Date : " + date.toLocaleString());
		System.out.println("GMT Date   : " + date.toGMTString());
	}

}
