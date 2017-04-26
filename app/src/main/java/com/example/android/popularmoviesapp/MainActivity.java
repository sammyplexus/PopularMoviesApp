package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.onClickListener, LoaderManager.LoaderCallbacks<ArrayList<MoviePosters>>{
    @BindView(R.id.rv_movie_posters) RecyclerView mRecyclerView;
    @BindView(R.id.pb_recyclerview) ProgressBar progressBar;
    @BindView(R.id.tv_recycler_view_error) TextView mErrorTextView;

    private final int LOADER_ID = 342;
    private RecyclerAdapter mRecyclerAdapter;
    private ArrayList<MoviePosters> MainMoviePosters;
    private String whatIsShowing = "popular";
    private ActionBar actionBar;
    private URL url = null;

    public static final String PARCELABLE_CONTENT = "parcelable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        actionBar = getSupportActionBar();
        mRecyclerAdapter = new RecyclerAdapter(this, this);

        if (NetworkUtils.isInternetAvailable() && NetworkUtils.isNetworkAvailable(this)) {

            mErrorTextView.setVisibility(View.INVISIBLE);
            MainMoviePosters = new ArrayList<>();
            actionBar.setTitle(getResources().getString(R.string.title_popular_movies));
            getPosters("popular");
        }
        else {
            mErrorTextView.setVisibility(View.VISIBLE);
            actionBar.hide();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu.size() == 0)
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_top_rated_movies:
                if (whatIsShowing == "popular") {
                    mErrorTextView.setVisibility(View.INVISIBLE);
                    actionBar.setTitle(getResources().getString(R.string.title_toprated_movies));
                    if (NetworkUtils.isNetworkAvailable(MainActivity.this) && NetworkUtils.isInternetAvailable()){
                        getPosters("top_rated");
                        whatIsShowing = "top_rated";
                    }
                    else {
                        noInternetConnection();
                    }
                }
                return true;
            case R.id.action_popular_movies:
                if (whatIsShowing == "top_rated")
                {
                    mErrorTextView.setVisibility(View.INVISIBLE);
                    actionBar.setTitle(getResources().getString(R.string.title_popular_movies));
                    if (NetworkUtils.isNetworkAvailable(MainActivity.this) && NetworkUtils.isInternetAvailable()) {
                        getPosters("popular");
                        whatIsShowing = "popular";
                        actionBar.setTitle(getResources().getString(R.string.title_popular_movies));
                    }
                    else {
                        noInternetConnection();
                    }
                }
                return true;
        }
        return false;
    }

    @Override
    public void onRecyclerItemClick(int position) {
       Intent intent = new Intent(this, MovieDetails.class);
        MoviePosters moviePosters = MainMoviePosters.get(position);
        intent.putExtra(PARCELABLE_CONTENT, moviePosters);
        startActivity(intent);
    }

    void noInternetConnection(){
        MainMoviePosters.clear();
        mRecyclerAdapter.notifyDataSetChanged();
        mErrorTextView.setVisibility(View.VISIBLE);
    }


    public void getPosters(String path) {

            Loader<ArrayList<MoviePosters>> loader = getSupportLoaderManager().getLoader(LOADER_ID);
            if (loader != null){
                getSupportLoaderManager().destroyLoader(LOADER_ID);
            }

            mRecyclerAdapter.setData(null);
            progressBar.setVisibility(View.VISIBLE);
            url = NetworkUtils.buildUrl(this, path);

            getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }


    @Override
    public Loader<ArrayList<MoviePosters>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<MoviePosters>>(this) {
            ArrayList<MoviePosters> MoviePostersCache;
            @Override
            protected void onStartLoading()
            {
               if (MoviePostersCache != null){
                   Log.w("It is not null", "Not null");
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

                try {
                    network_response = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (network_response != null)
                    try {
                       mMoviePosters = MovieUtils.ConvertResulttoMoviePostersModel(network_response);
                        Log.d("TAG", mMoviePosters.size() + " ");
                    } catch (JSONException e) {
                        e.printStackTrace();
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
    public void onLoadFinished(Loader<ArrayList<MoviePosters>> loader, ArrayList<MoviePosters> data) {
        if (data != null){
            mRecyclerAdapter.setData(data);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.num_columns_recycler_view));
            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            progressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }

        //loader.stopLoading();

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MoviePosters>> loader) {

    }
}



