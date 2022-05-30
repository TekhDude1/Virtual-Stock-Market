package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowAnimationFrameStats;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class home extends Fragment implements portfolio_viewholder.onClickRecyclerListen, recyclerview_watchlist_adapter.ViewHolder.OnClickWatchlistNo,portfolio_viewholder.onLongClickRecyclerListen {
    RecyclerView watchlist;
    RecyclerView watchlist_no;
    ArrayList<String> watchlist_count;
    EditText searchaddcrypto;
    ProgressBar loading;
    CheckBox checkBox;

    //CLASS VARIABLES
    ArrayList<portfolio_data> temp_crptolist;
    String phoneno;
    ArrayList<portfolio_data> searchbar_cryptototal= new ArrayList<>();
    portfolio_adapter searchbar_adapter;
    portfolio_adapter homescreen_adapter;
    int recyclerview_position;
    public static Handler handler = new Handler();
    public static Runnable runnable;
    public static  Handler handler_watchlist = new Handler();
    public static Runnable runnable_watchlist;

    //GLOBAL VARIABLES
    public static int GetAdapterDetails,CURRENT_watchlistno_position;
    public static String temp;
    public static ArrayList<portfolio_data> watchlist_crypto;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home,container,false);

        Intent getData= getActivity().getIntent();
        handler_watchlist.removeCallbacks(runnable_watchlist);
        phoneno = getData.getStringExtra("phoneno");
        watchlist=view.findViewById(R.id.home_watchlist);
        watchlist_no=view.findViewById(R.id.home_watchlistno);
        searchaddcrypto=view.findViewById(R.id.searchbar);
        loading=view.findViewById(R.id.loading);
        checkBox = view.findViewById(R.id.watchlist_checkbox);

        temp_crptolist= new ArrayList<>();
        watchlist_crypto = new ArrayList<>();
        watchlist_count = new ArrayList<>();
        searchbar_adapter=new portfolio_adapter(getActivity(), searchbar_cryptototal,this,this);
        homescreen_adapter=new portfolio_adapter(getActivity(),watchlist_crypto,this,this);

        watchlist_count.add("Watchlist 1");
        //watchlist_count.add("Watchlist 2");
       // watchlist_count.add("Watchlist 3");
       // watchlist_count.add("Watchlist 4");
       // watchlist_count.add("Watchlist 5");

        watchlist_no.setAdapter(new recyclerview_watchlist_adapter(getActivity(),watchlist_count,this));
        watchlist_no.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        //saveFireBaseData();
        GetAdapterDetails=0;
        watchlist.setAdapter(homescreen_adapter);
        watchlist.setLayoutManager(new LinearLayoutManager(getActivity()));

        getFireBaseData();
        home_screen_update(view);


        // checkBox.setChecked(true);


        searchaddcrypto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>=1)
                {
                    GetAdapterDetails=1;
                    loading.setVisibility(view.VISIBLE);
                    watchlist.setAdapter(searchbar_adapter);
                    search_add_crypto(view,s.toString());

                }
                if(s.length()==0){
                    GetAdapterDetails=0;
                    handler.removeCallbacks(runnable);
                    watchlist.setAdapter(homescreen_adapter);
                    handler_watchlist.removeCallbacks(runnable_watchlist);
                    home_screen_update(view);
                }
            }
        });

        return view;
    }

    void getFireBaseData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://startup-ee05d-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(phoneno).child("data");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.d("LOAD SUCCESS","UES");
                    int length = (int) snapshot.child(watchlist_count.get(CURRENT_watchlistno_position)).getChildrenCount();
                    Log.d("LENGTH",Integer.toString(length));
                    watchlist_crypto.clear();
                    for(int i=0;i<length;i++) {
                        watchlist_crypto.add(new portfolio_data(snapshot.child(watchlist_count.get(CURRENT_watchlistno_position)).child(Integer.toString(i)).child("name").getValue(String.class), snapshot.child(watchlist_count.get(CURRENT_watchlistno_position)).child(Integer.toString(i)).child("price").getValue(Double.class), snapshot.child(watchlist_count.get(CURRENT_watchlistno_position)).child(Integer.toString(i)).child("profit").getValue(Double.class), snapshot.child(watchlist_count.get(CURRENT_watchlistno_position)).child(Integer.toString(i)).child("percent").getValue(Double.class)));
                        Log.d("VALUES",watchlist_crypto.get(i).getName().toString());
                    }
                    //watchlist_crypto.set(snapshot.child(watchlist_count.get(CURRENT_watchlistno_position)).getValue(Integer.class),snapshot.child(watchlist_count.get(CURRENT_watchlistno_position)).child("0").getValue(portfolio_data.class));
                    watchlist.setAdapter(homescreen_adapter);
                    watchlist.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        FirebaseDatabase rootNode;
        DatabaseReference reference;
        rootNode=FirebaseDatabase.getInstance("https://startup-ee05d-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference=rootNode.getReference("users");
        reference.child(phoneno).child("data").child(watchlist_count.get(CURRENT_watchlistno_position)).setValue(watchlist_crypto);
    }


    void home_screen_update(View v){
        handler_watchlist.removeCallbacks(runnable_watchlist);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://api.coindcx.com/exchange/ticker";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    ArrayList<portfolio_data> upDateList= new ArrayList<>();
                    for(int j=0;j<watchlist_crypto.size();j++) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            if (jsonObject.getString("market").equals(watchlist_crypto.get(j).getName())){
                                upDateList.add(new portfolio_data(watchlist_crypto.get(j).getName(),Double.parseDouble(jsonObject.getString("last_price")),0.0,0.0));
                                break;
                            }
                        }
                    }
                    Log.d("PRICE","UPDATED");
                    homescreen_adapter.updateData(upDateList);
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
        refresh_watchlist(2000,v);
    }
    void search_add_crypto(View v,String text){
        handler.removeCallbacks(runnable);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://api.coindcx.com/exchange/ticker";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                temp_crptolist.clear();
                try {
                    loading.setVisibility(v.GONE);
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        if (!jsonObject.getString("market").equals("BTCINR_insta") && jsonObject.getString("market").contains(text.toUpperCase(Locale.ROOT))) {
                            temp_crptolist.add(new portfolio_data(jsonObject.getString("market"), Double.parseDouble(jsonObject.getString("last_price")), 0.0, 0.0));
                            temp=jsonObject.getString("market");
                        }
                    }
                    searchbar_adapter.updateData(temp_crptolist);
                } catch (JSONException e) {
                    Log.d("JSONARRAY", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY ERROR",error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
        refresh_search_add_crypto(1000,v,text);

    }

    void refresh_watchlist(int milisec,View v){
        runnable_watchlist = new Runnable() {
            @Override
            public void run() {
                home_screen_update(v);
            }
        };
        handler_watchlist.postDelayed(runnable_watchlist,milisec);
        watchlist.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch(newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        handler_watchlist.postDelayed(runnable_watchlist, milisec);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        handler_watchlist.removeCallbacks(runnable_watchlist);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        handler_watchlist.removeCallbacks(runnable_watchlist);
                        break;
                }
            }
        });
    }
    void refresh_search_add_crypto(int milisec,View v,String text){
        runnable = new Runnable() {
            @Override
            public void run() {
                search_add_crypto(v,text);
            }
        };
        handler.postDelayed(runnable,milisec);
        watchlist.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch(newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        handler.postDelayed(runnable, milisec);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        handler.removeCallbacks(runnable);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        handler.removeCallbacks(runnable);
                        break;
                }
            }
        });
    }

    public void buyselldialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.buy_sell_bottomsheet);

        TextView buyselldialog_symbol = dialog.findViewById(R.id.buyselldialogbox_symbol);
        TextView buyselldialog_price = dialog.findViewById(R.id.buyselldialogbox_price);
        TextView buyselldialog_percent = dialog.findViewById(R.id.buyselldialogbox_pricepercent);
        MaterialButton buy_button = dialog.findViewById(R.id.buyselldialogbox_buy);
        MaterialButton sell_button = dialog.findViewById(R.id.buyselldialogbox_sell);

        buyselldialog_symbol.setText(watchlist_crypto.get(recyclerview_position).getName());
        buyselldialog_price.setText(Double.toString(watchlist_crypto.get(recyclerview_position).getPrice()));
       // buyselldialog_percent.setText(Double.toString(watchlist_crypto.get(recyclerview_position).get()));

        buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Buy_Page.class);
                intent.putExtra("symbol",watchlist_crypto.get(recyclerview_position).getName());
                intent.putExtra("phoneno",phoneno);

                startActivity(intent);
            }
        });
        sell_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Sell_Page.class);
                intent.putExtra("symbol",watchlist_crypto.get(recyclerview_position).getName());
                startActivity(intent);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().getAttributes().windowAnimations=R.style.buyselldialog_Animation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    @Override
    public void onClickRecycler(int position,View v) {
        recyclerview_position=position;
        if(!temp_crptolist.isEmpty() && GetAdapterDetails==1){
            watchlist_crypto.add(temp_crptolist.get(position));
            Log.d("TESTING 1 ","LETS CHECK WORKING");
            watchlist_crypto = removeDuplicates(watchlist_crypto);
            saveFireBaseData();
        }
        if(GetAdapterDetails==0)
        {
            buyselldialog();
        }
    }

    @Override
    public void onClickWatchlistRecycler(int position) {
        CURRENT_watchlistno_position = position;
        watchlist_no.setAdapter(new recyclerview_watchlist_adapter(getActivity(),watchlist_count,this));
        watchlist_no.scrollToPosition(position);
    }

    public ArrayList<portfolio_data> removeDuplicates(ArrayList<portfolio_data> list)
    {
        ArrayList<portfolio_data> newList = new ArrayList<portfolio_data>();
        int error=0;
        for (int i=0;i<list.size();i++) {
            for(int j=0;j<newList.size();j++)
            {
                if (newList.get(j).getName().contains(list.get(i).getName())) {
                    error=1;
                    break;
                }
            }
            if(error==0){
                newList.add(list.get(i));
            }
            error=0;
        }
        return newList;
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
                        watchlist_crypto.remove(position);
                        saveFireBaseData();
                    break;
                    default:
                }
                return false;
            }
        });
        popupMenu.show();

    }
}
