package com.sangupta.clitools.finance;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.sangupta.clitools.CliTool;
import com.sangupta.clitools.WebInvoker;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.io.StringLineIterator;
import com.sangupta.jerry.util.AssertUtils;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.SingleCommand;

@Command(name = "stock", description = "Stock quotes from NASDAQ")
public class StockQuote implements Runnable, CliTool {

	@Inject
	private HelpOption helpOption;
	
	@Arguments
	private List<String> arguments;
	
	public static void main(String[] args) {
		StockQuote sq = SingleCommand.singleCommand(StockQuote.class).parse(args);
		
		if(sq.helpOption.showHelpIfRequested()) {
			return;
		}
		
		sq.run();
	}
	
	@Override
	public void run() {
		if(AssertUtils.isEmpty(arguments)) {
			System.out.println("Provide 4-letter NASDAQ codes for stock prices");
			return;
		}
		
		String url = "http://finance.yahoo.com/d/quotes.csv?f=snabopc1d1c6&s=";
		for(String arg : this.arguments) {
			url += arg + ",";
		}
		
		WebResponse response = WebInvoker.getResponse(url);
		if(response == null) {
			System.out.println("Unable to connect to internet to fetch stock rates!");
			return;
		}
		
		if(!response.isSuccess()) {
			System.out.println("Invalid server response!");
			return;
		}
		
		// parse the csv
		StringLineIterator iterator = new StringLineIterator(response.getContent());
		while(iterator.hasNext()) {
			String[] tokens = StringUtils.split(iterator.next(), ',');
			
			if(tokens.length != 9) {
				System.out.println("Pasing of response failed.");
				return;
			}
			
			// parse
			String symbol = tokens[0];
			String name = tokens[1];
			String ask = tokens[2];
			String bid = tokens[3];
			String open = tokens[4];
			String previous = tokens[5];
			String change = tokens[6];
			String lastTradeDate = tokens[7];
			String changeReal = tokens[8];
			
			System.out.println();
			System.out.println("Symbol: " + symbol);
			System.out.println("Name: " + name);
			System.out.println("Open/Previous: " + open + "/" + previous);
			System.out.println("Ask/Bid: " + ask + "/" + bid);
			System.out.println("Change: " + change + ", realtime: " + changeReal);
			System.out.println("Last trade date: " + lastTradeDate);
			System.out.println("");
		}
	}
}
