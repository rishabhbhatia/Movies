package com.satiate.movies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.satiate.movies.interfaces.IAsyncCallback;
import com.satiate.movies.models.Movies;
import com.satiate.movies.network.BaseHttpRequest;
import com.satiate.movies.network.MoviesListingParser;
import com.satiate.movies.network.WebResponse;

public class HomeScreen extends AppCompatActivity {

    private Movies movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        getMovies();
    }

    private void getMovies()    //fetch movie listings
    {
        try {
            BaseHttpRequest request = new BaseHttpRequest(HomeScreen.this, "http://blog.ashwanik.in/search?max-results=50");
            request.setHtmlParser(new MoviesListingParser());
            IAsyncCallback callback = new IAsyncCallback() {
                @Override
                public void onComplete(WebResponse responseContent) {
                    movies = responseContent.getMovies();
                    Log.d("rishabh", "movies are: "+movies.getMovies().size());
                }

                @Override
                public void onError(String errorData) {
                    Toast.makeText(HomeScreen.this, errorData, Toast.LENGTH_SHORT).show();
                }
            };
            request.execute(callback, MoviesApplication.getInstance().getRequestQueue());
        } catch (Exception e) {
            Toast.makeText(HomeScreen.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
