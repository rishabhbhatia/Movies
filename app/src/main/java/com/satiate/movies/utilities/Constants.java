package com.satiate.movies.utilities;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public class Constants {

    public static final String TAG = "hello";

    //Open Data links
    public static final String SERIES = "http://s1.bia2m.biz/Series/";
    public static final String OPEN_MOVIE_TITLE_SEARCH = "http://www.omdbapi.com/?t=";
    public static final String OPEN_MOVIE_TITLE_SEARCH_SERIES_SUFFIX = "&type=series";
    public static final String OPEN_MOVIE_TITLE_SEARCH_MOVIES_SUFFIX = "&type=movie";
    public static final String OPEN_MOVIE_TITLE_SEARCH_EPISODES_SUFFIX = "&type=episode";


    //API Endpoints
    public static final String MOVIES_ENDPOINT = "https://jsonblob.com/api/jsonBlob/5824a798e4b0a828bd21a48d";
    public static final String TRAILER_ENDPOINT = "http://trailersapi.com/trailers.json?movie=";
    public static final String TRAILER_SUFFIX_ENDPOINT = "&limit=1";

    //Random Links
    public static final String RANDOM_IMAGE = "https://unsplash.it/500/500/?random";
    public static final String PLACEHOLDER_IMAGE = "https://unsplash.it/500/500/";

    //API Keys
    public static final String YOUTUBE_API_KEY = "AIzaSyC2l5aEx-zfE32sp7HXtBJb5Yb-oISPr_c";

    public static final int NETWORK_CALL_TIMEOUT = 60;
}
