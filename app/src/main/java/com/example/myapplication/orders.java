package com.example.myapplication;

import android.content.Intent;
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

import java.util.ArrayList;

public class orders extends Fragment implements orderpage_adapter.orderpage_ViewHolder.OnClickOrder,orderpage_adapter.orderpage_ViewHolder.OnLongClickOrder{
    String phoneno,ordertype;
    RecyclerView orders_recyclerview;
    ArrayList<order_data> currentorders;
    orderpage_adapter orderpage_adapter;
    final int[] count = {1};

    Intent intent;
    public static Handler handler_orders = new Handler();
    public static Runnable runnable_orders;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.orders,container,false);

        intent = getActivity().getIntent();
        ordertype = intent.getStringExtra("ordertype");
        if(ordertype==null)ordertype="default";
        phoneno = intent.getStringExtra("phoneno");
        currentorders = new ArrayList<>();

        orders_recyclerview = view.findViewById(R.id.orders_recyclerview);

        orderpage_adapter = new orderpage_adapter(getActivity(),currentorders,orders.this,orders.this);
        orders_recyclerview.setAdapter(orderpage_adapter);
        orders_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        getFireBaseData();

        order_screen_update(view);
        return view;
    }

    void order_screen_update(View v){
        handler_orders.removeCallbacks(runnable_orders);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://api.coindcx.com/exchange/ticker";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    ArrayList<order_data> upDateList= new ArrayList<>();
                    for(int j=0;j<currentorders.size();j++) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            if (jsonObject.getString("market").equals(currentorders.get(j).getSymbol())){
                                currentorders.get(j).setPrice(Double.parseDouble(jsonObject.getString("last_price")));
                                currentorders.get(j).setPercent(Double.parseDouble(jsonObject.getString("change_24_hour")));
                                //upDateList.add(new order_data(currentorders.get(j).getOrdertype(),currentorders.get(j).getSymbol(),currentorders.get(j).getBuyprice(),currentorders.get(j).getBuyquantity(),Double.parseDouble(jsonObject.getString("last_price")),Double.parseDouble(jsonObject.getString("change_24_hour"))));
                                break;
                            }
                        }
                    }
                    //Log.d("ORDER PRICE","UPDATED");
                    //currentorders.clear();
                   // currentorders.addAll(upDateList);
                    Log.d("CURRENT ORDERS LENGTH",Integer.toString(currentorders.size()));
                    orderpage_adapter.notifyDataSetChanged();
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
        refresh_orders(2000,v);
    }

    void refresh_orders(int milisec,View v){
        runnable_orders = new Runnable() {
            @Override
            public void run() {
                order_screen_update(v);
            }
        };
        handler_orders.postDelayed(runnable_orders,milisec);
        orders_recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch(newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        handler_orders.postDelayed(runnable_orders, milisec);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        handler_orders.removeCallbacks(runnable_orders);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        handler_orders.removeCallbacks(runnable_orders);
                        break;
                }
            }
        });
    }

    @Override
    public void onClickOrderRecycler(int position) {
        Log.d("ORDER POSITION",Integer.toString(position));
    }
    void getFireBaseData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://startup-ee05d-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(phoneno).child("data");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.d("LOAD SUCCESS","YES");
                    int length = (int) snapshot.child("currentorders").getChildrenCount();
                    currentorders.clear();
                    DataSnapshot temp = snapshot.child("currentorders");
                    for(int i=0;i<length;i++) {
                        currentorders.add(new order_data(temp.child(Integer.toString(i)).child("ordertype").getValue(String.class), temp.child(Integer.toString(i)).child("symbol").getValue(String.class),temp.child(Integer.toString(i)).child("buyprice").getValue(Double.class), temp.child(Integer.toString(i)).child("buyquantity").getValue(Double.class),0.0,0.0));
                    }
                    orders_recyclerview.setAdapter(orderpage_adapter);
                    orders_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
                if(ordertype.equals("BUY")&& Dashboard.order_add_count ==0) {
                    Dashboard.order_add_count=1;
                    currentorders.add(new order_data(ordertype,intent.getStringExtra("symbol"),Double.parseDouble(intent.getStringExtra("buyprice")),Double.parseDouble(intent.getStringExtra("buyquantity")),0.0,0.0));
                    Log.d("CURRENT ORDER LENGTH",Integer.toString(currentorders.size()));
                    saveFireBaseData();
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
        reference.child(phoneno).child("data").child("currentorders").setValue(currentorders);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLongClickOrderRecycler(int position,View v) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
        popupMenu.inflate(R.menu.orderpage_menu);
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.orderpage_menu_modify:
                        break;
                    case R.id.orderpage_menu_remove:
                        currentorders.remove(position);
                        saveFireBaseData();
                        break;

                    default:
                        Toast.makeText(getActivity(), "ERRORS", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        popupMenu.show();

    }
}
