package com.sangupta.clitools.dir;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.SingleCommand;

import java.io.File;

import javax.inject.Inject;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.ds.Tree;
import com.sangupta.jerry.transform.Transformer;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.FileUtils;

@Command(name = "tree", description = "Displays a directory tree of the folder")
public class TreeCommand implements CliTool {
	
	@Inject
	private HelpOption helpOption;
	
	@Arguments(description = "The path of which to display the directory tree")
	private String path = ".";
	
	public static void main(String[] args) {
		TreeCommand tree = SingleCommand.singleCommand(TreeCommand.class).parse(args);
		if(tree.helpOption.showHelpIfRequested()) {
			return;
		}
		
		tree.run();
	}
	
	private void run() {
		if(AssertUtils.isEmpty(this.path)) {
			this.path = ".";
		}
		
		Tree<File> tree = FileUtils.getDirTree(new File(this.path));
		System.out.println(tree.renderTree(new Transformer<File, String>() {
			
			@Override
			public String transform(File file) {
				return file.getAbsoluteFile().getName();
			}
			
		}));
	}
	
	
}
