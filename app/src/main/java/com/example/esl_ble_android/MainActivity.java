package com.example.esl_ble_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Member;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    private EditText et_id, et_pw;
    private Button button_login_member, button_login_admin, button_unlogin, button_register, bt_test;

    final static String dbName = "unlogin.db";
    final static int dbVersion = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        button_login_member = findViewById(R.id.button_login_member);
        button_unlogin = findViewById(R.id.button_unlogin);
        button_login_admin = findViewById(R.id.button_login_admin);
        button_register = findViewById(R.id.button_register);
        dbHelper = new DBHelper(this, dbName, null, dbVersion);

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        button_unlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UnloginHomeActivity.class);
                startActivity(intent);
            }
        });

        button_login_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = et_id.getText().toString();
                String userPassword = et_pw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                String userID = jsonObject.getString("userID");
                                String userName = jsonObject.getString("userName");
                                String userLanguage = jsonObject.getString("userLanguage");
                                String userCurrency = jsonObject.getString("userCurrency");

                                Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, MemberHomeActivity.class);
                                intent.putExtra("userID",userID);
                                intent.putExtra("userName",userName);
                                intent.putExtra("userLanguage",userLanguage);
                                intent.putExtra("userCurrency",userCurrency);

                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginMemberRequest loginRequest = new LoginMemberRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }
        });
        button_login_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = et_id.getText().toString();
                String userPassword = et_pw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                String userID = jsonObject.getString("userID");
                                String userPassword = jsonObject.getString("userPassword");

                                Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                                intent.putExtra("userID",userID);
                                intent.putExtra("userPassword",userPassword);

                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginAdminRequest loginRequest = new LoginAdminRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }
        });

    }

    static class DBHelper extends SQLiteOpenHelper {

        //생성자 - database 파일을 생성한다.
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //DB 처음 만들때 호출. - 테이블 생성 등의 초기 처리.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE unlogin (language TEXT, Currency TEXT);");
        }

        //DB 업그레이드 필요 시 호출. (version값에 따라 반응)
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS t3");
            onCreate(db);
        }

    }
}
