package com.example.android.popularmoviesapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmoviesapp.Model.MoviePosters;
import com.example.android.popularmoviesapp.Utils.MovieUtils;
import com.example.android.popularmoviesapp.Utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Agbede Samuel D on 4/15/2017.
 */

    class TMBDQuery extends AsyncTask<URL, Void, ArrayList<MoviePosters>> {
        private Context context;
        private AsyncTaskCompleteListener<ArrayList<MoviePosters>> taskCompleteListener;
        private ArrayList<MoviePosters> mMoviePosters;

        public TMBDQuery (Context context, AsyncTaskCompleteListener<ArrayList<MoviePosters>> taskCompleteListener){
            this.context = context;
            this.taskCompleteListener = taskCompleteListener;
        }

        @Override
        protected ArrayList<MoviePosters> doInBackground(URL... params) {
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
                    Log.d("TAG", mMoviePosters.size() + " ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            return mMoviePosters;
        }

        @Override
        protected void onPostExecute(ArrayList<MoviePosters> moviePosters)
        {
            taskCompleteListener.onTaskComplete(moviePosters);
        }
    }

