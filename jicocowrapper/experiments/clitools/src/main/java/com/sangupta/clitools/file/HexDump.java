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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.ConsoleUtils;

@Command(name = "hex", description = "Dump a given file as hex")
public class HexDump implements CliTool {
	
	@Inject
	private HelpOption helpOption;
	
	@Option(name = { "--outfile" , "-of" }, description = "Save output as file")
	private boolean outputAsFile;
	
	@Arguments(description = "The file to display as hex")
	private String filePath;
	
	public static void main(String[] args) {
		if(AssertUtils.isEmpty(args)) {
			args = new String[] { "--help" };
		}
		
		HexDump hex = SingleCommand.singleCommand(HexDump.class).parse(args);
		if(hex.helpOption.showHelpIfRequested()) {
			return;
		}
		
		hex.execute();
	}
	
	private void execute() {
		File file = new File(this.filePath);

		InputStream is = null;
		BufferedInputStream bis = null;
		
		PrintStream outStream = null;
		try {
			is = new FileInputStream(file);
			bis = new BufferedInputStream(is);
			
			if(this.outputAsFile) {
				outStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(file.getAbsolutePath() + ".hex"))));
			} else {
				outStream = System.out;
			}
			
			int currentRow = 0;
			do {
				hexDump(outStream, bis, currentRow, 16);
				currentRow += 16;
				
				if(bis.available() <= 0) {
					break;
				}

				if(!this.outputAsFile) {
					// ask user to continue or exit
					String input = ConsoleUtils.readLine(":", true);
					if("q".equalsIgnoreCase(input)) {
						break;
					}
				}
			} while(true);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(bis);
			if(this.outputAsFile) {
				IOUtils.closeQuietly(outStream);
			}
		}
	}
	
	public static void hexDump(PrintStream outStream, BufferedInputStream bis, int currentRow, int maxRows) throws IOException {
		int row = currentRow + 1;
		if(maxRows == 0) {
			maxRows = Integer.MAX_VALUE;
		} else {
			maxRows += currentRow;
		}
		
		StringBuilder builder1 = new StringBuilder(100);
		StringBuilder builder2 = new StringBuilder(100);
		
		while (bis.available() > 0) {
			outStream.printf("%04X  ", row * 16);
			for (int j = 0; j < 16; j++) {
				if (bis.available() > 0) {
					int value = (int) bis.read();
					builder1.append(String.format("%02X ", value));
					
					if (!Character.isISOControl(value)) {
						builder2.append((char) value);
					} else {
						builder2.append(".");
					}
				} else {
					for (; j < 16; j++) {
						builder1.append("   ");
					}
				}
			}
			outStream.print(builder1);
			outStream.println(builder2);
			row++;
			
			if(row > maxRows) {
				break;
			}
			
			builder1.setLength(0);
			builder2.setLength(0);
		}
	}
}
