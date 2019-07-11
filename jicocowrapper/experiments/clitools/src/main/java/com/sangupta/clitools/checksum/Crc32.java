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
import java.util.zip.CRC32;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.sangupta.clitools.core.AbstractMultiFileTool;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.StringUtils;

/**
 * @author sangupta
 *
 */
@Command(name = "crc32", description = "Compute the CRC32 hash of given file/file pattern(s)")
public class Crc32 extends AbstractMultiFileTool {
	
	@Inject
	private HelpOption helpOption;
	
	@Arguments(description = "File(s)/file pattern(s) to work upon")
	private List<String> arguments;

	public static void main(String[] args) {
		Crc32 crc32 = SingleCommand.singleCommand(Crc32.class).parse(args);
		
		if(crc32.helpOption.showHelpIfRequested()) {
			return;
		}
		
		if(AssertUtils.isEmpty(crc32.arguments)) {
			Help.help(crc32.helpOption.commandMetadata);
			return;
		}
		
		crc32.execute(crc32.arguments.toArray(StringUtils.EMPTY_STRING_LIST));
	}
	
	/**
	 * @see com.sangupta.andruil.commands.AbstractAndruilCommand#execute(java.lang.String[])
	 */
	@Override
	protected boolean processFile(File file) throws IOException {
		if(file.isDirectory()) {
			return true;
		}
		
		byte[] bytes = FileUtils.readFileToByteArray(file);
		CRC32 crc32 = new CRC32();
		crc32.update(bytes);
		System.out.println(Long.toHexString(crc32.getValue()) + " *" + file.getName());
		
		return true;
	}

}