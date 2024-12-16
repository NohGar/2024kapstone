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

public class Login extends AppCompatActivity {

    TextView sign;

    private EditText login_email, login_password;
    private Button login_button, join_button, btn_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        btn_pass = findViewById(R.id.btn_login);
        btn_pass.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            Toast.makeText(Login.this, "환영합니다!", Toast.LENGTH_LONG).show();
            startActivity(intent);
        });

        // 회원가입 버튼
        sign = findViewById(R.id.btn_signup);
        // 회원가입 버튼 클릭 시, 회원가입 페이지로 이동
        sign.setOnClickListener(v -> {
            Intent intent = new Intent(this, Signup.class);
            startActivity(intent);
        });

        initializeComponets();
    }

    private void initializeComponets() {
        login_email = findViewById(R.id.login_ID);
        login_password = findViewById(R.id.login_Password);
        login_button = findViewById(R.id.btn_pass);

        // Retrofit 서비스 초기화
        RetrofitService retrofitService = new RetrofitService(this);
        AuthApi authApi = retrofitService.getRetrofit().create(AuthApi.class);

        login_button.setOnClickListener(view -> {
            String email = login_email.getText().toString();
            String password = login_password.getText().toString();

            // 로그인 요청 객체 생성
            LoginRequest loginRequest = new LoginRequest(email, password);

            // Retrofit을 통한 로그인 요청
            authApi.login(loginRequest).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful()) {
                        // 응답 성공 시
                        AuthResponse authResponse = response.body();

                        // MainActivity로 이동하면서 토큰 전달
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("accessToken", authResponse.getAccessToken());
                        intent.putExtra("tokenType", authResponse.getTokenType());
                        startActivity(intent);
                    } else {
                        // 응답 실패 시
                        Toast.makeText(Login.this, response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                    // 통신 실패 시
                    Toast.makeText(Login.this, "통신 실패", Toast.LENGTH_LONG).show();
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error occurred", throwable);
                }
            });
        });

    }
}
