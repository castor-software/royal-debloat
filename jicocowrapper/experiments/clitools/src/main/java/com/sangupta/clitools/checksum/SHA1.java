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

package com.sangupta.clitools.checksum;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.HelpOption;
import io.airlift.airline.SingleCommand;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.sangupta.clitools.core.AbstractMultiFileTool;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.StringUtils;

/**
 * @author sangupta
 *
 */
@Command(name = "sha1", description = "Compute the SHA1 hash of given file/file pattern(s)")
public class SHA1 extends AbstractMultiFileTool {
	
	@Inject
	private HelpOption helpOption;
	
	@Arguments(description = "File(s)/file pattern(s) to work upon")
	private List<String> arguments;

	public static void main(String[] args) {
		SHA1 sha1 = SingleCommand.singleCommand(SHA1.class).parse(args);
		
		if(sha1.helpOption.showHelpIfRequested()) {
			return;
		}
		
		if(AssertUtils.isEmpty(sha1.arguments)) {
			Help.help(sha1.helpOption.commandMetadata);
			return;
		}
		
		sha1.execute(sha1.arguments.toArray(StringUtils.EMPTY_STRING_LIST));
	}
	
	protected boolean processFile(File file) throws IOException {
		if(file.isDirectory()) {
			return true;
		}
		
		byte[] bytes = FileUtils.readFileToByteArray(file);
		try {
	        java.security.MessageDigest md = java.security.MessageDigest.getInstance(getAlgorithmName());
	        byte[] array = md.digest(bytes);
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < array.length; ++i) {
	          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
	       }
	        
	        System.out.println(sb.toString() + " *" + file.getName());
	    } catch (java.security.NoSuchAlgorithmException e) {
	    	// do nothing
	    	System.out.println("No " + getAlgorithmName() + " implementation available");
	    	return false;
	    }
		
		return true;
	}
	
	protected String getAlgorithmName() {
		return "sha-1";
	}

}