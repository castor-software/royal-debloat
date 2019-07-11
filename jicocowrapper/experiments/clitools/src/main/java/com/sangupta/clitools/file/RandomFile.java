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

package com.sangupta.clitools.file;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.ReadableUtils;

/**
 * Generate a random file of given size.
 * 
 * @author sangupta
 *
 */
@Command(name = "randfile", description = "Generate a random file of given size")
public class RandomFile implements CliTool {

	@Inject
	private HelpOption helpOption;
	
	@Option(name = { "--size", "-s" }, description = "The size of the file to generate, ex. 1600, 12m, 1g etc")
	private String size;
	
	@Arguments(description = "The path and name of the file that you want to generate")
	private String fileName;
	
	@Option(name = { "--text", "-t" }, description = "Use only textual characters as in Base64 encoding")
	private boolean onlyText;
	
	@Option(name = { "--fast", "-f" }, description = "Fast mode - use only one random chunk repeatedly")
	private boolean fastMode;
	
	private final SecureRandom random = new SecureRandom();
	
	private final char[] TEXT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
	
	public static void main(String[] args) {
		if(AssertUtils.isEmpty(args)) {
			args = new String[] { "--help" };
		}
		
		// parse and show help if needed
		RandomFile randomFile = SingleCommand.singleCommand(RandomFile.class).parse(args);
		if(randomFile.helpOption.showHelpIfRequested()) {
			return;
		}
		
		// run the command
		randomFile.generate();
	}
	
	public void generate() {
		if(AssertUtils.isEmpty(this.size)) {
			System.out.println("Size of file to be generated must be specified.");
			return;
		}
		
		if(AssertUtils.isEmpty(this.fileName)) {
			System.out.println("Filename of file to be generated must be specified.");
			return;
		}
		
		int bytes = (int) ReadableUtils.parseByteCount(this.size);
		if(bytes == 0) {
			System.out.println("Cannot generate a random file of size zero, use touch for the same");
			return;
		}
		
		File file = new File(this.fileName);
		if(file.exists()) {
			if(file.isFile()) {
				if(!file.canWrite()) {
					System.out.println("File already exists... but no permissions to write to it.");
					return;
				}

				// display warning message
				System.out.println("File already exists... it is being overwritten!");
			}
		}
		
		final long start = System.currentTimeMillis();
		try {
			createFile(bytes, file);
		} finally {
			long end = System.currentTimeMillis();
			System.out.println("File written in " + ReadableUtils.getReadableTimeDuration(end - start));
		}
	}

	private void createFile(int bytes, File file) {
		final int bufferSize = 2048;
		if(bytes < bufferSize) {
			byte[] data = new byte[bytes];
			fillRandomData(data);
			try {
				FileUtils.writeByteArrayToFile(file, data);
			} catch (IOException e) {
				System.out.println("Unable to write file to disk.");
				e.printStackTrace();
			}
			
			return;
		}
		
		final long chunks = bytes / bufferSize;
		byte[] data = new byte[bufferSize];
		
		FileOutputStream fileStream = null;
		BufferedOutputStream stream = null;
		try {
			fileStream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fileStream);
			
			for(int ch = 0; ch < chunks; ch++) {
				if(!this.fastMode) {
					fillRandomData(data);
				}
				
				stream.write(data);
			}
			
			// last min chunk
			int pending = bytes % bufferSize;
			if(pending == 0) {
				return;
			}
			
			data = new byte[pending];
			fillRandomData(data);
			stream.write(data);
		} catch (FileNotFoundException e) {
			// this should never happen
		} catch(IOException e) {
			System.out.println("Unable to write file to disk.");
			e.printStackTrace();
			return;
		} finally {
			IOUtils.closeQuietly(stream);
			IOUtils.closeQuietly(fileStream);
		}
	}
	
	/**
	 * Fill byte[] with random data
	 * 
	 * @param data
	 */
	private void fillRandomData(byte[] data) {
		if(this.onlyText) {
			for(int i = 0; i < data.length; i++) {
				data[i] = (byte) TEXT[this.random.nextInt(64)];
			}
			
			return;
		}
		
		// use entire byte space
		for(int i = 0; i < data.length; i++) {
			data[i] = (byte) this.random.nextInt(256);
		}
	}
	
}
