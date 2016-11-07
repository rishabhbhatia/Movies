package com.satiate.movies.network;

import com.satiate.movies.interfaces.IHTMLParser;
import com.satiate.movies.models.Movie;
import com.satiate.movies.models.Movies;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public class MoviesListingParser implements IHTMLParser {

    @Override
    public Movies parseHTML(String htmlToParse) {

        Movies movies = new Movies();

        try {
            Document doc = Jsoup.parse(htmlToParse);
            Elements anchors = doc.select(".entry-header a");
            for (Element anchor : anchors) {
                Movie movie = new Movie();
                movie.setTitle(anchor.text());
                movies.getMovies().add(movie);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return movies;
    }
}
