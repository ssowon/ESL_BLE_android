package com.example.esl_ble_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ItemMemberRequest extends StringRequest {

    final static private String URL = "http://ec2-13-124-77-109.ap-northeast-2.compute.amazonaws.com" + "/Item_app_member.php";
    private Map<String, String> map;

    public ItemMemberRequest(String number, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("number",number);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
