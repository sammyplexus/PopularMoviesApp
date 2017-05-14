package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesapp.Adapter.FavoriteAdapter;
import com.example.android.popularmoviesapp.Data.PMAContract;
import com.example.android.popularmoviesapp.Model.MoviePosters;
import com.example.android.popularmoviesapp.Adapter.RecyclerAdapter;
import com.example.android.popularmoviesapp.Utils.MovieUtils;
import com.example.android.popularmoviesapp.Utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.onClickListener, FavoriteAdapter.onFavItemClickListener{
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_bundle";
    private static final String SORT_ORDER_KEY = "sort_order_key";
    private static final String FAVORITES = "favorites";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    @BindView(R.id.rv_movie_posters) RecyclerView mRecyclerView;
    @BindView(R.id.pb_recyclerview) ProgressBar progressBar;
    @BindView(R.id.btn_retry) Button mRetryButton;
    @BindView(R.id.tv_recycler_view_error) TextView mErrorTextView;
    LoaderManager.LoaderCallbacks<Cursor> CursorLoaderCallback;
    LoaderManager.LoaderCallbacks<ArrayList<MoviePosters>> MoviePosterCallback;
    private static final String SORT_TYPE_EXTRA = "sort_type";

    private GridLayoutManager gridLayoutManager;
    private final int LOADER_ID = 342;
    private final int LOADER_FAVORITE_ID = 343;
    private RecyclerAdapter mRecyclerAdapter;
    private ArrayList<MoviePosters> MainMoviePosters;

    private static String whatIsShowing = "popular";
    private ActionBar actionBar;
    private URL url = null;
    private FavoriteAdapter favoriteAdapter;
    public static final String PARCELABLE_CONTENT = "parcelable";
    private Cursor favorites_cursor;
    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        actionBar = getSupportActionBar();
        mRecyclerAdapter = new RecyclerAdapter(this, this);
        favoriteAdapter = new FavoriteAdapter(this, null, this);
        MainMoviePosters = new ArrayList<>();

        CursorLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>()
        {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = new CursorLoader(MainActivity.this, PMAContract.PMAEntry.CONTENT_URI, null, null, null, null);
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

                progressBar.setVisibility(View.INVISIBLE);
                if (data.getCount() == 0){
                    Toast.makeText(MainActivity.this, R.string.error_favorite_tray, Toast.LENGTH_LONG).show();
                }
                if (data != null){
                    favorites_cursor = data;
                    favoriteAdapter = new FavoriteAdapter(MainActivity.this, data, MainActivity.this);
                    gridLayoutManager = new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.num_columns_recycler_view));
                    mRecyclerView.setLayoutManager(gridLayoutManager);
                    mRecyclerView.setHasFixedSize(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    mRecyclerView.setAdapter(favoriteAdapter);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };

        MoviePosterCallback = new LoaderManager.LoaderCallbacks<ArrayList<MoviePosters>>() {
            @Override
            public Loader<ArrayList<MoviePosters>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<ArrayList<MoviePosters>>(MainActivity.this) {
                    ArrayList<MoviePosters> MoviePostersCache;
                    @Override
                    protected void onStartLoading()
                    {
                        if (MoviePostersCache != null){
                            deliverResult(MoviePostersCache);
                        }
                        else {
                            forceLoad();
                        }
                    }

                    @Override
                    public ArrayList<MoviePosters> loadInBackground()
                    {
                        ArrayList<MoviePosters> mMoviePosters = null;
                        String network_response = " ";
                        try
                        {
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
                                mMoviePosters = MovieUtils.ConvertResulttoMoviePostersModel(network_response);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                onCancelLoad();
                            }
                        return mMoviePosters;
                    }

                    @Override
                    public void deliverResult(ArrayList<MoviePosters> data)
                    {
                        MoviePostersCache = data;
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<MoviePosters>> loader, ArrayList<MoviePosters> data)
            {
                if (data != null){
                    mRecyclerAdapter.setData(data);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.num_columns_recycler_view));
                    mRecyclerView.setLayoutManager(gridLayoutManager);
                    mRecyclerView.setHasFixedSize(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    mRecyclerView.setAdapter(mRecyclerAdapter);
                }
                else {
                    showErrorMessageAndRetryButton();
                }
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<MoviePosters>> loader) {

            }
        };

        if (NetworkUtils.isNetworkAvailable(this)) {
            hideErrorMessageAndRetryButton();
            actionBar.setTitle(getString(R.string.title_popular_movies));
            getPosters(whatIsShowing);
        }
        else
        {
            progressBar.setVisibility(View.INVISIBLE);
            showErrorMessageAndRetryButton();
            actionBar.hide();
        }

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkUtils.isNetworkAvailable(MainActivity.this))
                {
                        hideErrorMessageAndRetryButton();
                        getPosters(whatIsShowing);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Still some issue with internet connectivity", Toast.LENGTH_LONG).show();
                    showErrorMessageAndRetryButton();
                }
            }
        });

        if (savedInstanceState != null){
            if (savedInstanceState != null) {

                String sortType = savedInstanceState.getString(SORT_ORDER_KEY);
                if (sortType.equals(FAVORITES)) {
                    getFavoriteMovies();
                } else {
                    getPosters(sortType);
                }
            } else {
                getPosters(POPULAR);
            }
        }


    }

    public void getPosters(String path) {
        mSortOrder = path;
        Bundle bundle = new Bundle();
        bundle.putString(SORT_TYPE_EXTRA, path);
        mRecyclerAdapter.setData(null);
        favoriteAdapter.setCursor(null);

        progressBar.setVisibility(View.VISIBLE);
        url = NetworkUtils.buildUrl(this, path);

        Loader<ArrayList<MoviePosters>> loader = getSupportLoaderManager().getLoader(LOADER_ID);
        if (loader == null){
            getSupportLoaderManager().initLoader(LOADER_ID, null, MoviePosterCallback);
        }
        else {
            getSupportLoaderManager().restartLoader(LOADER_ID, null, MoviePosterCallback);
        }
    }

    private void showErrorMessageAndRetryButton()
    {
        mErrorTextView.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessageAndRetryButton()
    {
        actionBar.show();
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRetryButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu.size() == 0)
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(BUNDLE_RECYCLER_LAYOUT)) {
                Parcelable savedRecylerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
                gridLayoutManager.onRestoreInstanceState(savedRecylerLayoutState);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (gridLayoutManager != null)
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, gridLayoutManager.onSaveInstanceState());
        outState.putString(SORT_ORDER_KEY, mSortOrder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_top_rated_movies:
                    mErrorTextView.setVisibility(View.INVISIBLE);
                    hideErrorMessageAndRetryButton();
                    actionBar.setTitle(getString(R.string.title_toprated_movies));
                    if (NetworkUtils.isNetworkAvailable(MainActivity.this))
                    {
                        whatIsShowing = "top_rated";
                        getPosters(whatIsShowing);
                        return true;
                    }
                    else
                    {
                        noInternetConnection();
                    }
                return false;
            case R.id.action_popular_movies:
                mErrorTextView.setVisibility(View.INVISIBLE);
                    hideErrorMessageAndRetryButton();
                    actionBar.setTitle(getResources().getString(R.string.title_popular_movies));
                    if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                        whatIsShowing = "popular";
                        getPosters(whatIsShowing);
                        actionBar.setTitle(getResources().getString(R.string.title_popular_movies));
                        return true;
                    }
                    else {
                        noInternetConnection();
                    }

                return false;
            case R.id.action_show_favorite_movies:
                actionBar.setTitle(getString(R.string.favorite_movies));
                hideErrorMessageAndRetryButton();
                progressBar.setVisibility(View.VISIBLE);
                getFavoriteMovies();
        }
        return false;
    }

    private void getFavoriteMovies() {
        mSortOrder = FAVORITES;
        progressBar.setVisibility(View.VISIBLE);
        Loader<CursorLoader> loader = getSupportLoaderManager().getLoader(LOADER_FAVORITE_ID);
        if (loader == null){
            getSupportLoaderManager().initLoader(LOADER_FAVORITE_ID, null, CursorLoaderCallback);
        }
        else {
            getSupportLoaderManager().restartLoader(LOADER_FAVORITE_ID, null, CursorLoaderCallback);
        }
    }

    @Override
    public void onRecyclerItemClick(int position) {
        Intent intent = new Intent(this, MovieDetails.class);
        MoviePosters moviePosters = mRecyclerAdapter.getData().get(position);
        intent.putExtra(PARCELABLE_CONTENT, moviePosters);
        startActivity(intent);
    }

    void noInternetConnection() {
        if (MainMoviePosters != null){
            MainMoviePosters.clear();
            mRecyclerAdapter.notifyDataSetChanged();
        }
        mRecyclerView.setAdapter(null);

        showErrorMessageAndRetryButton();
    }

    @Override
    public void onFavItemClick(int position) {
        Intent move = new Intent(this, MovieDetails.class);

        favorites_cursor.moveToPosition(position);
        MoviePosters moviePosters = new MoviePosters();
        moviePosters.setMovie_id(Integer.parseInt(favorites_cursor.getString(favorites_cursor.getColumnIndex(PMAContract.PMAEntry.COLUMN_MOVIE_ID))));
        moviePosters.setMovie_overview(favorites_cursor.getString(favorites_cursor.getColumnIndex(PMAContract.PMAEntry.COLUMN_MOVIE_SYNOPSIS)));
        moviePosters.setMovie_poster_path(favorites_cursor.getString(favorites_cursor.getColumnIndex(PMAContract.PMAEntry.COLUMN_MOVIE_POSTER)));
        moviePosters.setMovie_original_title(favorites_cursor.getString(favorites_cursor.getColumnIndex(PMAContract.PMAEntry.COLUMN_MOVIE_TITLE)));
        moviePosters.setMovie_release_date(favorites_cursor.getString(favorites_cursor.getColumnIndex(PMAContract.PMAEntry.COLUMN_MOVIE_RELEASE_DATE)));
        moviePosters.setMovie_vote_average(favorites_cursor.getDouble(favorites_cursor.getColumnIndex(PMAContract.PMAEntry.COLUMN_MOVIE_RATING)));
        moviePosters.setMovie_backdrop_path(favorites_cursor.getString(favorites_cursor.getColumnIndex(PMAContract.PMAEntry.COLUMN_MOVIE_POSTER)));

        move.putExtra(PARCELABLE_CONTENT, moviePosters);
        startActivity(move);

    }
}



