package com.satiate.movies;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.satiate.movies.models.JsonBlobResponse;
import com.satiate.movies.models.Movie;
import com.satiate.movies.models.Movies;
import com.satiate.movies.models.YoutubeTrailer;
import com.satiate.movies.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.blurry.Blurry;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeScreen extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    @BindView(R.id.bt_home_request)
    Button btHomeRequest;
    @BindView(R.id.activity_home_screen)
    RelativeLayout activityHomeScreen;
    @BindView(R.id.toolbar_search)
    SearchView toolbarSearch;
    @BindView(R.id.toolbar_iv_logo)
    ImageView toolbarIvLogo;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_home_footer_title)
    TextView tvHomeFooterTitle;
    @BindView(R.id.tv_home_footer_categories)
    TextView tvHomeFooterCategories;
    @BindView(R.id.ll_home_footer_main)
    LinearLayout llHomeFooterMain;
    @BindView(R.id.slider_home_cover)
    SliderLayout sliderHomeCover;
    @BindView(R.id.frame_home_footer_play_pause)
    FrameLayout frameHomeFooterPlayPause;

    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);

        loadToolbarProperties();
//        blurFooter();
//        loadCoverSlider();
        getMovies();
    }

    private void loadImageSliders(List<Movie> someMovies)
    {
        for (int i = 0; i < someMovies.size(); i++)
        {
            DefaultSliderView defaultSliderView = new DefaultSliderView(HomeScreen.this);
            defaultSliderView
                    .image(Constants.RANDOM_IMAGE)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            sliderHomeCover.addSlider(defaultSliderView);
        }

        sliderHomeCover.addOnPageChangeListener(this);
        sliderHomeCover.stopAutoCycle();

        loadMovieInfo();
    }

    private void blurFooter() {
        Blurry.with(HomeScreen.this)
                .radius(2)
                .sampling(2)
                .async()
                .animate(500)
                .onto(llHomeFooterMain);
    }

    private void loadToolbarProperties()
    {
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
            Observable<ArrayList<Movie>> moviesObservable = MoviesApplication.movieService.getMovies();

            moviesObservable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(movies -> {
                        HomeScreen.this.movies = movies;
                        Log.d(Constants.TAG, "Found movies "+movies.size());

                        List<Movie> someMovies = movies.subList(0,20);

                        loadImageSliders(someMovies);
                    });

        } catch (Exception e) {
            Toast.makeText(HomeScreen.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void fetchMovieInfo()
    {
        int position = sliderHomeCover.getCurrentPosition();
        String movieTitle = movies.get(position).getTitle();

        Log.e(Constants.TAG, "fetch movie information "+position+" for movie "+movieTitle);


        Observable<Movie> movieObservable = MoviesApplication.movieService.getMovieDetails(Constants.OPEN_MOVIE_TITLE_SEARCH+ movieTitle
                +Constants.OPEN_MOVIE_TITLE_SEARCH_SERIES_SUFFIX);

        movieObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movie -> {

                    movie.setTitle(movieTitle);
                    Log.e(Constants.TAG, movie.toString());
                    sliderHomeCover.getCurrentSlider().image(movie.getPoster()).setScaleType(BaseSliderView.ScaleType.Fit);
                    movie.setInfo_present(true);
                    movies.remove(position);
                    movies.add(position, movie);

                    Log.e(Constants.TAG, "shud update blob now");
                    Gson gson = new Gson();

                    try {
                        String json = gson.toJson(movies);
//                        longInfo(json);
                        Observable<Void> jsonBlobResponseObservable = MoviesApplication.movieService.
                                updateBlobJson(json);

                        jsonBlobResponseObservable.subscribeOn(Schedulers.newThread())
                                .observeOn(Schedulers.newThread())
                                .subscribe(Void -> {
                                    Log.e(Constants.TAG, "blobs better be updated");
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

    }

    public static void longInfo(String str) {
        if (str.length() > 4000) {
            Log.i(Constants.TAG, str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else
            Log.i(Constants.TAG, str);
    }

    @OnClick( { R.id.bt_home_request, R.id.frame_home_footer_play_pause })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_home_request:
                break;
            case R.id.frame_home_footer_play_pause:
                break;
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        loadMovieInfo();

        if (movies != null && !movies.get(sliderHomeCover.getCurrentPosition()).isInfo_present())
        {
            fetchMovieInfo();
        }
    }

    private void loadMovieInfo()
    {
        if (movies != null && movies.get(sliderHomeCover.getCurrentPosition()) != null)
        {
            Movie movie = movies.get(sliderHomeCover.getCurrentPosition());
            String title = movie.getTitle();

            Log.e(Constants.TAG, "movie title is: "+title+ " at position "+sliderHomeCover.getCurrentPosition());

            if (title != null) tvHomeFooterTitle.setText(title);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
