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

package com.sangupta.clitools.basic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.ConsoleUtils;

import io.airlift.airline.Command;

/**
 * Find out the weekday today, or on a given date.
 * 
 * @author sangupta
 *
 */
@Command(name = "day", description = "Display the day today or a given date")
public class DayCommand implements CliTool {
	
	private static final String[] WEEKDAYS = { "Saturday", "Sunday", "Monday", "Tueday", "Wednesday", "Thursday", "Friday"};

	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		System.out.println("Today is " + WEEKDAYS[day]);
		
		String date = ConsoleUtils.readLine("Enter date (dd/MM/yyyy): ", true);
		if(AssertUtils.isEmpty(date)) {
			return;
		}
		
		Date d = null;
		try {
			d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
		} catch(ParseException e) {
			System.out.println("Date entered not in valid format.");
			return;
		}
		
		calendar.setTimeInMillis(d.getTime());
		day = calendar.get(Calendar.DAY_OF_WEEK);
		System.out.println("Day on " + date + " was " + WEEKDAYS[day]);
	}

}