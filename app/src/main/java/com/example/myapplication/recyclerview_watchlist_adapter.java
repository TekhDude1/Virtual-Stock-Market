package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class recyclerview_watchlist_adapter extends RecyclerView.Adapter<recyclerview_watchlist_adapter.ViewHolder> {

    ArrayList<String> watchlist_no;
    public ViewHolder.OnClickWatchlistNo mWatchlistListener;
    private Context mContext;

    public recyclerview_watchlist_adapter(Context mContext,ArrayList<String> watchlist_no,ViewHolder.OnClickWatchlistNo mWatchlistListener) {
        this.watchlist_no = watchlist_no;
        this.mContext = mContext;
        this.mWatchlistListener=mWatchlistListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_watchlist,parent,false);
        return new ViewHolder(view,mWatchlistListener);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.watchlistno.setText(watchlist_no.get(position));
        if(home.CURRENT_watchlistno_position==position){
            holder.cardView.setStrokeWidth(5);
        }
        else {
            holder.cardView.setStrokeColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return watchlist_no.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        MaterialTextView watchlistno;
        MaterialCardView cardView;
        public OnClickWatchlistNo mWatchlistListener;
        public ViewHolder(View itemView,OnClickWatchlistNo m1WatchlistListener) {
            super(itemView);
            cardView = itemView.findViewById(R.id.recyclerview_watchlist_number_cardbox);
            watchlistno=itemView.findViewById(R.id.recyclerview_watchlist_number);
            mWatchlistListener=m1WatchlistListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mWatchlistListener.onClickWatchlistRecycler(getAdapterPosition());
        }
        public static interface OnClickWatchlistNo {
            void onClickWatchlistRecycler(int position);
        }
    }


}
