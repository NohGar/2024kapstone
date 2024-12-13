package com.example.app_combined.retrofit;

import android.content.Context;

import com.example.app_combined.cookies.PersistentCookieJar;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;
    private OkHttpClient client;
    private PersistentCookieJar cookieJar; // 쿠키 작동 확인하기 위해 넣음

    public RetrofitService(Context context) {
        initializeRetrofit(context);
    }

    private void initializeRetrofit(Context context){
        cookieJar = new PersistentCookieJar(context);

        // OkHttpClient 생성 및 CookieJar 설정 -> OkHttpClient를 통해 쿠키 관리
        client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://34.204.67.21:8080/")
                .addConverterFactory(GsonConverterFactory.create()) // JSON 변환
                .client(client) // OkHttpClient 연결
                .build();
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }

    public PersistentCookieJar getCookieJar() {
        return cookieJar; // PersistentCookieJar 반환
    }
}
