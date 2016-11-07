package com.satiate.movies.network;

import com.satiate.movies.models.Movies;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public class WebResponse {

    private Movies movies;

    public WebResponse(Movies movies) {
        this.movies = movies;
    }

    public Movies getMovies() {
        return movies;
    }

    public void setMovies(Movies movies) {
        this.movies = movies;
    }
}
