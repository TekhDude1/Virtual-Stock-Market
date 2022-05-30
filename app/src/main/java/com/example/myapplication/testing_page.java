package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class testing_page extends AppCompatActivity implements portfolio_viewholder.onClickRecyclerListen,portfolio_viewholder.onLongClickRecyclerListen{

    private ArrayList<portfolio_data> saved_crypto;
    private RecyclerView recyclerView;
    private portfolio_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_page);

        recyclerView = findViewById(R.id.testingpage_recycleview);
        saved_crypto = new ArrayList<>();
        saved_crypto.add(new portfolio_data("VANSH",0.0,0.0,0.0));
        saved_crypto.add(new portfolio_data("DILIP",1.0,3.0,2.0));

        adapter = new portfolio_adapter(this,saved_crypto,this,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onClickRecycler(int position, View v) {

    }

    @Override
    public void onLongClickRecycler(int position, View v) {

    }
}