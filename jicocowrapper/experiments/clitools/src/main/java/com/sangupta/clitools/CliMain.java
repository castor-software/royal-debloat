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

package com.sangupta.clitools;

import io.airlift.airline.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.reflections.Reflections;

import com.sangupta.jerry.print.ConsoleTable;
import com.sangupta.jerry.util.AssertUtils;

/**
 * Main function to call any command within the `clitools` package.
 * 
 * @author sangupta
 *
 */
public class CliMain {
	
	/**
	 * Map of all commands with their .class instances as values
	 * 
	 */
	private static final Map<String, Class<? extends CliTool>> availableCommands = new HashMap<String, Class<? extends CliTool>>();
	
	/**
	 * Map between all commands and their basic description
	 */
	private static final Map<String, String> commandHelp = new TreeMap<String, String>();
	
	/**
	 * Discover all commands via reflection
	 */
	static {
		System.out.println("Discovering commands...");

		Reflections reflections = new Reflections("com.sangupta.clitools");


		Set<Class<? extends CliTool>> commands = reflections.getSubTypesOf(CliTool.class);

		System.out.println("Size of subtipes: " + commands.size());
		
		if(AssertUtils.isNotEmpty(commands)) {
			for(Class<? extends CliTool> clazz : commands) {
				if(Modifier.isAbstract(clazz.getModifiers())) {
					// no need to instantiate abstract classes
					continue;
				}

				Command com = clazz.getAnnotation(Command.class);
				if(com != null) {
					availableCommands.put(com.name(), clazz);
					commandHelp.put(com.name(), com.description());
				}
			}
		}

		System.out.println("Discovering commands finished...");
	}
	
	public static void main(String[] args) {
		if(args.length == 0) {
			showCommandList();
			return;
		}

		System.out.println("Size of available commands: " + availableCommands.size());
		
		String command = args[0].toLowerCase();
		if(!availableCommands.containsKey(command)) {
			System.out.println("No command found by name: " + args[0]);
			return;
		}
		
		Class<?> clazz = availableCommands.get(command);
		try {
			// remove the first argument from the arguments list
			String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
			
			Method main = clazz.getDeclaredMethod("main", new Class[] { String[].class });
			main.invoke(null, new Object[] { newArgs });
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodError(e.toString());
		} catch (IllegalAccessException e) {
			throw new IllegalAccessError(e.toString());
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Show a list of all commands with their basic description
	 * 
	 */
	private static void showCommandList() {
		System.out.println("Available commands:\n");
		ConsoleTable table = new ConsoleTable();
		table.setColumnSeparator("  ");
		for(Entry<String, String> entry : commandHelp.entrySet()) {
			table.addRow(entry.getKey(), entry.getValue());
		}
		table.write(System.out);
		System.out.println();
	}

}