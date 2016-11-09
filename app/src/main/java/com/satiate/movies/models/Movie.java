package com.satiate.movies.models;

import lombok.Data;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public @Data class Movie {

    private String title;
    private String year;
    private String imdbId;
    private String type;
    private String posterUrl;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String writer;
    private String actors;
    private String plot;
    private String language;
    private String country;
    private String metascore;
    private String imdbRating;
    private String imdbVotes;
    private String totalSeasons;
    private String response;

}
