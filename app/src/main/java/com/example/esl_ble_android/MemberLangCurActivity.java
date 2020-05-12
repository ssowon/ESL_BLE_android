package com.example.esl_ble_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MemberLangCurActivity extends AppCompatActivity {

    private TextView tv_name;
    private Spinner spinner_language, spinner_currency;
    private Button button_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_lang_cur);

        final Intent intent = getIntent();

        final String userID = intent.getStringExtra("userID");
        final String userName = intent.getStringExtra("userName");
        final String userLanguage = intent.getStringExtra("userLanguage");
        final String userCurrency = intent.getStringExtra("userCurrency");

        tv_name = findViewById(R.id.tv_name);
        button_change = findViewById(R.id.button_change);
        spinner_language = findViewById(R.id.spinner_language);
        spinner_currency = findViewById(R.id.spinner_currency);

        tv_name.setText(userID);

        button_change.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userLanguage = spinner_language.getSelectedItem().toString();
                final String userCurrency = spinner_currency.getSelectedItem().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {

                                Toast.makeText(getApplicationContext(), "변경 성공",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MemberLangCurActivity.this, MemberHomeActivity.class);
                                intent.putExtra("userID",userID);
                                intent.putExtra("userName",userName);
                                intent.putExtra("userLanguage",userLanguage);
                                intent.putExtra("userCurrency",userCurrency);

                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "변경 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                MemberLangCurRequest langCurRequest = new MemberLangCurRequest(userID,userName,userLanguage,userCurrency,responseListener);

                RequestQueue queue = Volley.newRequestQueue(MemberLangCurActivity.this);
                queue.add(langCurRequest);
            }
        });    }
}
