package com.satiate.movies.models;

import lombok.Data;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public @Data class Movie {

    private String title;
    private String Year;
    private String Type;
    private String Poster;
    private String Rated;
    private String Released;
    private String Runtime;
    private String Genre;
    private String Director;
    private String Writer;
    private String Actors;
    private String Plot;
    private String Language;
    private String Country;
    private String Metascore;
    private String imdbId;
    private String imdbRating;
    private String imdbVotes;
    private String totalSeasons;
    private String Response;
    private String trailer;
    private boolean info_present;
    private YoutubeTrailer youtubeTrailer;

}
