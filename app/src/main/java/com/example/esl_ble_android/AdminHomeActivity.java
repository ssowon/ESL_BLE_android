package com.example.esl_ble_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

//관리자로 로그인하면 나오는 제품 리스트
public class AdminHomeActivity extends AppCompatActivity {
    String myJSON;

    private ArrayList<ListViewItem> data = null;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_POS = "pos";
    private static final String TAG_NUMBER = "number";
    private static final String TAG_SALE = "sale";

    JSONArray peoples = null;

    ArrayList<HashMap<String,String>>personList;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        list = findViewById(R.id.listView);

        getData("http://ec2-13-125-127-155.ap-northeast-2.compute.amazonaws.com" + "/db_connect.php");
    }

    protected void showList(){
        data = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            peoples = jsonObject.getJSONArray(TAG_RESULTS);

            for (int i = 0; i<peoples.length()-1;i++){
                JSONObject c = peoples.getJSONObject(i);
                final String name = c.getString(TAG_NAME);
                final String price = c.getString(TAG_PRICE);
                final String pos = c.getString(TAG_POS);
                final String number = c.getString(TAG_NUMBER);
                final String sale = c.getString(TAG_SALE);

                ListViewItem listViewItem = new ListViewItem(name, price, pos, number, sale);

                data.add(listViewItem);
            }

            ListViewAdapter cusAdapter = new ListViewAdapter(data);

            list.setAdapter(cusAdapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), AdminItemManageActivity.class);
                    intent.putExtra("name", data.get(position).getName());
                    intent.putExtra("price", data.get(position).getPrice());
                    intent.putExtra("pos", data.get(position).getPos());
                    intent.putExtra("number", data.get(position).getNumber());
                    intent.putExtra("sale", data.get(position).getSale());

                    startActivity(intent);
                }
            });

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
    }
}