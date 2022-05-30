package com.example.myapplication;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class portfolio_viewholder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
    private TextView symbol;
    private TextView price;
    private TextView profit;
    private TextView percent;
    public static CheckBox mWatchList_Checkbox;

    public onClickRecyclerListen mListener;
    public onLongClickRecyclerListen mLongListener;
    //public onClickMenuListen menuListen;
    PopupMenu popupMenu;


    public portfolio_viewholder(@NonNull View itemView,onClickRecyclerListen listener,onLongClickRecyclerListen longlistener) {
        super(itemView);
        mListener=listener;
        mLongListener=longlistener;
        symbol = itemView.findViewById(R.id.symbol);
        price = itemView.findViewById(R.id.price);
        profit = itemView.findViewById(R.id.profit);
        percent = itemView.findViewById(R.id.profitpercent);
        mWatchList_Checkbox = itemView.findViewById(R.id.watchlist_checkbox);

        if (home.GetAdapterDetails == 1 || home.GetAdapterDetails==0) {
            profit.setVisibility(itemView.GONE);
            percent.setVisibility(itemView.GONE);
        }
        else
        {
            profit.setVisibility(itemView.VISIBLE);
            percent.setVisibility(itemView.VISIBLE);
        }

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        mWatchList_Checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("added","YES");
                    home.watchlist_crypto.add(new portfolio_data(symbol.getText().toString(),0.00,0.0,0.0));

                }
            }
        });
    }


    public void setSymbol(String s) {
        this.symbol.setText(s);
    }


    public void setPrice(String s) {
        this.price.setText(s);
    }

    public void setProfit(String s) {
        if (Double.parseDouble(s) >= 0) {
            this.profit.setTextColor(Color.GREEN);
        } else this.profit.setTextColor(Color.RED);
        this.profit.setText(s);
    }

    public void setPercent(String s) {
        if (Double.parseDouble(s) >= 0) {
            this.percent.setTextColor(Color.GREEN);
        } else this.percent.setTextColor(Color.RED);
        this.percent.setText(s + "%");
    }


    @Override
    public void onClick(View v){
        Log.d("POSTION :",Integer.toString(getAdapterPosition()));
        mListener.onClickRecycler(getAdapterPosition(),v);
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d("LONG CLICK POSTION :",Integer.toString(getAdapterPosition()));
        mLongListener.onLongClickRecycler(getAdapterPosition(),v);
        return true;
    }


    public static interface onClickRecyclerListen {
        void onClickRecycler(int position,View v);

    }
    public static interface onLongClickRecyclerListen{
        void onLongClickRecycler(int position,View v);
    }

    public static void setWatchlist_checkbox(){
        //watchlist_checkbox.setChecked(true);
    }
}
