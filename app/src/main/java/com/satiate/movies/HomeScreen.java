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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.satiate.movies.models.Movie;
import com.satiate.movies.models.Movies;
import com.satiate.movies.utilities.Constants;

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
    @BindView(R.id.ll_home_footer_main)
    LinearLayout llHomeFooterMain;
    @BindView(R.id.slider_home_cover)
    SliderLayout sliderHomeCover;

    private Movies movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);

        loadToolbarProperties();
//        blurFooter();
        loadCoverSlider();
    }

    private void loadCoverSlider()
    {
        for(int i=0; i<100; i++)
        {
            DefaultSliderView defaultSliderView = new DefaultSliderView(HomeScreen.this);
            defaultSliderView
                    .image("http://www.newvideo.com/wp-content/uploads/2011/10/Assassins-Creed-Lineage-DVD-F.jpg")
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            sliderHomeCover.addSlider(defaultSliderView);
        }

        sliderHomeCover.addOnPageChangeListener(this);
        sliderHomeCover.stopAutoCycle();
    }

    private void blurFooter()
    {
        Blurry.with(HomeScreen.this)
                .radius(2)
                .sampling(2)
                .async()
                .animate(500)
                .onto(llHomeFooterMain);
    }

    private void loadBackground(String poster) {
        Glide
                .with(HomeScreen.this)
//                .load(Uri.parse("http://www.newvideo.com/wp-content/uploads/2011/10/Assassins-Creed-Lineage-DVD-F.jpg"))
                .load(Uri.parse(poster))
                .crossFade()
                .fitCenter()
                .into(ivCover);
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
            Observable<Movies> moviesObservable = MoviesApplication.movieService.getMovies();

            moviesObservable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(movies -> {
                        HomeScreen.this.movies = movies;

                        Movie movie = movies.getMovies().get(0);

                        Log.e(Constants.TAG, movie
                                .getTitle());
                        loadMovieInfo();

                        if(!movie.isInfo_present()) {
                            fetchMovieInfo();
                        }

                    });

        } catch (Exception e) {
            Toast.makeText(HomeScreen.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void fetchMovieInfo()
    {
       /* Observable<Movie> movieObservable = MoviesApplication.movieService.getMovieDetails(Constants.OPEN_MOVIE_TITLE_SEARCH+movies.getMovies().
                get(sliderHomeCover.getCurrentPosition()).getTitle()+Constants.OPEN_MOVIE_TITLE_SEARCH_SERIES_SUFFIX);

        movieObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movie -> {

                    Log.e(Constants.TAG, movie.toString());
                    sliderHomeCover.getCurrentSlider().image(movie.getPoster()).setScaleType(BaseSliderView.ScaleType.Fit);
                    movie.setInfo_present(true);
                });
*/
    }

    public static void longInfo(String str) {
        if(str.length() > 4000) {
            Log.i(Constants.TAG, str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else
            Log.i(Constants.TAG, str);
    }

    private void playVideo(String sampleMovieFile) {
        videoView.setVideoURI(Uri.parse(sampleMovieFile));
        videoView.start();
//        Log.d(Constants.TAG, "staring video: "+sampleMovieFile[0]);
    }

    @OnClick(R.id.bt_home_request)
    public void onClick() {
        getMovies();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        loadMovieInfo();

        if(movies != null && movies.getMovies() != null && !movies.getMovies().get(sliderHomeCover.getCurrentPosition()).isInfo_present()) {
            fetchMovieInfo();
        }
    }

    private void loadMovieInfo()
    {
        if(movies != null && movies.getMovies() != null && movies.getMovies().get(sliderHomeCover.getCurrentPosition()) != null)
        {
            Movie movie = movies.getMovies().get(sliderHomeCover.getCurrentPosition());
            String title = movie.getTitle();

            if(title != null) tvHomeFooterTitle.setText(title);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
