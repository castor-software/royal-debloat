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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.inject.Inject;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.FileUtils;

@Command(name = "format", description = "Format the file with proper white spaces and indentation")
public class Format implements CliTool {
	
	@Option(name = { "--overwrite", "-o" }, description = "Overwrite existing file with newer contents")
	private boolean overwrite;
	
	@Arguments
	private String filePath;
	
	@Inject
	private HelpOption helpOption;
	
	public static void main(String[] args) {
		if(AssertUtils.isEmpty(args)) {
			args = new String[] { "--help" };
		}
		
		Format format = SingleCommand.singleCommand(Format.class).parse(args);
		if(format.helpOption.showHelpIfRequested()) {
			return;
		}
		
		format.execute();
	}

	private void execute() {
		File file = new File(this.filePath);
		String ext = FileUtils.getExtension(file);
		if(AssertUtils.isEmpty(ext)) {
			System.out.println("Don't know how to format this file... add a file extension");
			return;
		}
		
		ext = ext.toLowerCase();
		switch(ext) {
			case "xml":
				formatXML(file);
				return;
				
			case "json":
				formatJSON(file);
					return;
		}
		
		System.out.println("Formatter for file extension: " + ext + " not available!");
	}

	private void formatJSON(File file) {
		String json = null;
		try {
			json = org.apache.commons.io.FileUtils.readFileToString(file);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(json);
		String out = new GsonBuilder().setPrettyPrinting().create().toJson(element);
		System.out.println(out);
	}

	private void formatXML(File file) {
		try {
			Source xmlInput = new StreamSource(file);
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
             
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            String out = xmlOutput.getWriter().toString();
            
            // add a new line after "?>< for next tag starting
            int index = out.indexOf("\"?><");
            if(index > 0) {
            	out = out.substring(0, index + 3) + "\n" + out.substring(index + 3);
            }
            
            if(!this.overwrite) {
            	System.out.println(out);
            	return;
            }
            
            org.apache.commons.io.FileUtils.writeStringToFile(file, out);
		} catch(Exception e) {
			System.out.println("Unable to format file!");
			e.printStackTrace();
		}
	}
}
