package com.example.esl_ble_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MemberIItemActivity extends AppCompatActivity {

    private ImageView img;
    private TextView tv_name, tv_price, tv_pos, tv_number, tv_sale, tv_cur;
    private String result;

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
        int saleprice = (int) (Integer.parseInt(price) * 0.01 * (100 - Integer.parseInt(sale)));

        tv_name = findViewById(R.id.tv_name);
        tv_price = findViewById(R.id.tv_price);
        tv_pos = findViewById(R.id.tv_pos);
        tv_number = findViewById(R.id.tv_num);
        tv_sale = findViewById(R.id.tv_sale);
        tv_cur = findViewById(R.id.tv_cur);
        img = findViewById(R.id.itemimage);

        tv_name.setText(name);
        tv_price.setText(String.valueOf(saleprice));
        tv_pos.setText(pos);
        tv_number.setText(number);
        tv_sale.setText(sale);
        if(userCurrency.equals("USD")) tv_cur.setText("$");
        else if(userCurrency.equals("EUR")) tv_cur.setText("€");
        else if(userCurrency.equals("JPY") || userCurrency.equals("CNY")) tv_cur.setText("¥");

        Picasso.get()
                .load("http://ec2-13-125-127-155.ap-northeast-2.compute.amazonaws.com" + "/image/" + number + ".png")
                .into(img);
        getWebsite();
        if(!userLanguage.equals("Korean")){
            new BackgroundTask().execute();
        }
    }

    private void getWebsite() {
        Intent intent = getIntent();

        String price = intent.getStringExtra("price");
        String sale = intent.getStringExtra("sale");
        final String userCurrency = intent.getStringExtra("userCurrency");
        final int saleprice = (int) (Integer.parseInt(price) * 0.01 * (100 - Integer.parseInt(sale)));

        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                try {
                    Document doc = Jsoup.connect("https://ko.valutafx.com/KRW-" + userCurrency + ".htm").get();
                    Elements elements = doc.select(".converter-result").select(".rate-value");
                    builder.append(elements.text());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (userCurrency.equals("KRW")) {
                            tv_price.setText(Integer.toString(saleprice));
                        } else {
                            float floatbuilder = saleprice * Float.parseFloat(String.valueOf(builder));
                            String textbuilder = Float.toString(floatbuilder);
                            tv_price.setText(textbuilder);
                        }
                    }
                });
            }
        }).start();
    }

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        private String responseBody;
        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String userLanguage = intent.getStringExtra("userLanguage");

        @Override
        protected Integer doInBackground(Integer... integers) {
            String clientID = "coy9j9qXIz4r5dVpFPCV";
            String clientSecret = "KnNtMU5A1h";

            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
            String text;
            try {
                text = URLEncoder.encode(name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("인코딩 실패", e);
            }

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", clientID);
            requestHeaders.put("X-Naver-Client-Secret", clientSecret);

            responseBody = post(apiURL, requestHeaders, text);

            System.out.println(responseBody);
            return null;
        }

        protected void onPostExecute(Integer a) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(responseBody);
            if(element.getAsJsonObject().get("message") != null){
                tv_name.setText(element.getAsJsonObject().get("message").getAsJsonObject().get("result").getAsJsonObject().get("translatedText").getAsString());
            }
        }
        private String post(String apiUrl, Map<String, String> requestHeaders, String text) {
            HttpURLConnection con = connect(apiUrl);
            String postParams = "source=ko&target=en&text=" + text;

            if(userLanguage.equals("English")) {
                postParams = "source=ko&target=en&text=" + text;
            }
            else if(userLanguage.equals("Chinese")){
                postParams = "source=ko&target=zh-CN&text=" + text; //원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
            }else {
                postParams = "source=ko&target=ja&text=" + text; //원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
            }
            try {
                con.setRequestMethod("POST");
                for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                    con.setRequestProperty(header.getKey(), header.getValue());
                }

                con.setDoOutput(true);
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postParams.getBytes());
                    wr.flush();
                }

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                    return readBody(con.getInputStream());
                } else {  // 에러 응답
                    return readBody(con.getErrorStream());
                }
            } catch (IOException e) {
                throw new RuntimeException("API 요청과 응답 실패", e);
            } finally {
                con.disconnect();
            }
        }

        private HttpURLConnection connect(String apiUrl) {
            try {
                URL url = new URL(apiUrl);
                return (HttpURLConnection) url.openConnection();
            } catch (MalformedURLException e) {
                throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
            } catch (IOException e) {
                throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
            }
        }

        private String readBody(InputStream body) {
            InputStreamReader streamReader = new InputStreamReader(body);

            try (BufferedReader lineReader = new BufferedReader(streamReader)) {
                StringBuilder responseBody = new StringBuilder();

                String line;
                while ((line = lineReader.readLine()) != null) {
                    responseBody.append(line);
                }

                return responseBody.toString();
            } catch (IOException e) {
                throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
            }
        }
    }
}
