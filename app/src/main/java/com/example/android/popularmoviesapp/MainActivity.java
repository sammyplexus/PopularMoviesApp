package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesapp.Model.MoviePosters;
import com.example.android.popularmoviesapp.Adapter.RecyclerAdapter;
import com.example.android.popularmoviesapp.Utils.MovieUtils;
import com.example.android.popularmoviesapp.Utils.NetworkUtils;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.onClickListener{
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar progressBar;
    private TextView mErrorTextView;
    private RecyclerAdapter mRecyclerAdapter;
    private ArrayList<MoviePosters> MainMoviePosters;
    private String whatIsShowing = "popular";
    private ActionBar actionBar;
    public static final String SERIALIZABLE_CONTENT = "serializable";

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
        intent.putExtra(SERIALIZABLE_CONTENT, moviePosters);
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
        mRecyclerAdapter = null;
        URL url = NetworkUtils.buildUrl(path);
        new TMBDQuery().execute(url);
    }

    class TMBDQuery extends AsyncTask<URL, Void, ArrayList<MoviePosters>>{
        private ArrayList<MoviePosters> mMoviePosters;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<MoviePosters> doInBackground(URL... params)
        {
            URL url = params[0];
            String network_response = " ";

            try {
                network_response = NetworkUtils.getResponseFromHttpUrl(url);
                Log.d("TAG", network_response);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (network_response != null)
                try {
                    mMoviePosters = MovieUtils.ConvertResulttoMoviePostersModel(network_response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            return mMoviePosters;
        }

        @Override
        protected void onPostExecute(ArrayList<MoviePosters> moviePosters) {

            MainMoviePosters.addAll(mMoviePosters);

            mRecyclerAdapter = new RecyclerAdapter(MainActivity.this, MainActivity.this, MainMoviePosters);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);

            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            progressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setAdapter(mRecyclerAdapter);

        }

    }
}
