package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class portfolio extends Fragment implements portfolio_page_adapter.portfoliopage_viewholder.onClickRecyclerListen,portfolio_page_adapter.portfoliopage_viewholder.onLongClickRecyclerListen {
    String symbolname;
    String autophoneno;
    Double investmentvalue=0.0,currentvalue=0.0;
    int count =0;

    private static DecimalFormat df2 = new DecimalFormat("#.##");


    TextView investment,current;
    public static ArrayList<portfolio_data> saved_watchlist;
    private ArrayList<portfolio_data> portfolioData;
    private portfolio_page_adapter portfolioAdapter;
    RecyclerView portfolio_savedcrypto;

    public static Handler handler_portfolio = new Handler();
    public static Runnable runnable_portfolio;
    public static Double profitdashboard;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.portfolio, container, false);
        Buy_Page.buypage_handler.removeCallbacks(Buy_Page.buypage_runnable);
        //HOOKS
        investment = view.findViewById(R.id.portfolio_investmentvalue);
        current = view.findViewById(R.id.portfolio_currentvalue);
        portfolio_savedcrypto = view.findViewById(R.id.portfolio_savedcrypto);
        RelativeLayout currentvalueLayout = (RelativeLayout) view.findViewById(R.id.portfollio_currentlayout);

        //Initialise
        home.GetAdapterDetails = 3;
        saved_watchlist = new ArrayList<>();
        portfolioData = new ArrayList<>();
        Intent intent = getActivity().getIntent();
        symbolname = intent.getStringExtra("symbol");
        autophoneno = intent.getStringExtra("phoneno");

        portfolioAdapter = new portfolio_page_adapter(getActivity(), portfolioData, this, this);
        portfolio_savedcrypto.setAdapter(portfolioAdapter);
        portfolio_savedcrypto.setLayoutManager(new LinearLayoutManager(getActivity()));

        getFireBaseData();
        portfolio_screen_update(view);

        currentvalueLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count%2!=0) {
                    Double portfoliooverallprofit = currentvalue - investmentvalue;
                    Double portfoliooverallprofitpercent = ((Double) portfoliooverallprofit / investmentvalue) * 100;
                    if (portfoliooverallprofit >= 0) current.setTextColor(Color.GREEN);
                    else current.setTextColor(Color.RED);
                    current.setText(Double.toString(Double.parseDouble(df2.format(portfoliooverallprofit))) + "\n" + Double.toString(Double.parseDouble(df2.format(portfoliooverallprofitpercent)))+"%");
                }
                else
                {
                    current.setTextColor(Color.BLACK);
                    current.setText(Double.toString(Double.parseDouble(df2.format(currentvalue))));
                }

            }
        });
        return view;
    }

    void portfolio_screen_update(View v){
        handler_portfolio.removeCallbacks(runnable_portfolio);
        investmentvalue=0.0;
        currentvalue=0.0;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://api.coindcx.com/exchange/ticker";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    for(int j=0;j<portfolioData.size();j++) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            if (jsonObject.getString("market").equals(portfolioData.get(j).getName())){
                                portfolioData.get(j).setPrice(Double.parseDouble(jsonObject.getString("last_price")));
                                investmentvalue+= (portfolioData.get(j).getProfit()*portfolioData.get(j).getPercent());
                                currentvalue+= (Double.parseDouble(jsonObject.getString("last_price"))*portfolioData.get(j).getPercent());
                                break;
                            }
                        }
                    }
                    investment.setText(Double.toString(investmentvalue));
                    if(count%2!=0) {
                        Double portfoliooverallprofit = currentvalue - investmentvalue;
                        Double portfoliooverallprofitpercent = ((Double) portfoliooverallprofit / investmentvalue) * 100;
                        if (portfoliooverallprofit >= 0) current.setTextColor(Color.GREEN);
                        else current.setTextColor(Color.RED);
                        current.setText(Double.toString(Double.parseDouble(df2.format(portfoliooverallprofit))) + "\n" + Double.toString(Double.parseDouble(df2.format(portfoliooverallprofitpercent)))+"%");
                    }
                    else
                    {
                        current.setTextColor(Color.BLACK);
                        current.setText(Double.toString(Double.parseDouble(df2.format(currentvalue))));
                    }
                    profitdashboard = Double.parseDouble(df2.format(currentvalue-investmentvalue));
                    Dashboard.bottom_profit.setText(Double.toString(profitdashboard));
                    portfolioAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volly Error", error.toString());

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                }
            }});
        requestQueue.add(jsonArrayRequest);
        refresh_portfolio(2000,v);
    }
    void refresh_portfolio(int milisec,View v){
        runnable_portfolio = new Runnable() {
            @Override
            public void run() {
                portfolio_screen_update(v);

            }
        };
        handler_portfolio.postDelayed(runnable_portfolio,milisec);
        portfolio_savedcrypto.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch(newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        handler_portfolio.postDelayed(runnable_portfolio, milisec);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        handler_portfolio.removeCallbacks(runnable_portfolio);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        handler_portfolio.removeCallbacks(runnable_portfolio);
                        break;
                }
            }
        });
    }

    @Override
    public void onClickRecycler(int position,View v) {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLongClickRecycler(int position, View v) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
        popupMenu.inflate(R.menu.home_recyclerview_menu);
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.watchlist_menu_remove:
                        handler_portfolio.removeCallbacks(runnable_portfolio);
                        portfolioData.remove(position);
                        saveFireBaseData();
                        break;
                    default:
                }
                handler_portfolio.post(runnable_portfolio);
                return false;
            }
        });
        popupMenu.show();

    }
    void getFireBaseData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://startup-ee05d-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(autophoneno).child("data").child("portfolio");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    portfolioData.clear();
                    Log.d("LOAD SUCCESS","YES");
                    int length = (int) snapshot.getChildrenCount();
                    Log.d("LENGTH",Integer.toString(length));
                    for(int i=0;i<length;i++) {
                        portfolioData.add(new portfolio_data(snapshot.child(Integer.toString(i)).child("name").getValue(String.class), snapshot.child(Integer.toString(i)).child("price").getValue(Double.class), snapshot.child(Integer.toString(i)).child("profit").getValue(Double.class), snapshot.child(Integer.toString(i)).child("percent").getValue(Double.class)));
                        Log.d("VALUES",portfolioData.get(i).getName().toString());
                    }
                    portfolio_savedcrypto.setAdapter(portfolioAdapter);
                    portfolio_savedcrypto.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void saveFireBaseData()
    {
        Log.d("SAVING","DATA");
        DatabaseReference reference = FirebaseDatabase.getInstance("https://startup-ee05d-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(autophoneno).child("data").child("portfolio");
        reference.setValue(portfolioData);
    }
}
