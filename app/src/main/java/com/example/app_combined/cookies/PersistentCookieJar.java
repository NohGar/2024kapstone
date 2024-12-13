package com.example.app_combined.cookies;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class PersistentCookieJar implements CookieJar {

    private final SharedPreferences sharedPreferences;
    private final Map<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

    // SharedPreferences 초기화 및 기존 저장된 쿠키를 로드
    public PersistentCookieJar(Context context) {
        this.sharedPreferences = context.getSharedPreferences("CookiePrefs", Context.MODE_PRIVATE);
        loadCookiesFromStorage();
    }

    @Override // 서버 응답에서 받은 쿠키를 저장
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.put(url, cookies);
        saveCookiesToStorage();
    }

    @Override  // 요청 시 쿠키 불러옴
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies != null ? cookies : new ArrayList<>();
    }

    // 메모리 내 쿠키 데이터를 SharedPreferences에 JSON 형태로 저장
    private void saveCookiesToStorage() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<HttpUrl, List<Cookie>> entry : cookieStore.entrySet()) {
            String cookieJson = new Gson().toJson(entry.getValue());
            editor.putString(entry.getKey().toString(), cookieJson);
        }
        editor.apply();
    }

    // SharedPreferences에서 저장된 쿠키 데이터를 메모리로 로드
    private void loadCookiesFromStorage() {
        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            HttpUrl url = HttpUrl.parse(entry.getKey());
            List<Cookie> cookies = new Gson().fromJson((String) entry.getValue(), new TypeToken<List<Cookie>>() {}.getType());
            cookieStore.put(url, cookies);
        }
    }

    // 쿠키 잘 들어갔는지 테스트용 메서드
    public String getCookieValue(String name) {
        for(Map.Entry<HttpUrl, List<Cookie>> entry : cookieStore.entrySet()) {
            for (Cookie cookie : entry.getValue()) {
                if (cookie.name().equals(name)) {
                    return cookie.value();
                }
            }
        }
        return null;
    }
}

