package com.satiate.movies.ImageSlider;

import android.content.Context;
import android.os.Build;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.satiate.movies.R;
import com.satiate.movies.models.Movie;
import com.satiate.movies.utilities.Constants;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by Rishabh Bhatia on 13/11/16.
 */

public class MovieSliderView extends BaseSliderView {

    private Movie movie;
    private Context context;
    private CircleProgressView circleProgressView;

    public MovieSliderView(Context context, Movie movie) {
        super(context);
        this.context = context;
        this.movie = movie;
    }

    @Override
    public View getView() {

        PercentRelativeLayout movieCard = (PercentRelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.movie_card, null);

        ImageView ivCover = (ImageView) movieCard.findViewById(R.id.iv_movie_card);
        TextView tvMovieName = (TextView) movieCard.findViewById(R.id.tv_movie_card_movie_name);
        TextView tvMovieCategory = (TextView) movieCard.findViewById(R.id.tv_movie_card_movie_category);
        TextView tvMovieDescription = (TextView) movieCard.findViewById(R.id.tv_movie_card_movie_description);
        circleProgressView = (CircleProgressView) movieCard.findViewById(R.id.circle_progress_movie_card_rating);

        Glide
                .with(context)
                .load(movie.getPoster())
                .asBitmap()
                .fitCenter()
                .into(ivCover);

        tvMovieName.setText(movie.getTitle());
        tvMovieCategory.setText(movie.getGenre());
        tvMovieDescription.setText(movie.getPlot());

        if(movie.getImdbRating() != null)
        {
            circleProgressView.setBarWidth(10);
            circleProgressView.setRimWidth(10);
            circleProgressView.setText(movie.getImdbRating());
            circleProgressView.setTextColorAuto(false);
            circleProgressView.setAutoTextSize(true);
//            circleProgressView.setTextSize(15);
            circleProgressView.setTextMode(TextMode.TEXT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                circleProgressView.setBarColor(context.getColor(R.color.imdb_rating_progress));
                circleProgressView.setRimColor(context.getColor(R.color.colorPrimaryDark));
                circleProgressView.setTextColor(context.getColor(android.R.color.white));
            }else
            {
                circleProgressView.setBarColor(context.getResources().getColor(R.color.imdb_rating_progress));
                circleProgressView.setRimColor(context.getResources().getColor(R.color.colorPrimaryDark));
                circleProgressView.setTextColor(context.getResources().getColor(android.R.color.white));
            }

            circleProgressView.setMaxValue(10);
            circleProgressView.setValue(0);
            circleProgressView.setValueAnimated(Float.valueOf(movie.getImdbRating()), 2500);
            circleProgressView.setText(movie.getImdbRating());
        }

        bindEventAndShow(movieCard, ivCover);
        return movieCard;
    }

    public void setRating(String rating) {

        Log.d(Constants.TAG, "Update movie rating to "+rating);
        if(rating != null)
        {
            circleProgressView.setMaxValue(10);
            circleProgressView.setValue(0);
            circleProgressView.setValueAnimated(Float.valueOf(rating), 2500);
            circleProgressView.setText(rating);
        }
    }
}