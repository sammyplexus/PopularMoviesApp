package com.example.android.popularmoviesapp.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesapp.Data.PMAContract;
import com.example.android.popularmoviesapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Agbede Samuel D on 5/12/2017.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FaViewHolder> {
    public interface onFavItemClickListener {
        void onFavItemClick(int position);
    }
    private Context context;
    private Cursor cursor;
    private onFavItemClickListener listener;

    public FavoriteAdapter(Context context, Cursor cursor, onFavItemClickListener listener){
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }


    @Override
    public FaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.individual_recycler_views, parent, false);
        return new FaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FaViewHolder holder, int position) {
        cursor.moveToPosition(position);
        int image_poster_column_count = cursor.getColumnIndex(PMAContract.PMAEntry.COLUMN_MOVIE_POSTER);
        Picasso.with(context).load(cursor.getString(image_poster_column_count)).placeholder(R.mipmap.ic_launcher).into(holder.mPosterImage);

    }

    @Override
    public int getItemCount() {
        if (cursor == null)
            return 0;
        return cursor.getCount();
    }


    public void setCursor(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    class FaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mPosterImage;
        FaViewHolder(View itemView) {
            super(itemView);
            mPosterImage = (ImageView) itemView.findViewById(R.id.iv_posters);
            mPosterImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onFavItemClick(getAdapterPosition());
        }
    }

}
