package com.example.android.popularmoviesapp.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesapp.R;
import com.example.android.popularmoviesapp.Model.MoviePosters;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Agbede Samuel D on 4/10/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    public interface onClickListener{
        void onRecyclerItemClick(int position);
    }

    public static String base_path_image_url = "https://image.tmdb.org/t/p/w500/";
    private onClickListener mOnClickListener;
    private Context context;
    private ArrayList<MoviePosters> mMoviePosters;
    public RecyclerAdapter(Context context, onClickListener mOnClickListener){
        this.context = context;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_recycler_views, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

            String ImageURLPath = base_path_image_url + mMoviePosters.get(position).getMovie_poster_path();
            mMoviePosters.get(position).setMovie_poster_path(ImageURLPath);
            Picasso.with(context).load(ImageURLPath).placeholder(R.mipmap.ic_launcher).into(holder.mPosterImage);

    }

    @Override
    public int getItemCount() {
            if (mMoviePosters == null)
                return 0;
            return mMoviePosters.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mPosterImage;
        private ViewHolder(View itemView) {
            super(itemView);
            mPosterImage = (ImageView)itemView.findViewById(R.id.iv_posters);
            mPosterImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onRecyclerItemClick(getAdapterPosition());
        }
    }

    public void setData(ArrayList<MoviePosters> mMoviePosters){
        this.mMoviePosters = mMoviePosters;
        notifyDataSetChanged();
    }


    public ArrayList<MoviePosters> getData(){
        return this.mMoviePosters;
    }


}
