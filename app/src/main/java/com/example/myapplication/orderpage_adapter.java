package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class orderpage_adapter extends RecyclerView.Adapter<orderpage_adapter.orderpage_ViewHolder> {
    ArrayList<order_data> current_orders;
    private Context mContext;
    private orderpage_ViewHolder.OnClickOrder mOrderPageListener;
    private orderpage_ViewHolder.OnLongClickOrder mLongClickOrderListener;

    public orderpage_adapter(Context mContext, ArrayList<order_data> current_orders, orderpage_ViewHolder.OnClickOrder mOrderPageListener,orderpage_ViewHolder.OnLongClickOrder mLongClickOrderListener) {
        this.current_orders = current_orders;
        this.mContext = mContext;
        this.mOrderPageListener = mOrderPageListener;
        this.mLongClickOrderListener = mLongClickOrderListener;
    }

    @NonNull
    @Override

    public orderpage_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orderpage_boxes,parent,false);
        return new orderpage_ViewHolder(view,mOrderPageListener,mLongClickOrderListener);
    }

    @Override
    public void onBindViewHolder(@NonNull orderpage_ViewHolder holder, int position) {
        if(current_orders.get(position).getOrdertype().equals("BUY"))holder.buytype.setTextColor(Color.parseColor("#007BFF"));
        if(current_orders.get(position).getOrdertype().equals("SELL"))holder.buytype.setTextColor(Color.RED);
        holder.buytype.setText(current_orders.get(position).getOrdertype());
        holder.symbol.setText(current_orders.get(position).getSymbol());
        holder.buyprice.setText(Double.toString(current_orders.get(position).getBuyprice()));
        holder.buyquantity.setText(Double.toString(current_orders.get(position).getBuyquantity()));
        holder.price.setText(Double.toString(current_orders.get(position).getPrice()));
        if(current_orders.get(position).getPercent()>=0)holder.pricepercent.setTextColor(Color.GREEN);
        else holder.pricepercent.setTextColor(Color.RED);
        holder.pricepercent.setText(Double.toString(current_orders.get(position).getPercent()));


    }

    @Override
    public int getItemCount() {
        return current_orders.size();
    }

    public static class orderpage_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        TextView buytype;
        TextView symbol;
        TextView buyprice;
        TextView buyquantity;
        TextView price;
        TextView pricepercent;
        public OnClickOrder mOrderPageListener;
        public OnLongClickOrder mLongClickOrderListener;
        public orderpage_ViewHolder(View itemView, OnClickOrder m1OrderPageListener,OnLongClickOrder m1LongClickOrderListener) {
            super(itemView);
            buytype = itemView.findViewById(R.id.orderpage_type);
            symbol = itemView.findViewById(R.id.orderpage_symbol);
            buyprice = itemView.findViewById(R.id.orderpage_buyprice);
            buyquantity = itemView.findViewById(R.id.orderpage_buyquantity);
            price = itemView.findViewById(R.id.orderpage_price);
            pricepercent = itemView.findViewById(R.id.orderpage_pricepercent);
            mOrderPageListener=m1OrderPageListener;
            mLongClickOrderListener = m1LongClickOrderListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOrderPageListener.onClickOrderRecycler(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mLongClickOrderListener.onLongClickOrderRecycler(getAdapterPosition(),v);
            return true;
        }

        public static interface OnClickOrder {
            void onClickOrderRecycler(int position);
        }
        public static interface OnLongClickOrder{
            void onLongClickOrderRecycler(int position,View v);
        }
    }
}
