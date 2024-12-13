package com.example.app_combined.info;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_combined.R;
import com.example.app_combined.api.AuthApi;
import com.example.app_combined.model.AuthResponse;
import com.example.app_combined.model.LoginRequest;
import com.example.app_combined.retrofit.RetrofitService;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// getString, getInt, getJSONArray, has

public class Login extends AppCompatActivity {

    TextView sign;

    private EditText login_email, login_password;
    private Button login_button, join_button, btn_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        btn_pass = findViewById(R.id.btn_pass);
        btn_pass.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });

        //회원가입 버튼
        sign = findViewById(R.id.btn_signup);
        //회원가입 버튼 클릭시, 회원가입 페이지로 이동
        sign.setOnClickListener(v -> {
            Intent intent = new Intent(this, Signup.class);
            startActivity(intent);
        });

        initializeComponets();
    }

    private void initializeComponets() {
        login_email = findViewById( R.id.login_ID );
        login_password = findViewById( R.id.login_Password );
        login_button = findViewById( R.id.btn_login );

        RetrofitService retrofitService = new RetrofitService(this);
        AuthApi authApi = retrofitService.getRetrofit().create(AuthApi.class);

        login_button.setOnClickListener(view -> {
            String email = login_email.getText().toString();
            String password = login_password.getText().toString();

            LoginRequest loginRequest = new LoginRequest(email, password);

            authApi.login(loginRequest)
                    .enqueue(new Callback<AuthResponse>() {
                        @Override
                        public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                            Toast.makeText(Login.this, "통신 성공", Toast.LENGTH_LONG).show();
                            if (response.isSuccessful()) {
                                AuthResponse authResponse = response.body();

//                                // Access Token 로그 출력
//                                Log.d("AuthResponse", "Access Token: " + authResponse.getAccessToken());
//
//                                // 모든 쿠키 출력
//                                Log.d("ResponseHeaders", response.headers().toString());
//
//                                // Refresh-Token 값 가져오기
//                                PersistentCookieJar persistentCookieJar = retrofitService.getCookieJar();
//                                String refreshToken = persistentCookieJar.getCookieValue("Refresh-Token");
//
//                                // Refresh Token 로그 출력
//                                if (refreshToken != null) {
//                                    Log.d("AuthResponse", "Refresh Token: " + refreshToken);
//                                } else {
//                                    Log.e("AuthResponse", "Refresh Token not found in cookies.");
//                                }
//
//                                SharedPreferences sharedPreferences = getSharedPreferences("CookiePrefs", MODE_PRIVATE);
//                                Map<String, ?> allEntries = sharedPreferences.getAll();
//                                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                                    Log.d("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
//                                }

                                // MainActivity로 이동
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("accessToken", authResponse.getAccessToken());
                                intent.putExtra("tokenType", authResponse.getTokenType());
                                startActivity(intent);
                            } else {
                                Toast.makeText(Login.this, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                            Toast.makeText(Login.this, "통신 실패", Toast.LENGTH_LONG).show();
                            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error occurred", throwable);
                        }
                    });
        });

    }

}