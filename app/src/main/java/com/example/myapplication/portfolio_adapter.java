package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class portfolio_adapter extends RecyclerView.Adapter<portfolio_viewholder>{
    private Context context;
    private ArrayList<portfolio_data> arrayList;
    private portfolio_viewholder.onClickRecyclerListen mOnClickRecyclerListen;
    private portfolio_viewholder.onLongClickRecyclerListen mOnLongClickRecyclerListen;
    //private portfolio_viewholder.onClickMenuListen mOnMenuListen;

   // private static DecimalFormat df2 = new DecimalFormat("#.##");
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public portfolio_adapter(Context context, ArrayList<portfolio_data> arrayList,portfolio_viewholder.onClickRecyclerListen OnClickRecyclerListen,portfolio_viewholder.onLongClickRecyclerListen OnLongClickRecyclerListen) {
        this.context = context;
        this.arrayList = arrayList;
        this.mOnClickRecyclerListen=OnClickRecyclerListen;
        this.mOnLongClickRecyclerListen = OnLongClickRecyclerListen;

       // this.mOnMenuListen=mOnMenuListen;
       // this.onLongClickRecyclerListen=OnLongClickRecyclerListen;
    }
    @Override
    public portfolio_viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_boxes, parent, false);
        return new portfolio_viewholder(view,mOnClickRecyclerListen,mOnLongClickRecyclerListen);
    }
    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();

    }

    @Override
    public void onBindViewHolder(portfolio_viewholder holder, int position) {
        try {
            portfolio_data dat = arrayList.get(position);
            holder.setSymbol(dat.getName());
            holder.setPrice(Double.toString(Double.parseDouble(df2.format(dat.getPrice()))));
            holder.setProfit(Double.toString(Double.parseDouble(df2.format(dat.getProfit()))));
            holder.setPercent(Double.toString(Double.parseDouble(df2.format(dat.getPercent()))));

        }catch (IndexOutOfBoundsException e){
            Log.d("INDEX OUT OF BOUND : ",e.toString());
        }catch (Exception e){
            Log.d("EXCEPTION TIME: ",e.toString());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull portfolio_viewholder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads);
        else{
            try {
                Bundle bundle = (Bundle) payloads.get(0);
                for (String key : bundle.keySet()) {
                    if (key.equals("newprice")) {
                        // Log.d("PRICE NEW UPDATED", bundle.getString("newprice", ""));
                        Log.d("NEW PRICE SET","DONE SUCCESSFULLY");
                        holder.setPrice(bundle.getString("newprice", ""));
                        holder.setSymbol(bundle.getString("symbolname",""));
                        holder.setProfit(bundle.getString("profit",""));
                        holder.setPercent(bundle.getString("percent",""));
                        bundle.clear();
                    }

                }
            }catch (IndexOutOfBoundsException e){
                Log.d("INDEX OUT BOUND : ",e.toString());
            }catch (Exception e){
                Log.d("EXCEPT TIME: ",e.toString());
            }
        }
    }

    public void updateData(ArrayList<portfolio_data> newArrayList){
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallback(arrayList,newArrayList));
        diffResult.dispatchUpdatesTo(this);
       // Log.d("OLD PRICE,NEW PRICE",arrayList.get(0).getPrice()+","+newArrayList.get(0).getPrice());
        arrayList.clear();
        arrayList.addAll(newArrayList);
    }

    public void SetChechkbox(){
        //portfolio_viewholder.mWatchList_Checkbox.setChecked(true);
    }

}
