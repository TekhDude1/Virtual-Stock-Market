package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ThrowOnExtraProperties;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class portfolio_page_adapter extends RecyclerView.Adapter<portfolio_page_adapter.portfoliopage_viewholder> {


    ArrayList<portfolio_data> portfolioData;
    private Context mContext;
    private portfoliopage_viewholder.onClickRecyclerListen mOnClickRecyclerListen;
    private portfoliopage_viewholder.onLongClickRecyclerListen mOnLongClickRecyclerListen;
    private static DecimalFormat df2 = new DecimalFormat("#.##");


    public portfolio_page_adapter( Context mContext, ArrayList<portfolio_data> portfolioData,portfoliopage_viewholder.onClickRecyclerListen OnClickRecyclerListen, portfoliopage_viewholder.onLongClickRecyclerListen OnLongClickRecyclerListen) {
        this.portfolioData = portfolioData;
        this.mContext = mContext;
        this.mOnClickRecyclerListen=OnClickRecyclerListen;
        this.mOnLongClickRecyclerListen = OnLongClickRecyclerListen;
    }

    @NonNull
    @Override
    public portfoliopage_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.portfoliopage_boxes,parent,false);
        return new portfoliopage_viewholder(view,mOnClickRecyclerListen,mOnLongClickRecyclerListen);
    }

    @Override
    public void onBindViewHolder(@NonNull portfoliopage_viewholder holder, int position) {
        holder.buyprice.setText("Avg: "+(Double.toString(portfolioData.get(position).getProfit())));
        holder.buyqty.setText("Qty: "+Double.toString(portfolioData.get(position).getPercent()));
        holder.symbol.setText(portfolioData.get(position).getName());
        holder.invested.setText("Invested: "+ Double.toString(portfolioData.get(position).getPercent()*portfolioData.get(position).getProfit()));
        holder.ltp.setText("LTP: "+Double.toString(portfolioData.get(position).getPrice()));
        Double prof = (portfolioData.get(position).getPrice()-portfolioData.get(position).getProfit())*portfolioData.get(position).getPercent();
        if(prof>=0){
            holder.profit.setTextColor(Color.GREEN);
            holder.percent.setTextColor(Color.GREEN);
        }
        else{
            holder.profit.setTextColor(Color.RED);
            holder.percent.setTextColor(Color.RED);
        }
        holder.profit.setText(Double.toString(Double.parseDouble(df2.format(prof))));
        holder.percent.setText(Double.toString(Double.parseDouble(df2.format((Double)prof/(portfolioData.get(position).getPercent()*portfolioData.get(position).getProfit())*100)))+"%");

    }

    @Override
    public int getItemCount() {
        return portfolioData.size();
    }

    public static class portfoliopage_viewholder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private TextView buyprice,buyqty,symbol,invested,percent,profit,ltp;
        public onClickRecyclerListen mListener;
        public onLongClickRecyclerListen mLongListener;
        public portfoliopage_viewholder(@NonNull View itemView, onClickRecyclerListen listener, onLongClickRecyclerListen longlistener) {
            super(itemView);
            mListener=listener;
            mLongListener=longlistener;

            buyprice=itemView.findViewById(R.id.portfoliopage_buyprice);
            buyqty = itemView.findViewById(R.id.portfoliopage_buyquantity);
            symbol=itemView.findViewById(R.id.portfoliopage_symbol);
            invested = itemView.findViewById(R.id.portfoliopage_investment);
            percent = itemView.findViewById(R.id.portfoliopage_profitpercent);
            profit = itemView.findViewById(R.id.portfoliopage_profit);
            ltp = itemView.findViewById(R.id.portfoliopage_ltpchange);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClickRecycler(getAdapterPosition(),v);
        }

        @Override
        public boolean onLongClick(View v) {
            mLongListener.onLongClickRecycler(getAdapterPosition(),v);
            return true;
        }
        public static interface onClickRecyclerListen {
            void onClickRecycler(int position,View v);

        }
        public static interface onLongClickRecyclerListen{
            void onLongClickRecycler(int position,View v);
        }
    }

}
