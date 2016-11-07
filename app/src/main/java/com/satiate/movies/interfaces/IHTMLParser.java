package com.satiate.movies.interfaces;

import com.satiate.movies.models.Movies;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public interface IHTMLParser {
    Movies parseHTML(String htmlToParse);
}
