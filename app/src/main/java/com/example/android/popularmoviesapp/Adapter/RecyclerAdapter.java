package com.example.android.popularmoviesapp.Adapter;

import android.content.Context;
import android.graphics.Rect;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesapp.R;
import com.example.android.popularmoviesapp.Model.MoviePosters;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Agbede Samuel D on 4/10/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    public interface onClickListener{
        void onRecyclerItemClick(int position);
    }

    private final String base_path_image_url = "https://image.tmdb.org/t/p/w500/";
    private onClickListener mOnClickListener;
    private Context context;
    private ArrayList<MoviePosters> mMoviePosters;
    public RecyclerAdapter(Context context, onClickListener mOnClickListener, ArrayList<MoviePosters> mMoviePosters){
        this.context = context;
        this.mOnClickListener = mOnClickListener;
        this.mMoviePosters = mMoviePosters;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.individual_recycler_views, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String ImageURLPath = base_path_image_url + mMoviePosters.get(position).getMovie_poster_path();
        Log.d("TAG", ImageURLPath);
        mMoviePosters.get(position).setMovie_poster_path(ImageURLPath);
        Picasso.with(context).load(ImageURLPath).placeholder(R.mipmap.ic_launcher).into(holder.mPosterImage);
    }

    @Override
    public int getItemCount() {
        return mMoviePosters.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mPosterImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mPosterImage = (ImageView)itemView.findViewById(R.id.iv_posters);
            mPosterImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onRecyclerItemClick(getAdapterPosition());
        }
    }


}
