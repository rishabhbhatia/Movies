package com.satiate.movies;

import com.satiate.movies.models.Movie;
import com.satiate.movies.models.Movies;


import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Rishabh Bhatia on 10/11/16.
 */

public interface MovieService {

    public static String MOVIE_BASE_ENDPOINT = "https://jsonblob.com/";

    @GET("api/jsonBlob/5824a798e4b0a828bd21a48d")
    Observable<Movies> getMovies();

    @GET()
    Observable<Movie> getMovieDetails(@Url String url);
}
