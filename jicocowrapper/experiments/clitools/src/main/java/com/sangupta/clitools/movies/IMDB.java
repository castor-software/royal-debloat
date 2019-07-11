package com.sangupta.clitools.movies;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.annotations.SerializedName;
import com.sangupta.clitools.CliTool;
import com.sangupta.clitools.WebInvoker;
import com.sangupta.jerry.http.WebResponse;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.GsonUtils;
import com.sangupta.jerry.util.UriUtils;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.SingleCommand;

@Command(name = "imdb", description = "Fetch movie information from IMDB")
public class IMDB implements Runnable, CliTool {
	
	@Arguments
	private List<String> arguments;
	
	@Inject
	private HelpOption helpOption;

	public static void main(String[] args) {
		IMDB imdb = SingleCommand.singleCommand(IMDB.class).parse(args);
		
		if(imdb.helpOption.showHelpIfRequested()) {
			return;
		}
		
		imdb.run();
	}
	
	@Override
	public void run() {
		if(AssertUtils.isEmpty(this.arguments)) {
			System.out.println("Provide the movie title to search for.");
			return;
		}

		String title = StringUtils.join(this.arguments, " ");
		String url = "http://www.omdbapi.com/?apikey=3dfb8ed0&y=&plot=full&r=json&t=" + UriUtils.encodeURIComponent(title);
		WebResponse response = WebInvoker.getResponse(url);
		if(response == null) {
			System.out.println("Unable to connect to internet to fetch movie details!");
			return;
		}
		
		if(!response.isSuccess()) {
			System.out.println("Invalid server response!");
			return;
		}
		
		MovieResponse mr = GsonUtils.getGson(FieldNamingPolicy.IDENTITY).fromJson(response.getContent(), MovieResponse.class);
		if(mr == null) {
			System.out.println("Unable to decipher server response!");
			return;
		}
		
		System.out.println("Title: " + mr.title + " (" + mr.year + ") - " + mr.rated);
		System.out.println("Rating: " + mr.imdbRating + " (" + mr.imdbVotes + " votes)");
		System.out.println("Released: " + mr.released + ", Runtime: " + mr.runtime);
		System.out.println("Genre: " + mr.genre);
		System.out.println("Plot: " + mr.plot);
		System.out.println("Actors: " + mr.actors);
		System.out.println("Written By: " + mr.writer);
		System.out.println("Directed By: " + mr.director);
		System.out.println("Language: " + mr.language + ", Country: " + mr.country);
	}

	private static class MovieResponse {
		
		@SerializedName("Title")
		String title;
		
		@SerializedName("Year")
		String year;
		
		@SerializedName("Rated")
		String rated;
		
		@SerializedName("Released")
		String released;
		
		@SerializedName("Runtime")
		String runtime;
		
		@SerializedName("Genre")
		String genre;
		
		@SerializedName("Director")
		String director;
		
		@SerializedName("Writer")
		String writer;
		
		@SerializedName("Actors")
		String actors;
		
		@SerializedName("Plot")
		String plot;
		
		@SerializedName("Language")
		String language;
		
		@SerializedName("Country")
		String country;
		
		String imdbRating;
		
		String imdbVotes;
		
	}
}
