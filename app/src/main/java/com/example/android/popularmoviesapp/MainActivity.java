package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.popularmoviesapp.Model.MoviePosters;
import com.example.android.popularmoviesapp.Adapter.RecyclerAdapter;
import com.example.android.popularmoviesapp.Utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.onClickListener, AsyncTaskCompleteListener<ArrayList<MoviePosters>>{
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private TextView mErrorTextView;
    private RecyclerAdapter mRecyclerAdapter;
    private ArrayList<MoviePosters> MainMoviePosters;
    private String whatIsShowing = "popular";
    private ActionBar actionBar;
    public static final String PARCELABLE_CONTENT = "parcelable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.pb_recyclerview);
        mErrorTextView = (TextView)findViewById(R.id.tv_recycler_view_error);
        actionBar = getSupportActionBar();


        if (NetworkUtils.isInternetAvailable() && NetworkUtils.isNetworkAvailable(this)) {

            mErrorTextView.setVisibility(View.INVISIBLE);
            MainMoviePosters = new ArrayList<>();

            actionBar.setTitle(getResources().getString(R.string.title_popular_movies));
            getPosters("popular");
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_posters);
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
        if (MainMoviePosters != null)
        MainMoviePosters.clear();
        if (mRecyclerAdapter != null)
            mRecyclerAdapter.notifyDataSetChanged();
        mRecyclerAdapter = null;
        URL url = NetworkUtils.buildUrl(this, path);
        progressBar.setVisibility(View.VISIBLE);
        new TMBDQuery(this, this).execute(url);
    }

        @Override
        public void onTaskComplete(ArrayList<MoviePosters> result)
        {
            if (MainMoviePosters != null)
            MainMoviePosters.addAll(result);
            mRecyclerAdapter = new RecyclerAdapter(MainActivity.this, MainActivity.this, MainMoviePosters);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.num_columns_recycler_view));

            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            progressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }
    }



