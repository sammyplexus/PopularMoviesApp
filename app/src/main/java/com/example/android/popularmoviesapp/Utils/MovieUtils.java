package com.example.android.popularmoviesapp.Utils;

import com.example.android.popularmoviesapp.Model.MoviePosters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;


/**
 * Created by Agbede Samuel D on 4/14/2017.
 */

public class MovieUtils {

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
}
