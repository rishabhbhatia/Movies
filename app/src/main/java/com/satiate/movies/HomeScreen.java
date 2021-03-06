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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.google.gson.Gson;
import com.satiate.movies.ImageSlider.MovieSliderView;
import com.satiate.movies.models.Movie;
import com.satiate.movies.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeScreen extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, OnPreparedListener, OnErrorListener {

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
    @BindView(R.id.video_view)
    EMVideoView emVideoView;

    private ArrayList<Movie> movies;
    private List<Movie> someMovies;

    private boolean isFirstDataset = true;

    private String json = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);

        loadToolbarProperties();
        sliderHomeCover.getPagerIndicator().setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        getMovies();

       /* emVideoView.setOnPreparedListener(this);
        emVideoView.setOnErrorListener(this);
        emVideoView.setVideoURI(Uri.parse("http://s1.bia2m.biz/Series/11.22.63/s1/11.22.63%20S01E01%20(Bia2Movies).mkv"));*/

    }

    private void loadImageSliders(List<Movie> tempMovies)
    {

        for (int i = 0; i < tempMovies.size(); i++)
        {
            final Movie currentMovie = tempMovies.get(i);

            MovieSliderView movieSliderView = new MovieSliderView(HomeScreen.this, currentMovie);
            movieSliderView.setOnSliderClickListener(this);

            sliderHomeCover.addSlider(movieSliderView);
        }

        sliderHomeCover.startAutoCycle();
        sliderHomeCover.addOnPageChangeListener(this);

      /*  if(isFirstDataset)
        {
            if(!tempMovies.get(0).isInfo_present())
            {
                fetchMovieInfo(tempMovies.get(0));
            }
        }else
        {
            sliderHomeCover.setCurrentPosition(someMovies.size()-2, true);
            someMovies.addAll(movies);
        }

        isFirstDataset = false;*/
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

            Log.d(Constants.TAG, "111");
            Observable<ArrayList<Movie>> moviesObservable = MoviesApplication.movieService.getMovies();

            moviesObservable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(movies -> {
                        HomeScreen.this.movies = movies;
                        Log.d(Constants.TAG, "Found movies "+movies.size());

                        someMovies = movies.subList(0,10);
                        List<Movie> tempMovies = movies.subList(0, 10);

                        loadImageSliders(tempMovies);
                        Log.d(Constants.TAG, "222");

                    });

        } catch (Exception e) {
            Toast.makeText(HomeScreen.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void fetchMovieInfo(Movie currentMovie)
    {
        int position = sliderHomeCover.getCurrentPosition();
        String movieTitle = currentMovie.getTitle();

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
                        json = gson.toJson(movies);
//                        longInfo(json);

                        if(sliderHomeCover.getCurrentPosition() == 110)
                        {
                            Observable<Void> jsonBlobResponseObservable = MoviesApplication.movieService.
                                    updateBlobJson(json);

                            jsonBlobResponseObservable.subscribeOn(Schedulers.newThread())
                                    .observeOn(Schedulers.newThread())
                                    .subscribe(Void -> {
                                        Log.e(Constants.TAG, "blobs better be updated");
                                    });
                        }

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
    }

    @Override
    public void onPageSelected(int position) {

        Movie movie = movies.get(sliderHomeCover.getCurrentPosition());

       /* MovieSliderView movieSliderView = (MovieSliderView) sliderHomeCover.getCurrentSlider();       //TODO try to animate movie rating's
        movieSliderView.setRating(movie.getImdbRating());*/
       /*
        if (!movie.isInfo_present())
        {
            fetchMovieInfo(movie);
        }
*/
        //TODO try to create pagination here
       /* if(position == someMovies.size()-1 && movies.size() > someMovies.size())
        {
            sliderHomeCover.stopAutoCycle();
            sliderHomeCover.removeAllSliders();
            sliderHomeCover.removeOnPageChangeListener(HomeScreen.this);

            if(movies.size() - someMovies.size() > 20)
            {
                List<Movie> someMoreMovies = movies.subList(someMovies.size(), (someMovies.size()+20));
                Log.d(Constants.TAG, "size of some more movies is: "+someMoreMovies.size());
                loadImageSliders(someMoreMovies);
            }else
            {
                loadImageSliders(movies.subList(someMovies.size(), movies.size()));
            }
        }*/
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPrepared() {
        emVideoView.start();
        Log.d(Constants.TAG, "starting the video");
    }

    @Override
    public boolean onError() {
        Log.d(Constants.TAG, "error");
        return false;
    }
}
