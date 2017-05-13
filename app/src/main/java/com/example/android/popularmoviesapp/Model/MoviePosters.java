package com.example.android.popularmoviesapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Agbede Samuel D on 4/14/2017.
 */

public class MoviePosters implements Parcelable{
    public MoviePosters(){}

    private MoviePosters(Parcel in){
        movie_poster_path = in.readString();
        movie_overview = in.readString();
        movie_release_date = in.readString();
        movie_id = in.readInt();
        movie_original_title = in.readString();
        movie_backdrop_path = in.readString();
        movie_vote_average = in.readDouble();

    }
    String movie_poster_path;
    String movie_overview;
    String movie_release_date;
    int movie_id;
    String movie_original_title;
    String movie_backdrop_path;

    double movie_vote_average;

    public static final Creator<MoviePosters> CREATOR = new Creator<MoviePosters>() {
        @Override
        public MoviePosters createFromParcel(Parcel in) {
            return new MoviePosters(in);
        }

        @Override
        public MoviePosters[] newArray(int size) {
            return new MoviePosters[size];
        }
    };

    public String getMovie_poster_path() {
        return movie_poster_path;
    }

    public void setMovie_poster_path(String movie_poster_path) {
        this.movie_poster_path = movie_poster_path;
    }

    public String getMovie_overview() {
        return movie_overview;
    }

    public void setMovie_overview(String movie_overview) {
        this.movie_overview = movie_overview;
    }

    public String getMovie_release_date() {
        return movie_release_date;
    }

    public void setMovie_release_date(String movie_release_date) {
        this.movie_release_date = movie_release_date;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getMovie_original_title() {
        return movie_original_title;
    }

    public void setMovie_original_title(String movie_original_title) {
        this.movie_original_title = movie_original_title;
    }

    public String getMovie_backdrop_path() {
        return movie_backdrop_path;
    }

    public void setMovie_backdrop_path(String movie_backdrop_path) {
        this.movie_backdrop_path = movie_backdrop_path;
    }

    public double getMovie_vote_average() {
        return movie_vote_average;
    }

    public void setMovie_vote_average(double movie_vote_average) {
        this.movie_vote_average = movie_vote_average;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movie_poster_path);
        dest.writeString(movie_overview);
        dest.writeString(movie_release_date);
        dest.writeInt(movie_id);
        dest.writeString(movie_original_title);
        dest.writeString(movie_backdrop_path);
        dest.writeDouble(movie_vote_average);

    }
}

