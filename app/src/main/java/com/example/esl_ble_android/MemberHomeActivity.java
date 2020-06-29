package com.example.esl_ble_android;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MemberHomeActivity extends AppCompatActivity {
    private static final String TAG = "MemberHomeActivity";

    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<String> items = new ArrayList<>();

    private TextView tv_name_hello;
    private Button bt_change, bt_beacon; //언어랑 통화 바꾸는 액티비티로 이동
    private ImageButton bt_cart;
    public boolean overlay;
    public CustomAdapter adapter;

    GridView gridlist;
    String deviceName;

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
        bt_beacon = findViewById(R.id.bt_beacon);
        gridlist = findViewById(R.id.itemlist);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


//        items.add("1"); //삼다수
//        items.add("2"); //우유
//        items.add("3"); //오이
//        items.add("4");
//        items.add("5"); //계란
//        items.add("6"); //짜파게티

        adapter = new CustomAdapter(this, 0, items);
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

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(MemberHomeActivity.this, MainActivity.class);
        startActivity(intent);
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
                    .load("http://ec2-13-125-127-155.ap-northeast-2.compute.amazonaws.com" + "/image/" + items.get(position) + ".png")
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

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                deviceName = device.getName();
                if(deviceName != null && deviceName.startsWith("ESL")) {
//                    overlay = false;
//                    for(int i = 0; i < device_name_list.size(); i++) if(device_name_list.get(i).equals(deviceName.substring(3))) overlay = true;
//                    if(!overlay) device_name_list.add(deviceName.substring(3));
                    overlay = false;
                    for(int j = 0; j < items.size(); j++) if(items.get(j).equals(deviceName.substring(3,4))) overlay = true;
                    if(!overlay) items.add(deviceName.substring(3,4));
                }
//                tv_name_hello.setText(device_name_list.toString());
//                tv_name_hello.setText(items.toString());
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                //CustomAdapter adapter = new CustomAdapter(this, 0, items);

                adapter = new CustomAdapter(MemberHomeActivity.this, 0, items);
                gridlist.setAdapter(adapter);

            }
        }
    };
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        //mBluetoothAdapter.cancelDiscovery();
    }

    public void btnDiscover(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){
            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

}