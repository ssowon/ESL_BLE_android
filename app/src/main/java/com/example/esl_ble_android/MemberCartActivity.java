package com.example.esl_ble_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemberCartActivity extends AppCompatActivity {
    String myJSON;
    private TextView tv_cart, tv_sum;

    private ArrayList<ListViewItem> data = null;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_POS = "pos";
    private static final String TAG_NUMBER = "number";
    private static final String TAG_SALE = "sale";

    JSONArray peoples = null;

    ArrayList<HashMap<String,String>> personList;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_cart);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");
        final String userName = intent.getStringExtra("userName");
        final String userCart = intent.getStringExtra("userCart");
        tv_cart = findViewById(R.id.tv_cart);
        tv_sum = findViewById(R.id.tv_sum);


        list = findViewById(R.id.listView);

        tv_cart.setText(userName + "님의 장바구니");

        personList = new ArrayList<>();
        getData("http://ec2-13-125-127-155.ap-northeast-2.compute.amazonaws.com" + "/db_connect.php");
    }

    protected void showList(){
        data = new ArrayList<>();

        Intent intent = getIntent();
        final String userCart = intent.getStringExtra("userCart");
        String[] userCartItem = userCart.split(" ");
        List<String> userCartlist = new ArrayList<String>();

        for(int i=0;i < userCartItem.length; i++){
            String[] res = userCartItem[i].split("-");
            userCartlist.add(res[0]);
        }

        Integer sumint = 0;
        int totalElements = userCartlist.size();

        try {

            JSONObject jsonObject = new JSONObject(myJSON);
            peoples = jsonObject.getJSONArray(TAG_RESULTS);

            for (int i = 0; i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String name = c.getString(TAG_NAME);
                String price = c.getString(TAG_PRICE);
                String pos = c.getString(TAG_POS);
                String number = c.getString(TAG_NUMBER);
                String sale = c.getString(TAG_SALE);

                for(int index = 0; index < totalElements; index++){
                    if(number.equals(userCartlist.get(index))){

                        sumint = sumint + Integer.parseInt(price);

                        ListViewItem listViewItem = new ListViewItem(name, price, pos, number, sale);

                        data.add(listViewItem);
                    }
                }
            }
            ListViewAdapter cusAdapter = new ListViewAdapter(data);

            list.setAdapter(cusAdapter);
            tv_sum.setText("총 가격 : "+sumint + "원");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }}
