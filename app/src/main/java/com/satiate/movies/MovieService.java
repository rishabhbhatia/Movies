package com.satiate.movies;

import com.satiate.movies.models.JsonBlobResponse;
import com.satiate.movies.models.Movie;
import com.satiate.movies.models.Movies;
import com.satiate.movies.models.YoutubeTrailer;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Rishabh Bhatia on 10/11/16.
 */

public interface MovieService {

    public static String MOVIE_BASE_ENDPOINT = "https://jsonblob.com/";
    public static String MOVIES_GET_LIST_ENDPOINT = "api/jsonBlob/5824a798e4b0a828bd21a48d";

    @GET(MOVIES_GET_LIST_ENDPOINT)
    Observable<Movies> getMovies();

    @GET
    Observable<Movie> getMovieDetails(@Url String url);

    @GET
    Observable<ArrayList<YoutubeTrailer>> getYoutubeTrailers(@Url String url);

    @PUT(MOVIES_GET_LIST_ENDPOINT+"/")
    Observable<Void> updateBlobJson(@Body JSONObject body);

}
