package com.example.app_combined.info;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_combined.R;
import com.example.app_combined.api.AuthApi;
import com.example.app_combined.model.LoginRequest;
import com.example.app_combined.model.AuthResponse;
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


        // 회원가입
        sign = findViewById(R.id.btn_signup);
        sign.setOnClickListener(v -> {
            Intent intent = new Intent(this, Signup.class);
            startActivity(intent);
        });

        // 임시로 패스
        // Todo
        //  로그인 및 회원가입 기능
        btn_pass = findViewById(R.id.btn_pass);
        btn_pass.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });


        initializeComponets();

    }

    private void initializeComponets() {
        login_email = findViewById( R.id.login_ID );
        login_password = findViewById( R.id.login_Password );
        login_button = findViewById( R.id.btn_login );

        RetrofitService retrofitService = new RetrofitService();
        AuthApi authApi = retrofitService.getRetrofit().create(AuthApi.class);

        login_button.setOnClickListener(view -> {
            String email = login_email.getText().toString();
            String password = login_password.getText().toString();

            LoginRequest loginRequest = new LoginRequest(email, password);

            authApi.login(loginRequest)
                    .enqueue(loginCallBack());
        });
    }

    @NonNull
    private Callback<AuthResponse> loginCallBack() {
        return new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                Toast.makeText(Login.this, "통신 성공", Toast.LENGTH_LONG).show();
                if (response.isSuccessful()) {
                    AuthResponse authResponse = response.body();

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("accessToken", authResponse.getAccessToken());
                    intent.putExtra("tokenType", authResponse.getTokenType());


                    //여기서 팅긴다. intent 관련 오류인 듯하다. 뭐 static을 쓰든 뭘 쓰든 해야 할 거같다.
                    //어찌 됏든 토큰 값은 저장을 해야 한다.
                    //startActivity(intent);
                } else {
                    Toast.makeText(Login.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                Toast.makeText(Login.this, "통신 실패", Toast.LENGTH_LONG).show();
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error occurred", throwable);
            }
        };
    }
}