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
import com.google.gson.Gson;
import com.satiate.movies.models.Movie;
import com.satiate.movies.utilities.Constants;

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
    private List<Movie> someMovies;

    private boolean isFirstDataset = true;

    private String json = "";

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

    private void loadImageSliders(List<Movie> tempMovies)
    {
        for (int i = 0; i < tempMovies.size(); i++)
        {
            DefaultSliderView defaultSliderView = new DefaultSliderView(HomeScreen.this);
            defaultSliderView
                    .image(Constants.RANDOM_IMAGE)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            sliderHomeCover.addSlider(defaultSliderView);
        }

        sliderHomeCover.startAutoCycle();
        sliderHomeCover.addOnPageChangeListener(this);

        if(isFirstDataset)
        {
            loadMovieInfo(tempMovies.get(sliderHomeCover.getCurrentPosition()));
            fetchMovieInfo(tempMovies.get(sliderHomeCover.getCurrentPosition()));
        }else
        {
//            sliderHomeCover.setCurrentPosition(someMovies.size()-2, true);
//            someMovies.addAll(movies);
        }

        isFirstDataset = false;
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

                        someMovies = movies.subList(629,740);
                        List<Movie> tempMovies = movies.subList(629, 740);

                        loadImageSliders(tempMovies);
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
                    movies.remove(position+629);
                    movies.add(position+629, movie);

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

        Movie movie = movies.get(sliderHomeCover.getCurrentPosition()+629);

        loadMovieInfo(movie);

        if (!movie.isInfo_present())
        {
            fetchMovieInfo(movie);
        }

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

    private void loadMovieInfo(Movie currentMovie)
    {
        if (currentMovie != null)
        {
            String title = currentMovie.getTitle();

            Log.e(Constants.TAG, "movie title is: "+title+ " at position "+sliderHomeCover.getCurrentPosition());

            if (title != null) tvHomeFooterTitle.setText(title);
        }
    }

}
