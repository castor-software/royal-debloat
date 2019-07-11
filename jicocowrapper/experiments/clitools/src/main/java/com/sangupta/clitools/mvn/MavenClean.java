package com.sangupta.clitools.mvn;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;

import javax.inject.Inject;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.FileUtils;

import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

@Command(name = "mvnclean", description = "Clean erroroneous Maven artifacts")
public class MavenClean implements Runnable, CliTool {

	@Inject
	public HelpOption helpOption;
	
	@Option(name = { "--remove", "-r" })
	private boolean remove;
	
	public static void main(String[] args) {
		MavenClean mavenClean = SingleCommand.singleCommand(MavenClean.class).parse(args);
		
		if(mavenClean.helpOption.showHelpIfRequested()) {
			return;
		}
		
		mavenClean.run();
	}
	
	public void run() {
		System.out.println("Finding maven artifacts...");
		
		List<File> files = FileUtils.listFiles(FileUtils.resolveToFile("~/.m2"), "*.jar", true);
		if(AssertUtils.isEmpty(files)) {
			System.out.println("No maven artifacts found, nothing to do!");
			return;
		}
		
		System.out.println("Found " + files.size() + " artifacts, starting to validate...");
		
		int invalid = 0;
		for(File file : files) {
			boolean valid = this.validateZIP(file);
			if(!valid) {
				System.out.println("Found invalid JAR file: " + file.getAbsoluteFile().getAbsolutePath());
				invalid++;
				
				if(this.remove) {
					System.out.println("Removing file: " + file.getAbsoluteFile().getAbsolutePath());
					org.apache.commons.io.FileUtils.deleteQuietly(file);
				}
			}
		}
		
		System.out.println("Total invalid artifacts found: " + invalid);
	}

	private boolean validateZIP(File file) {
		ZipFile zipfile = null;
	    try {
	        zipfile = new ZipFile(file);
	        return true;
	    } catch (IOException e) {
	        return false;
	    } finally {
	        try {
	            if (zipfile != null) {
	                zipfile.close();
	                zipfile = null;
	            }
	        } catch (IOException e) {
	        }
	    }
	}
	
}
