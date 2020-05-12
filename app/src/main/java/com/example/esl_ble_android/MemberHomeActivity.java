package com.example.esl_ble_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MemberHomeActivity extends AppCompatActivity {

    private TextView tv_name_hello;
    private Button bt_change; //언어랑 통화 바꾸는 액티비티로 이동
    private ImageButton bt_cart;

    GridView gridlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_home);

        final Intent intent = getIntent();

        final String userID = intent.getStringExtra("userID");
        final String userName = intent.getStringExtra("userName");
        final String userLanguage = intent.getStringExtra("userLanguage");
        final String userCurrency = intent.getStringExtra("userCurrency");

        tv_name_hello = findViewById(R.id.tv_name_hello);
        bt_change = findViewById(R.id.button_change);
        bt_cart = findViewById(R.id.bt_cart);
        gridlist = findViewById(R.id.itemlist);

        ArrayList<String> items = new ArrayList<>();
        items.add("1");
        items.add("2");
        items.add("3");
        items.add("5");
        items.add("6");
        items.add("7");
        items.add("8");
        items.add("9");

        CustomAdapter adapter = new CustomAdapter(this, 0, items);
        gridlist.setAdapter(adapter);


        tv_name_hello.setText(userName + "님 안녕하세요");
        bt_change.setText(userLanguage +" and "+userCurrency +"\nchange");

        bt_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberHomeActivity.this, MemberLangCurActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                intent.putExtra("userLanguage", userLanguage);
                intent.putExtra("userCurrency", userCurrency);
                startActivity(intent);
            }
        });

        bt_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            String userCart = jsonObject.getString("userCart");
                            if (success && !userCart.equals("null")) {
                                Intent intent = new Intent(MemberHomeActivity.this, MemberCartActivity.class);
                                intent.putExtra("userID",userID);
                                intent.putExtra("userName",userName);
                                intent.putExtra("userCart",userCart);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "장바구니가 비어있습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                MemCartRequest memCartRequest = new MemCartRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MemberHomeActivity.this);
                queue.add(memCartRequest);
            }
        });

    }

    private class CustomAdapter extends ArrayAdapter<String> {
        final Intent intent = getIntent();
        final String userLanguage = intent.getStringExtra("userLanguage");
        final String userCurrency = intent.getStringExtra("userCurrency");

        private ArrayList<String> items;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.gridlist, null);
            }

            ImageView imageView = v.findViewById(R.id.imageView);
            Picasso.get()
                    .load("http://ec2-13-124-52-74.ap-northeast-2.compute.amazonaws.com/image/" + items.get(position) + ".png")
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String number = items.get(position);

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {
                                    String name = jsonObject.getString("name");
                                    String price = jsonObject.getString("price");
                                    String pos = jsonObject.getString("pos");
                                    String number = jsonObject.getString("number");
                                    String sale = jsonObject.getString("sale");

                                    Intent intent = new Intent(MemberHomeActivity.this, MemberIItemActivity.class);
                                    intent.putExtra("name",name);
                                    intent.putExtra("price",price);
                                    intent.putExtra("pos",pos);
                                    intent.putExtra("number",number);
                                    intent.putExtra("sale",sale);
                                    intent.putExtra("userLanguage",userLanguage);
                                    intent.putExtra("userCurrency",userCurrency);

                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "불러오기에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ItemMemberRequest ItemRequest = new ItemMemberRequest(number, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(MemberHomeActivity.this);
                    queue.add(ItemRequest);
                }
            });
            return v;
        }
    }
}