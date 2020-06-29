package com.example.esl_ble_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class UnlogIItemActivity extends AppCompatActivity {

    private ImageView img;
    private TextView tv_name, tv_price, tv_pos, tv_number, tv_sale;
    TextView transtext;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlog_iitem);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String pos = intent.getStringExtra("pos");
        String number = intent.getStringExtra("number");
        String sale = intent.getStringExtra("sale");
        int saleprice = (int) (Integer.parseInt(price)*0.01*(100-Integer.parseInt(sale)));

        tv_name = findViewById(R.id.tv_name);
        tv_price = findViewById(R.id.tv_price);
        tv_pos = findViewById(R.id.tv_pos);
        tv_number = findViewById(R.id.tv_num);
        tv_sale = findViewById(R.id.tv_sale);
        img = findViewById(R.id.itemimage);

        tv_name.setText(name);
        tv_price.setText(String.valueOf(saleprice));
        tv_pos.setText(pos);
        tv_number.setText(number);
        tv_sale.setText(sale);

        Picasso.get()
                .load("http://ec2-13-125-127-155.ap-northeast-2.compute.amazonaws.com" + "/image/" + number + ".png")
                .into(img);
    }
}

