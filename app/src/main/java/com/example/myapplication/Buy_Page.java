package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
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

public class Buy_Page extends AppCompatActivity {
    String symbolname;
    String autophoneno;
    String temp="";

    MaterialTextView price;
    MaterialTextView pricepercent;
    MaterialTextView dayHigh;
    MaterialTextView dayLow;
    MaterialTextView volume;
    MaterialTextView bid;
    MaterialTextView ask;
    TextView orderamt;

    TextInputLayout buyprice;
    TextInputLayout buyquantity;
    TextInputEditText buyprice_2;

    ProgressBar progressBar;

    public static Handler buypage_handler= new Handler();
    public static Runnable buypage_runnable;
    private ArrayList<portfolio_data> portfolioData;

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_page);

        Intent intent=getIntent();
        home.handler.removeCallbacks(home.runnable);
        home.handler_watchlist.removeCallbacks(home.runnable_watchlist);

        price = findViewById(R.id.buypage_price);
        pricepercent = findViewById(R.id.buypage_percent);
        dayHigh = findViewById(R.id.buypage_24hrhigh);
        dayLow = findViewById(R.id.buypage_24hrlow);
        volume = findViewById(R.id.buypage_volume);
        bid = findViewById(R.id.buypage_bid);
        ask = findViewById(R.id.buypage_ask);
        buyprice = findViewById(R.id.buypage_buyprice);
        buyquantity = findViewById(R.id.buypage_buyquantity);
        buyprice_2 = findViewById(R.id.buypage_buyquantity_2);
        orderamt=findViewById(R.id.buypage_orderamt);
        progressBar = findViewById(R.id.progressbar);
        SwipeButton buybutton = findViewById(R.id.buypage_buybutton);


        symbolname = intent.getStringExtra("symbol");
        autophoneno = intent.getStringExtra("phoneno");
        portfolioData = new ArrayList<>();
        getFireBaseData();

        TextView buypage_symbol = findViewById(R.id.buypage_symbol);
        buypage_symbol.setText(symbolname);
        progressBar.setVisibility(View.VISIBLE);
        updateSymbol();

        buyprice_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                Double ordeamt = Double.parseDouble(df2.format(Double.parseDouble(price.getText().toString())*Double.parseDouble(buyquantity.getEditText().getText().toString())));
                orderamt.setText("₹"+Double.toString(ordeamt));}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        buybutton.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                if(validateQuantity()) {
                    buybutton.setVisibility(View.GONE);
                    ProgressBar swipebutton_progress = findViewById(R.id.buypage_swipebutton_loading);
                    swipebutton_progress.setVisibility(View.VISIBLE);
                    Intent intent1 = new Intent(Buy_Page.this, Dashboard.class);
                    intent1.putExtra("phoneno", autophoneno);
                    //intent1.putExtra("startingpage","2");
                    intent1.putExtra("startingpage","3");
                    intent1.putExtra("ordertype","BUY");
                    intent1.putExtra("symbol",symbolname);
                    portfolioData.add(new portfolio_data(symbolname,Double.parseDouble(temp),Double.parseDouble(temp),Double.parseDouble(buyquantity.getEditText().getText().toString())));
                    //intent1.putExtra("buyprice",buyprice.getEditText().getText().toString());
                    intent1.putExtra("buyquantity",buyquantity.getEditText().getText().toString());
                    buypage_handler.removeCallbacks(buypage_runnable);
                    Dashboard.order_add_count=0;
                    saveFireBaseData();
                    startActivity(intent1);
                    finish();
                }
            }
        });

    }

    public Boolean validatePrice(){
        try {
            String val = buyprice.getEditText().getText().toString();
            if (val.isEmpty()) {
                buyprice.setError("Field cannot be empty");
                return false;
            } else {
                buyprice.setError(null);
                buyprice.setErrorEnabled(false);
                return true;
            }
        }catch (Exception e){
            Log.d("EXCEPTION",e.toString());
            return false;
        }
    }
    public Boolean validateQuantity(){
        try {
            String val = buyquantity.getEditText().getText().toString();
            if (val.isEmpty()) {
                buyquantity.setError("Field cannot be empty");
                return false;
            } else {
                buyquantity.setError(null);
                buyquantity.setErrorEnabled(false);
                return true;
            }
        }catch (Exception e){
            Log.d("EXCEPTION",e.toString());
            return false;
        }
    }

    void updateSymbol(){
        buypage_handler.removeCallbacks(buypage_runnable);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://api.coindcx.com/exchange/ticker";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                try{
                    ArrayList<portfolio_data> upDateList= new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            if (jsonObject.getString("market").equals(symbolname)){
                                price.setText(Double.toString(Double.parseDouble(jsonObject.getString("last_price"))));
                                temp=Double.toString(Double.parseDouble(jsonObject.getString("last_price")));
                                if(Double.parseDouble(jsonObject.getString("change_24_hour"))>0)pricepercent.setTextColor(Color.GREEN);
                                else pricepercent.setTextColor(Color.RED);
                                pricepercent.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(jsonObject.getString("change_24_hour")))))+"%");

                                dayHigh.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(jsonObject.getString("high"))))));
                                dayLow.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(jsonObject.getString("low"))))));
                                volume.setText(jsonObject.getString("volume"));
                                bid.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(jsonObject.getString("bid"))))));
                                ask.setText(Double.toString(Double.parseDouble(df2.format(Double.parseDouble(jsonObject.getString("ask"))))));
                            }
                        }
                    Log.d("PRICE","UPDATED");
                    //homescreen_adapter.updateData(upDateList);
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
        refresh_Symbol(2000);
    }
    void refresh_Symbol(int milisec){
        buypage_runnable = new Runnable() {
            @Override
            public void run() {
                updateSymbol();
            }
        };
        buypage_handler.postDelayed(buypage_runnable,milisec);
    }

    void getFireBaseData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://startup-ee05d-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(autophoneno).child("data").child("portfolio");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int length = (int) snapshot.getChildrenCount();
                    portfolioData.clear();
                    for(int i=0;i<length;i++) {
                        portfolioData.add(new portfolio_data(snapshot.child(Integer.toString(i)).child("name").getValue(String.class), snapshot.child(Integer.toString(i)).child("price").getValue(Double.class), snapshot.child(Integer.toString(i)).child("profit").getValue(Double.class), snapshot.child(Integer.toString(i)).child("percent").getValue(Double.class)));
                    }
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
        reference.child(autophoneno).child("data").child("portfolio").setValue(portfolioData);
    }
}