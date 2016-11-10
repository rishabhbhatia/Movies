package com.satiate.movies;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public class MoviesApplication extends Application {

    private static MoviesApplication sInstance;
    private RequestQueue mRequestQueue;
    public static MovieService movieService;

    public synchronized static MoviesApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mRequestQueue = Volley.newRequestQueue(this);
        VolleyLog.DEBUG = true;

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MovieService.MOVIE_BASE_ENDPOINT)
                .build();

        movieService = retrofit.create(MovieService.class);
    }
}
