package com.satiate.movies.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.satiate.movies.interfaces.IAsyncCallback;
import com.satiate.movies.interfaces.IHTMLParser;
import com.satiate.movies.utilities.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public class BaseHttpRequest {

    ProgressDialog progressDialog;
    int responseCode;
    IAsyncCallback callback;
    Response.Listener<String> stringResponseListener;
    private IHTMLParser htmlParser;
    private String url;
    private Context context;

    public BaseHttpRequest(Activity localActivity,
                           String url) {

        context = localActivity;
        this.url = url;
        setListeners();
    }

    public IHTMLParser getHtmlParser() {
        return htmlParser;
    }

    public void setHtmlParser(IHTMLParser htmlParser) {
        this.htmlParser = htmlParser;
    }


    public IAsyncCallback getCallback() {
        return callback;
    }

    public Context getContext() {
        return context;
    }

    private void setListeners() {
        stringResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(Constants.TAG,"hello hello: "+response);
                HTMLParseAsyncTask task = new HTMLParseAsyncTask();
                task.setCurrentRequest(BaseHttpRequest.this);
                task.setResponse(response);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);
            }
        };
    }

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            StringWriter errors = new StringWriter();
            error.printStackTrace(new PrintWriter(errors));
            dismissProgressDialog();
            callback.onError(error.toString());
        }
    };

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void execute(IAsyncCallback callback, RequestQueue requestQueue) {
        this.callback = callback;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        dismissProgressDialog();
        progressDialog.show();
        addToRequestQueue(requestQueue, getStringRequest());
    }

    public <X> void addToRequestQueue(RequestQueue requestQueue, Request<X> req) {
        requestQueue.add(req);
    }

    Request<String> getStringRequest() {
        StringRequest request = new StringRequest(Request.Method.GET, url, stringResponseListener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return request;
    }

}
