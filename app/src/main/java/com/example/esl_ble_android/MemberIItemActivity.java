package com.example.esl_ble_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MemberIItemActivity extends AppCompatActivity {

    private ImageView img;
    private TextView tv_name, tv_price, tv_pos, tv_number, tv_sale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_iitem);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String pos = intent.getStringExtra("pos");
        String number = intent.getStringExtra("number");
        String sale = intent.getStringExtra("sale");
        String userLanguage = intent.getStringExtra("userLanguage");
        String userCurrency = intent.getStringExtra("userCurrency");
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
                .load("http://ec2-13-124-52-74.ap-northeast-2.compute.amazonaws.com/image/" + number + ".png")
                .into(img);
        getWebsite();
    }

    private void getWebsite() {
        Intent intent = getIntent();
        String price = intent.getStringExtra("price");
        String sale = intent.getStringExtra("sale");
        final String userCurrency = intent.getStringExtra("userCurrency");
        final int saleprice = (int) (Integer.parseInt(price)*0.01*(100-Integer.parseInt(sale)));

        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                try{
                    Document doc = Jsoup.connect("https://ko.valutafx.com/KRW-"+userCurrency+".htm").get();
                    Elements elements = doc.select(".converter-result").select(".rate-value");
                    builder.append(elements.text());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(userCurrency.equals("KRW")) {
                            tv_price.setText(Integer.toString(saleprice));
                        }else{
                            float floatbuilder = saleprice * Float.parseFloat(String.valueOf(builder));
                            String textbuilder = Float.toString(floatbuilder);
                            tv_price.setText(textbuilder);

                        }
                    }
                });
            }
        }).start();
    }
}

