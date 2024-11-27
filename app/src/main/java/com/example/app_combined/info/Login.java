package com.example.app_combined.info;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue; //모든 네트워크 요청을 관리하는 큐, 요청을 실행하는 데 사용.
import com.android.volley.Response; // 네트워크 요청의 응답을 나타냄.
import com.android.volley.toolbox.Volley; // 네트워크를 요청을 위한 큐를 생성하는 유틸리티 클래스
import com.example.app_combined.MainActivity2;
import com.example.app_combined.request.LoginRequest;
import com.example.app_combined.R;

import org.json.JSONException; // json 데이터 처리 중 발생하는 오류를 나타내는 예외 클래스
import org.json.JSONObject; // json의 객체를 표현하는 클래스, json형식의 데이터들을 처리하는 데 사용
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
        login_email = findViewById( R.id.login_ID );
        login_password = findViewById( R.id.login_Password );
        btn_pass = findViewById(R.id.btn_pass);


        btn_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity로 이동하는 Intent 생성
                Intent intent = new Intent(Login.this, MainActivity.class);

                // MainActivity로 전환
                startActivity(intent);
            }
        });

        //회원가입 버튼
        sign = findViewById(R.id.btn_signup);
        //회원가입 버튼 클릭시, 회원가입 페이지로 이동
        sign.setOnClickListener(v -> {
            Intent intent = new Intent(this, Signup.class);
            startActivity(intent);
        });

        //로그인 버튼
        login_button = findViewById( R.id.btn_login );
        //로그인 버튼시, 사실관계 확인
        login_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserEmail = login_email.getText().toString();
                String UserPwd = login_password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            if(success) {//로그인 성공시

                                String UserEmail = jsonObject.getString( "UserEmail" );
                                String UserPwd = jsonObject.getString( "UserPwd" );
                                String UserName = jsonObject.getString( "UserName" );

                                Toast.makeText( getApplicationContext(), String.format("%s님 환영합니다.", UserName), Toast.LENGTH_SHORT ).show();
                                Intent intent = new Intent( Login.this, MainActivity.class );

                                intent.putExtra( "UserEmail", UserEmail );
                                intent.putExtra( "UserPwd", UserPwd );
                                intent.putExtra( "UserName", UserName );

                                startActivity( intent );

                            } else {//로그인 실패시
                                Toast.makeText( getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT ).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest( UserEmail, UserPwd, responseListener );
                RequestQueue queue = Volley.newRequestQueue( Login.this );
                queue.add( loginRequest );

            }
        });
    }
}