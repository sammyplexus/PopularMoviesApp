package com.example.android.popularmoviesapp.Utils;

import android.os.Parcel;
import android.util.Log;

import com.example.android.popularmoviesapp.Model.MoviePosters;
import com.example.android.popularmoviesapp.Model.MovieReviews;
import com.example.android.popularmoviesapp.Model.MovieTrailers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Agbede Samuel D on 4/14/2017.
 */

public final class MovieUtils {
    private MovieUtils(){}

    public static ArrayList<MoviePosters> ConvertResulttoMoviePostersModel(String URLResults) throws JSONException {
        ArrayList<MoviePosters> mMoviePosters = new ArrayList<>();

        final String MAIN_JSON_ARRAY = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_VOTE_AVERAGE = "vote_average";
        final String MOVIE_POSTER_URL = "poster_path";
        final String MOVIE_BACKDROP_URL = "backdrop_path";
        final String MOVIE_RELEASE_DATE = "release_date";

        JSONObject mainJsonObject = new JSONObject(URLResults);
        JSONArray results = mainJsonObject.getJSONArray(MAIN_JSON_ARRAY);

        for (int i = 0; i < results.length(); i++){
            JSONObject jsonObject = results.getJSONObject(i);
            MoviePosters posters = new MoviePosters();

            int movie_id = jsonObject.getInt(MOVIE_ID);
            String movie_title = jsonObject.getString(MOVIE_TITLE);
            String movie_poster_path = jsonObject.getString(MOVIE_POSTER_URL);
            String movie_overview = jsonObject.getString(MOVIE_OVERVIEW);
            String movie_backdrop_path = jsonObject.getString(MOVIE_BACKDROP_URL);
            double movie_vote_average = jsonObject.getDouble(MOVIE_VOTE_AVERAGE);
            String movie_release_date = jsonObject.getString(MOVIE_RELEASE_DATE);

            posters.setMovie_id(movie_id);
            posters.setMovie_original_title(movie_title);
            posters.setMovie_poster_path(movie_poster_path);
            posters.setMovie_overview(movie_overview);
            posters.setMovie_backdrop_path(movie_backdrop_path);
            posters.setMovie_vote_average(movie_vote_average);
            posters.setMovie_release_date(movie_release_date);

            mMoviePosters.add(posters);
        }

        return mMoviePosters;
    }

    public static MovieReviews ConvertResulttoMovieReviewsModel(String URLResults) throws JSONException
    {
        MovieReviews mMovieReviews = new MovieReviews();

        final String MAIN_JSON_ARRAY = "results";
        final String MOVIE_ID = "id";
        final String REVIEW_ID = "id";
        final String MOVIE_REVIEW_AUTHOR = "author";
        final String MOVIE_REVIEW_CONTENT = "content";
        final String MOVIE_REVIEW_URL = "url";

        JSONObject mainJsonObject = new JSONObject(URLResults);
        JSONArray results = mainJsonObject.getJSONArray(MAIN_JSON_ARRAY);

        for (int i = 0; i < results.length(); i++){
            JSONObject jsonObject = results.getJSONObject(i);

            String review_id = jsonObject.getString(REVIEW_ID);
            String review_content = jsonObject.getString(MOVIE_REVIEW_CONTENT);
            String review_author = jsonObject.getString(MOVIE_REVIEW_AUTHOR);
            String review_url = jsonObject.getString(MOVIE_REVIEW_URL);

            mMovieReviews.setReview_id(review_id);
            mMovieReviews.setReview_author(review_author);
            mMovieReviews.setReview_content(review_content);
            mMovieReviews.setReview_url(review_url);

        }

        return mMovieReviews;
    }

    public static ArrayList<MovieTrailers> ConvertResulttoMovieTrailersModel(String URLResults) throws JSONException
    {
        ArrayList<MovieTrailers> mMovieTrailers = new ArrayList<>();

        final String MAIN_JSON_ARRAY = "results";
        final String TRAILER_ID = "id";
        final String MOVIE_TRAILER_KEY = "key";
        final String MOVIE_TRAILER_NAME = "name";

        JSONObject mainJsonObject = new JSONObject(URLResults);
        JSONArray results = mainJsonObject.getJSONArray(MAIN_JSON_ARRAY);
        Log.d("ZA2", results.length()+"");

        if (results.length() > 0)
        {
            for (int i = 0; i < results.length(); i++)
            {
                MovieTrailers movieTrailers = new MovieTrailers();
                JSONObject jsonObject = results.getJSONObject(i);

                String trailer_id = jsonObject.getString(TRAILER_ID);
                String trailer_key = jsonObject.getString(MOVIE_TRAILER_KEY);
                String trailer_name = jsonObject.getString(MOVIE_TRAILER_NAME);

                movieTrailers.setId(trailer_id);
                movieTrailers.setKey(trailer_key);
                movieTrailers.setName(trailer_name);

                mMovieTrailers.add(movieTrailers);
            }
        }

        return mMovieTrailers;
    }
}
