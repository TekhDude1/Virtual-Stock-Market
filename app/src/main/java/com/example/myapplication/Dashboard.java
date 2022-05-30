package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.SettingInjectorService;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class Dashboard extends AppCompatActivity{
    public static int order_add_count=0;
    public static TextView bottom_profit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Intent intent = getIntent();

        bottom_profit = findViewById(R.id.fixed2);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        int startscreen=0;
        String temp = intent.getStringExtra("startingpage");
        if(temp!=null)startscreen = Integer.parseInt(temp);
        Log.d("START SCREEN",Integer.toString(startscreen));
        switch (startscreen){
            case 1: getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new home()).commit();
                    break;
            case 2: getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new orders()).commit();
                    break;
            case 3: getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new portfolio()).commit();
                    break;
            case 4: getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new settings()).commit();
                    break;
            default:
                Log.d("ERROR","SCREEN START IS DEFAULT");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new home()).commit();

        }


        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.home:
                    orders.handler_orders.removeCallbacks(orders.runnable_orders);
                    portfolio.handler_portfolio.removeCallbacks(portfolio.runnable_portfolio);
                    selectedFragment = new home();
                    break;
                case R.id.orders:
                    home.handler.removeCallbacks(home.runnable);
                    home.handler_watchlist.removeCallbacks(home.runnable_watchlist);
                    portfolio.handler_portfolio.removeCallbacks(portfolio.runnable_portfolio);
                    selectedFragment = new orders();
                    break;
                case R.id.portfolio:
                    orders.handler_orders.removeCallbacks(orders.runnable_orders);
                    home.handler.removeCallbacks(home.runnable);
                    home.handler_watchlist.removeCallbacks(home.runnable_watchlist);
                    selectedFragment=new portfolio();
                    break;
                case R.id.profile:
                    orders.handler_orders.removeCallbacks(orders.runnable_orders);
                    home.handler.removeCallbacks(home.runnable);
                    home.handler_watchlist.removeCallbacks(home.runnable_watchlist);
                    portfolio.handler_portfolio.removeCallbacks(portfolio.runnable_portfolio);
                    selectedFragment=new settings();

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout,selectedFragment).commit();

            return true;
        }
    };
}