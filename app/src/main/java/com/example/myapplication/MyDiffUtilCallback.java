package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyDiffUtilCallback extends DiffUtil.Callback {
    ArrayList<portfolio_data> newDataList= new ArrayList<>();
    ArrayList<portfolio_data> oldDataList = new ArrayList<>();
    //private static DecimalFormat df2 = new DecimalFormat("#.##");
    private static DecimalFormat df2 = new DecimalFormat("#.############");


    public MyDiffUtilCallback(ArrayList<portfolio_data> oldDataList, ArrayList<portfolio_data> newDataList) {
        this.newDataList = newDataList;
        this.oldDataList = oldDataList;
    }

    @Override
    public int getOldListSize() {
        if(oldDataList!=null) {
            return oldDataList.size();
        }
        else return 0;
    }

    @Override
    public int getNewListSize() {
        if(newDataList!=null) {
            return newDataList.size();
        }
        else return 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        int result =  newDataList.get(newItemPosition).compareTo(oldDataList.get(oldItemPosition));
        if(result==0)return true;
        return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        portfolio_data newData = newDataList.get(newItemPosition);
        portfolio_data oldData = oldDataList.get(oldItemPosition);

        Bundle bundle = new Bundle();

        if(!(newData.getPrice()==oldData.getPrice())) {
            bundle.putString("newprice", Double.toString(newData.getPrice()));
            bundle.putString("symbolname",newData.getName());
            bundle.putString("profit",Double.toString(Double.parseDouble(df2.format(newData.getProfit()))));
            bundle.putString("percent",Double.toString(Double.parseDouble(df2.format(newData.getPercent()))));
        }
        if(bundle.size()==0)return  null;

        return bundle;



    }
}
