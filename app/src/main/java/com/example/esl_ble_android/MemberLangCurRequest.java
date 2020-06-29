package com.example.esl_ble_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MemberLangCurRequest extends StringRequest {

    final static private String URL = "http://ec2-13-125-127-155.ap-northeast-2.compute.amazonaws.com" + "/member_langcur_app.php";
    private Map<String, String> map;

    public MemberLangCurRequest(String userID, String userName, String userLanguage, String userCurrency, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID",userID);
        map.put("userName",userName);
        map.put("userLanguage",userLanguage);
        map.put("userCurrency",userCurrency);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
