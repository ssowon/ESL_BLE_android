package com.example.esl_ble_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AdminItemUpdateRequest extends StringRequest {

    final static private String URL = "http://ec2-13-124-77-109.ap-northeast-2.compute.amazonaws.com" + "/Item_update_app.php";
    private Map<String, String> map;

    public AdminItemUpdateRequest(String name, String price, String pos, String number, String sale, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("name",name);
        map.put("price",price);
        map.put("pos",pos);
        map.put("number",number);
        map.put("sale",sale);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
