package com.example.esl_ble_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

//AdminHomeAct에서 제품 선택하면 들어옴
public class AdminItemManageActivity extends AppCompatActivity {

    public final static String ip = "13.125.127.155";
    public final static int port = 2017;

    private static Socket socket;
    private BufferedReader socketIn;
    private BufferedWriter socketOut;

    private Handler mHandler;
    private String data;

    private ImageView img;
    private TextView tv_name, tv_number;
    private EditText et_price, et_pos, et_sale;
    private Button bt_update, bt_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_item_manage);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mHandler = new Handler();

        AdminItemManageActivity.MainThread thread = new AdminItemManageActivity.MainThread();
        thread.setDaemon(true);
        thread.start();

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String pos = intent.getStringExtra("pos");
        final String number = intent.getStringExtra("number");
        String sale = intent.getStringExtra("sale");

        tv_name = findViewById(R.id.tv_name);
        et_price = findViewById(R.id.tv_price);
        et_pos = findViewById(R.id.tv_pos);
        tv_number = findViewById(R.id.tv_num);
        et_sale = findViewById(R.id.tv_sale);
        img = findViewById(R.id.itemimage);
        bt_update = findViewById(R.id.button_update);
        bt_delete = findViewById(R.id.button_delete);

        Picasso.get()
                .load("http://ec2-13-125-127-155.ap-northeast-2.compute.amazonaws.com"+"/image/"+number+".png")
                .into(img);

        tv_name.setText(name);
        et_price.setText(price);
        et_pos.setText(pos);
        tv_number.setText(number);
        et_sale.setText(sale);


        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = tv_name.getText().toString();
                String price = et_price.getText().toString();
                String pos = et_pos.getText().toString();
                String number = tv_number.getText().toString();
                String sale = et_sale.getText().toString();

                PrintWriter out = new PrintWriter(socketOut, true);
                String updateData = "CHANGEINFO@" + name + "_" + price + "_" + pos + "_" + number + "_" + sale;
                out.println(updateData);

                Intent intent = new Intent(AdminItemManageActivity.this, AdminHomeActivity.class);

                startActivity(intent);
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = tv_name.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {

                                Toast.makeText(getApplicationContext(), "삭제 성공",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AdminItemManageActivity.this, AdminHomeActivity.class);

                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                AdminItemDeleteRequest deleteRequest = new AdminItemDeleteRequest(name, responseListener);

                RequestQueue queue = Volley.newRequestQueue(AdminItemManageActivity.this);
                queue.add(deleteRequest);
            }
        });

    }

    public class MainThread extends Thread {
        public void run() {
            try{
                setSocket(ip,port);
            }catch (IOException e1){
                e1.printStackTrace();
            }
            try {
                while(true){
                    data = socketIn.readLine();
                    mHandler.post(showState);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setSocket(String ip, int port) throws IOException {
        try {
            socket = new Socket(ip,port);
            socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            PrintWriter out = new PrintWriter(socketOut, true);
            out.println("app");
        }catch (IOException e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private Runnable showState = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(AdminItemManageActivity.this,data, Toast.LENGTH_SHORT).show();
        }
    };

    public String isNull(String text) {
        if( text.equals(""))
            text = "default";
        return text;
    }
}
