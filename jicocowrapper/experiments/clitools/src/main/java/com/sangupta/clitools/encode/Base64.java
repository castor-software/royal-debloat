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

package com.sangupta.clitools.encode;

import javax.inject.Inject;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.encoder.Base64Encoder;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.ConsoleUtils;

/**
 * Base64 encoder/decoder for the command line.
 * 
 * @author sangupta
 *
 */
@Command(name = "base64", description = "Base64 encode/decode")
public class Base64 implements CliTool {
	
	@Option(name = { "--encode", "-e" }, description = "Encode mode")
	private boolean encode;
	
	@Option(name = { "--decode", "-d" }, description = "Decode mode")
	private boolean decode;
	
	@Inject
	private HelpOption helpOption;
	
	@Arguments(description = "The string to encode/decode")
	private String text;
	
	public static void main(String[] args) {
		if(AssertUtils.isEmpty(args)) {
			args = new String[] { "--help" };
		}
		
		Base64 base64 = SingleCommand.singleCommand(Base64.class).parse(args);
		if(base64.helpOption.showHelpIfRequested()) {
			return;
		}
		
		base64.execute();
	}
	
	private void execute() {
		if(this.encode && this.decode) {
			System.out.println("Only one mode must be selected: encode or decode");
			return;
		}
		
		if(!(this.encode || this.decode)) {
			System.out.println("One mode must be selected: encode or decode");
			return;
		}

		if(AssertUtils.isEmpty(this.text)) {
			this.text = ConsoleUtils.readLine(true);
			if(AssertUtils.isEmpty(this.text)) {
				System.out.println("Nothing to process!");
				return;
			}
		}
		
		// separator line
		System.out.println();
		
		if(this.encode) {
			String enc = Base64Encoder.encodeToString(this.text.getBytes(), true);
			System.out.println(enc);
			return;
		}
		
		if(this.decode) {
			byte[] dec = Base64Encoder.decode(this.text);
			String decoded = new String(dec);
			System.out.println(decoded);
			return;
		}
		
		throw new IllegalStateException("This code must never be reached");
	}
}
