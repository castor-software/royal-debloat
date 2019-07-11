package com.sangupta.clitools.dir;

import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.constants.SystemPropertyNames;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.FileUtils;
import com.sangupta.jerry.util.ReadableUtils;

/**
 * Clean all temporary files from OS temporary folder.
 * 
 * @author sangupta
 *
 */
@Command(name = "cleantmp", description = "Clean the current temp directories")
public class CleanTemp implements CliTool {

	@Inject
	private HelpOption helpOption;
	
	@Option(name = { "--info", "-i" }, description = "Info mode, no cleaning is done")
	private boolean info;
	
	@Option(name = { "--clean", "-c" }, description = "Clean mode, actually deletes files")
	private boolean clean;
	
	public static void main(String[] args) {
		if(AssertUtils.isEmpty(args)) {
			args = new String[] { "--help" };
		}
		
		CleanTemp cleanTemp = SingleCommand.singleCommand(CleanTemp.class).parse(args);
		if(cleanTemp.helpOption.showHelpIfRequested()) {
			return;
		}
		
		cleanTemp.execute();
	}

	private void execute() {
		String tmpDir = System.getProperty(SystemPropertyNames.JAVA_TMPDIR);
		File file = new File(tmpDir);
		System.out.println("Temporary directory is " + file.getAbsoluteFile().getAbsolutePath());
		
		if(!file.exists()) {
			System.out.println("Temporary directory does not exist.");
			return;
		}
		
		if(!file.isDirectory()) {
			System.out.println("Temporary directory is not a directory.");
			return;
		}
		
		if(!file.canRead()) {
			System.out.println("Unable to read temporary directory.");
			return;
		}
		
		List<File> files = FileUtils.listFiles(file.getAbsoluteFile().getAbsolutePath() + File.separator + "*", true);
		if(AssertUtils.isEmpty(files)) {
			System.out.println("Temporary directory has no files.");
			return;
		}
		
		if(this.info) {
			showInfo(files);
			return;
		}
		
		recoverSpace(files);
	}

	private void recoverSpace(List<File> files) {
		long size = 0;
		int count = 0;
		long unrecoverable = 0;
		for(File file : files) {
			if(file.exists() && file.isFile() && file.canWrite()) {
				long fs = file.length();
				boolean deleted = file.delete();
				if(deleted) {
					size += fs;
					count++;
				} else {
					unrecoverable += file.length();
				}
			} else {
				unrecoverable += file.length();
			}
		}
		
		System.out.println("Total recovered space is " + ReadableUtils.getReadableByteCount(size) + " from " + count + " files.");
		System.out.println("Unrecoverable space is " + ReadableUtils.getReadableByteCount(unrecoverable));
	}

	private void showInfo(List<File> files) {
		long size = 0;
		int count = 0;
		long unrecoverable = 0;
		for(File file : files) {
			if(file.exists() && file.isFile() && file.canWrite()) {
				size += file.length();
				count++;
			} else {
				unrecoverable += file.length();
			}
		}
		
		System.out.println("Total recoverable space is " + ReadableUtils.getReadableByteCount(size) + " in " + count + " files.");
		System.out.println("Unrecoverable space is " + ReadableUtils.getReadableByteCount(unrecoverable));
	}
}
