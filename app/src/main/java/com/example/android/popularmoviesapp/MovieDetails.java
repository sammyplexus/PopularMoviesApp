package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmoviesapp.Adapter.RecyclerAdapter;
import com.example.android.popularmoviesapp.Model.MoviePosters;
import com.squareup.picasso.Picasso;

import java.util.concurrent.RecursiveAction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity {
    @BindView(R.id.tv_movie_title) TextView mMovieTitle;
    @BindView(R.id.tv_movie_release_year) TextView mMovieReleaseYear;
    @BindView(R.id.tv_movie_plot_summary) TextView mMovieSummary;
    @BindView(R.id.iv_movie_details_poster) ImageView mMoviePoster;
    @BindView(R.id.ratingBar) RatingBar mRatingBar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(MainActivity.PARCELABLE_CONTENT)){
            Intent intent = getIntent();

            MoviePosters moviePosters = intent.getParcelableExtra(MainActivity.PARCELABLE_CONTENT);
            String imageURL = RecyclerAdapter.base_path_image_url + moviePosters.getMovie_poster_path();
            actionBar.setTitle(moviePosters.getMovie_original_title());
            Picasso.with(this).load(imageURL).into(mMoviePoster);

            mMovieTitle.setText(moviePosters.getMovie_original_title());
            mMovieReleaseYear.append(moviePosters.getMovie_release_date());
            mMovieSummary.setText(moviePosters.getMovie_overview());

            float rating = (float)((moviePosters.getMovie_vote_average() /10) * 5);
            Log.d("rate", rating+" ");
            mRatingBar.setRating(rating);

        }
    }
}
