package com.example.app_combined.info;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast; // 간단한 메시지 전시를 위한 클래스

import androidx.appcompat.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.app_combined.R;
import com.example.app_combined.api.AuthApi;
import com.example.app_combined.model.AuthResponse;
import com.example.app_combined.model.SignupRequest;
import com.example.app_combined.request.RegisterRequest;
import com.example.app_combined.retrofit.RetrofitService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;

public class Signup extends AppCompatActivity {

    private TextView btn_back;
    private EditText signup_name, signup_pw, signup_pw2, signup_email;
    private Button btn_emailcheck, btn_submit;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        //뒤로 가기 버튼
        btn_back = findViewById(R.id.back);
        btn_back.setOnClickListener(v -> onBackPressed() );

        //기입 항목
        signup_name = findViewById(R.id.signup_name);
        signup_email=findViewById(R.id.signup_email);
        signup_pw = findViewById(R.id.signup_password1);
        signup_pw2 = findViewById(R.id.signup_password2);
        //이메일 중복 확인
        btn_emailcheck = findViewById(R.id.signup_btn_emailcheck);

        //회원가입 완료 버튼
        btn_submit = findViewById(R.id.signupbutton);
        btn_submit.setOnClickListener(view -> {
            final String name = signup_name.getText().toString().trim();
            final String email = signup_email.getText().toString().trim();
            final String password = signup_pw.getText().toString().trim();
            final String passwordConfirm = signup_pw2.getText().toString().trim();

            //빈칸 체크
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                dialog.show();
                return;
            }

            SignupRequest signupRequest = new SignupRequest(name, email, password, passwordConfirm);
            RetrofitService retrofitService = new RetrofitService();
            AuthApi authApi = retrofitService.getRetrofit().create(AuthApi.class);

            authApi.register(signupRequest).enqueue(registerCallback());
        });

    }

    @NonNull
    private Callback<AuthResponse> registerCallback() {
        return new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, retrofit2.Response<AuthResponse> response) {
                Toast.makeText(Signup.this, "통신 성공", Toast.LENGTH_LONG).show();
                if (response.isSuccessful()) {

                    AuthResponse authResponse = response.body();

                    Intent intent = new Intent(Signup.this, MainActivity.class);
                    intent.putExtra("accessToken", authResponse.getAccessToken());
                    intent.putExtra("tokenType", authResponse.getTokenType());

                    //여기서 팅긴다.
                    //startActivity(intent);
                } else {
                    // Todo
                    //  전달받은 오류 message 출력( 지금은 공백이 뜬다. )
                    Toast.makeText(Signup.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                Toast.makeText(Signup.this, "통신 실패", Toast.LENGTH_LONG).show();
                Logger.getLogger(Signup.class.getName()).log(Level.SEVERE, "Error occurred", throwable);
            }
        };
    }
}