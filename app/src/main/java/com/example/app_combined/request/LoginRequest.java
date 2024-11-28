package com.example.app_combined.request;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    // 서버 URL 설정: localhost -> 10.0.2.2 (에뮬레이터), IP 주소 (실제 기기 사용 시)
    private static final String URL = "http://10.0.2.2:8080/api/user/login";
    private final Map<String, String> map;

    public LoginRequest(String userId, String passWord, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 네트워크 오류 처리
                if (error.networkResponse != null) {
                    Log.e("LoginRequest", "Error Code: " + error.networkResponse.statusCode);
                }
                Log.e("LoginRequest", "Error: " + error.getMessage());
            }
        });

        // 요청 파라미터 설정
        map = new HashMap<>();
        map.put("UserId", userId);
        map.put("passWord", passWord);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
