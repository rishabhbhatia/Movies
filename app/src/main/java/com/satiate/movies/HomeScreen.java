package com.satiate.movies;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.satiate.movies.interfaces.IAsyncCallback;
import com.satiate.movies.models.Movie;
import com.satiate.movies.models.Movies;
import com.satiate.movies.network.BaseHttpRequest;
import com.satiate.movies.network.MoviesListingParser;
import com.satiate.movies.network.WebResponse;
import com.satiate.movies.utilities.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeScreen extends AppCompatActivity {

    @BindView(R.id.bt_home_request)
    Button btHomeRequest;
    @BindView(R.id.myVideo)
    VideoView videoView;
    @BindView(R.id.activity_home_screen)
    RelativeLayout activityHomeScreen;
    @BindView(R.id.toolbar_search)
    SearchView toolbarSearch;
    @BindView(R.id.toolbar_iv_logo)
    ImageView toolbarIvLogo;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_cover)
    ImageView ivCover;
    @BindView(R.id.tv_home_footer_title)
    TextView tvHomeFooterTitle;
    @BindView(R.id.tv_home_footer_categories)
    TextView tvHomeFooterCategories;
    private Movies movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);

        loadToolbarProperties();
    }

    private void loadToolbarProperties() {
        int id = toolbarSearch.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) toolbarSearch.findViewById(id);
        textView.setTextColor(Color.WHITE);

        int idPlate = toolbarSearch.getContext()
                .getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View v = toolbarSearch.findViewById(idPlate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            v.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        } else {
            v.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void getMovies()    //fetch movie listings
    {
        try {
            BaseHttpRequest request = new BaseHttpRequest(HomeScreen.this, Constants.SERIES);
            request.setHtmlParser(new MoviesListingParser());
            IAsyncCallback callback = new IAsyncCallback() {
                @Override
                public void onComplete(WebResponse responseContent) {
                    movies = responseContent.getMovies();

                    for (int i = 0; i < movies.getMovies().size(); i++) {
                        Movie movie = movies.getMovies().get(i);
                        Log.d(Constants.TAG, movie.getTitle());
                    }
                }

                @Override
                public void onError(String errorData) {
                    Toast.makeText(HomeScreen.this, errorData, Toast.LENGTH_SHORT).show();
                    Log.d(Constants.TAG, "error is: " + errorData);
                }
            };
            request.execute(callback, MoviesApplication.getInstance().getRequestQueue());
        } catch (Exception e) {
            Toast.makeText(HomeScreen.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void playVideo(String sampleMovieFile) {
//        videoView.setVideoURI(Uri.parse(sampleMovieFile[0]));
        videoView.setVideoURI(Uri.parse("http://223.29.212.2/data/disk2/Hindi%20Movies/Rustom%20(2016)%20%5bHindi%5d%20%5b720p%5d%20DVDSCR.mkv"));
        videoView.start();
//        Log.d(Constants.TAG, "staring video: "+sampleMovieFile[0]);
    }

    @OnClick(R.id.bt_home_request)
    public void onClick() {
        getMovies();
    }
}
