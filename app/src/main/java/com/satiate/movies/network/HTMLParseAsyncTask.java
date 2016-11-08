package com.satiate.movies.network;

import android.os.AsyncTask;
import android.util.Log;

import com.satiate.movies.models.Movie;
import com.satiate.movies.models.Movies;
import com.satiate.movies.utilities.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public class HTMLParseAsyncTask extends AsyncTask<String, Void, Void> {

    private Movies movies;
    private BaseHttpRequest currentRequest;
    private String response;

    public BaseHttpRequest getCurrentRequest() {
        return currentRequest;
    }

    public void setCurrentRequest(BaseHttpRequest currentRequest) {
        this.currentRequest = currentRequest;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        if (getCurrentRequest().getHtmlParser() != null)
        {
            movies = new Movies();
            ArrayList<Movie> moviesList = new ArrayList<>();

            Element element = Jsoup.parse(response).body();
            Elements links = element.getElementsByTag("a");

            for(int i=0; i<links.size();i++)
            {
                if(i > 6)
                {
//                    String name = links.get(i).attr("href").replace("%20","").replace("%5b","").replace("%5d","");
                    String name = links.get(i).ownText().replace("/","").trim();
//                    Log.d(Constants.TAG, "hello to: "+name);
                    Movie movie = new Movie();
                    movie.setTitle(name);

                    if (!name.equalsIgnoreCase("icons") && !name.equalsIgnoreCase("listing.php"))
                    {
                        moviesList.add(movie);
                    }
                }
            }

            movies.setMovies(moviesList);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        getCurrentRequest().dismissProgressDialog();
        try {
            WebResponse webResponse = new WebResponse(movies);
            getCurrentRequest().getCallback().onComplete(webResponse);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void setResponse(String response) {
        this.response = response;
    }
}

