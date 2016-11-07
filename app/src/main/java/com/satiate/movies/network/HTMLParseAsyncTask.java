package com.satiate.movies.network;

import android.os.AsyncTask;

import com.satiate.movies.models.Movies;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public class HTMLParseAsyncTask extends AsyncTask<String, Void, Void> {

    private Movies movies;
    private BaseHttpRequest currentRequest;

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
        if (getCurrentRequest().getHtmlParser() != null) {
            movies = getCurrentRequest().getHtmlParser().parseHTML(params[0]);
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
}

