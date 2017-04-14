package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmoviesapp.Model.MoviePosters;
import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {
    private ActionBar actionBar;
    private TextView mMovieTitle;
    private TextView mMovieReleaseYear;
    private TextView mMovieSummary;
    private ImageView mMoviePoster;
    private RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);


        mMovieTitle = (TextView)findViewById(R.id.tv_movie_title);
        mMovieReleaseYear = (TextView)findViewById(R.id.tv_movie_release_year);
        mMovieSummary = (TextView)findViewById(R.id.tv_movie_plot_summary);
        mMoviePoster = (ImageView)findViewById(R.id.iv_movie_details_poster);
        mRatingBar = (RatingBar)findViewById(R.id.ratingBar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(MainActivity.SERIALIZABLE_CONTENT)){
            Intent intent = getIntent();
            MoviePosters moviePosters = (MoviePosters) intent.getSerializableExtra(MainActivity.SERIALIZABLE_CONTENT);

            actionBar.setTitle(moviePosters.getMovie_original_title());
            mMovieTitle.setText(moviePosters.getMovie_original_title());
            mMovieReleaseYear.setText(moviePosters.getMovie_release_date());
            mMovieSummary.setText(moviePosters.getMovie_overview());
            mRatingBar.setRating((float) moviePosters.getMovie_vote_average());


            Picasso.with(this).load(moviePosters.getMovie_poster_path()).into(mMoviePoster);


        }
    }
}
