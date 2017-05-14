package com.example.android.popularmoviesapp;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.popularmoviesapp.Adapter.RecyclerAdapter;
import com.example.android.popularmoviesapp.Data.PMAContract;
import com.example.android.popularmoviesapp.Model.MoviePosters;
import com.example.android.popularmoviesapp.Model.MovieReviews;
import com.example.android.popularmoviesapp.Model.MovieTrailers;
import com.example.android.popularmoviesapp.Utils.MovieUtils;
import com.example.android.popularmoviesapp.Utils.NetworkUtils;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity {
    @BindView(R.id.layout_wrapper_trailers)LinearLayout linearLayout;
    @BindView(R.id.tv_review_title)TextView mMovieReviewTitle;
    @BindView(R.id.expandable_review_button)RelativeLayout mMovieReviewButton;
    @BindView(R.id.tv_movie_title) TextView mMovieTitle;
    @BindView(R.id.tv_movie_release_year) TextView mMovieReleaseYear;
    @BindView(R.id.tv_movie_plot_summary) TextView mMovieSummary;
    @BindView(R.id.iv_movie_details_poster) ImageView mMoviePoster;
    @BindView(R.id.ratingBar) RatingBar mRatingBar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.collapsingImage) ImageView collapsingImage;
    @BindView(R.id.favorite_fab)FloatingActionButton fab;
    @BindView(R.id.expandable_reviews)
    ExpandableLinearLayout expandableLinearLayout;
    @BindView(R.id.tv_movie_review)TextView mMovieReview;
    private ActionBar actionBar;
    private final static String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private boolean isMovieFavorited = false;
    private String favorite_tag = "MOVIE_FAVORITED_TAG";
    MoviePosters moviePosters;
    LoaderManager.LoaderCallbacks<MovieReviews> movieReviewsLoaderCallbacks;
    LoaderManager.LoaderCallbacks<ArrayList<MovieTrailers>> movieTrailersLoaderCallbacks;
    private int LOADER_REVIEW_ID = 231;
    private int LOADER_TRAILER_ID = 233;
    private String trailer_key;

    private final String MOVIE_TAG = "movie_tag";




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_TAG, moviePosters);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        movieReviewsLoaderCallbacks = new LoaderManager.LoaderCallbacks<MovieReviews>()
        {
            @Override
            public Loader<MovieReviews> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<MovieReviews>(MovieDetails.this) {
                    MovieReviews movieReviewsCache;

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        if (movieReviewsCache != null){
                            deliverResult(movieReviewsCache);
                        }
                        else {
                            forceLoad();
                        }
                    }

                    @Override
                    public MovieReviews loadInBackground() {
                        MovieReviews movieReviews = new MovieReviews();
                        String network_response = " ";
                        try
                        {
                            URL url = NetworkUtils.buildReviewURL(MovieDetails.this, moviePosters.getMovie_id());
                            network_response = NetworkUtils.getResponseFromHttpUrl(url);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                            onCancelLoad();
                        }

                        if (network_response != null)
                            try
                            {
                                //Convert JSON to movie reviews
                                movieReviews = MovieUtils.ConvertResulttoMovieReviewsModel(network_response);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                onCancelLoad();
                            }
                        return movieReviews;
                    }

                    @Override
                    public void deliverResult(MovieReviews data) {
                        movieReviewsCache = data;
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<MovieReviews> loader, MovieReviews data) {

                mMovieReview.setText(data.getReview_content());
                expandableLinearLayout.initLayout();
            }

            @Override
            public void onLoaderReset(Loader<MovieReviews> loader) {

            }
        };

        movieTrailersLoaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<MovieTrailers>>() {
            @Override
            public Loader<ArrayList<MovieTrailers>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<ArrayList<MovieTrailers>>(MovieDetails.this) {
                    ArrayList<MovieTrailers> movieTrailers;
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        if (movieTrailers != null){
                            deliverResult(movieTrailers);
                        }
                        else {
                            forceLoad();
                        }

                    }

                    @Override
                    public ArrayList<MovieTrailers> loadInBackground() {
                        ArrayList<MovieTrailers> movieTrailers = new ArrayList<>();
                        String network_response = " ";
                        try
                        {
                            URL url = NetworkUtils.buildVideoURL(MovieDetails.this, moviePosters.getMovie_id());
                            network_response = NetworkUtils.getResponseFromHttpUrl(url);

                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                            onCancelLoad();
                        }

                        if (network_response != null)
                            try
                            {
                                //Convert JSON to movie trailers
                                movieTrailers = MovieUtils.ConvertResulttoMovieTrailersModel(network_response);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                onCancelLoad();
                            }
                        return movieTrailers;
                    }

                    @Override
                    public void deliverResult(ArrayList<MovieTrailers> data) {
                        super.deliverResult(data);
                        movieTrailers = data;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<MovieTrailers>> loader, final ArrayList<MovieTrailers> data)
            {
                for (int i = 0; i < data.size(); i ++)
                {
                    Button button = new Button(MovieDetails.this);
                    button.setText( getString(R.string.trailer)+" "+(i+1));
                    final int finalI = i;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            trailer_key = data.get(finalI).getKey();
                            String full_url = YOUTUBE_URL + trailer_key;
                            Uri uri = Uri.parse(full_url);
                            Intent showVid = new Intent(Intent.ACTION_VIEW, uri);
                            Intent chooser = Intent.createChooser(showVid, "Watch trailer via ");
                            startActivity(chooser);
                        }
                    });

                    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    linearLayout.addView(button, lp);

                }
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<MovieTrailers>> loader)
            {

            }
        };
        getSupportLoaderManager().initLoader(LOADER_REVIEW_ID, null, movieReviewsLoaderCallbacks);
        getSupportLoaderManager().initLoader(LOADER_TRAILER_ID, null, movieTrailersLoaderCallbacks);

        //Shared preference to store the boolean value of whether a movie is favorited or not
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPrefs.edit();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        final int movie_id;

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_TAG)) {
            moviePosters = savedInstanceState.getParcelable(MOVIE_TAG);
        }
        else
            {
            Intent intent = getIntent();
            moviePosters = null;
            if (intent != null && intent.hasExtra(MainActivity.PARCELABLE_CONTENT))
            {
                moviePosters = intent.getParcelableExtra(MainActivity.PARCELABLE_CONTENT);

            }
        }

        movie_id = moviePosters.getMovie_id();
        favorite_tag = favorite_tag + movie_id;
        isMovieFavorited = sharedPrefs.getBoolean(favorite_tag, false);

        String imageURL = moviePosters.getMovie_poster_path();
        String backdrop = RecyclerAdapter.base_path_image_url + moviePosters.getMovie_backdrop_path();
        actionBar.setTitle(moviePosters.getMovie_original_title());
        Picasso.with(this).load(backdrop).priority(Picasso.Priority.HIGH).placeholder(R.mipmap.ic_launcher_round).into(collapsingImage);
        Picasso.with(this).load(imageURL).placeholder(R.mipmap.ic_launcher_round).into(mMoviePoster);


        collapsingToolbarLayout.setTitle(moviePosters.getMovie_original_title());

        mMovieReleaseYear.append(moviePosters.getMovie_release_date());
        mMovieSummary.setText(moviePosters.getMovie_overview());
        float rating = (float)((moviePosters.getMovie_vote_average() /10) * 5);
        mRatingBar.setRating(rating);

        if (isMovieFavorited){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        }
        if (!isMovieFavorited){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        }

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Favorite or unfavorite;
                if (isMovieFavorited)
                {
                    //Unfavorite movie
                    int rows = getContentResolver().delete(PMAContract.PMAEntry.CONTENT_URI, PMAContract.PMAEntry.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(movie_id)});
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                    editor.putBoolean(favorite_tag, false);
                    editor.apply();
                    isMovieFavorited = false;
                    Snackbar.make(v, "Movie unfavorited", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    //favorite movie
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(PMAContract.PMAEntry.COLUMN_MOVIE_ID, moviePosters.getMovie_id());
                    contentValues.put(PMAContract.PMAEntry.COLUMN_MOVIE_SYNOPSIS, moviePosters.getMovie_overview());
                    contentValues.put(PMAContract.PMAEntry.COLUMN_MOVIE_RELEASE_DATE, moviePosters.getMovie_release_date());
                    contentValues.put(PMAContract.PMAEntry.COLUMN_MOVIE_RATING, moviePosters.getMovie_vote_average());
                    contentValues.put(PMAContract.PMAEntry.COLUMN_MOVIE_POSTER, moviePosters.getMovie_poster_path());
                    contentValues.put(PMAContract.PMAEntry.COLUMN_MOVIE_TITLE, moviePosters.getMovie_original_title());

                    getContentResolver().insert(PMAContract.PMAEntry.CONTENT_URI, contentValues);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                    editor.putBoolean(favorite_tag, true);
                    editor.apply();
                    isMovieFavorited = true;
                    Snackbar.make(v, "Movie favorited", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        mMovieReviewButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                expandableLinearLayout.toggle();
            }
        });
        expandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter()
        {
            @Override
            public void onPreOpen() {
                super.onPreOpen();
                createRotateAnimator(mMovieReviewButton, 0f, 180f).start();

            }

            @Override
            public void onPreClose() {
                createRotateAnimator(mMovieReviewButton, 180f, 0f).start();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(MovieDetails.this);
                break;
        }
        return false;
    }


    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }
}
