package com.satiate.movies;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.satiate.movies.utilities.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public class MoviesApplication extends Application {

    private static MoviesApplication sInstance;
    public static MovieService movieService;
    private HttpProxyCacheServer proxy;


    public synchronized static MoviesApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(Constants.NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(Constants.NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS);
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(httpClient.build())       //enable this for detailed network data flow view
                .baseUrl(MovieService.MOVIE_BASE_ENDPOINT)
                .build();

        movieService = retrofit.create(MovieService.class);
    }

    public static HttpProxyCacheServer getProxy() {
        return getInstance().proxy == null ? (getInstance().proxy = getInstance().newProxy()) : getInstance().proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }
}
