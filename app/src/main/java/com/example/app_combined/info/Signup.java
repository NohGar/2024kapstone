package com.example.app_combined.info;

import androidx.activity.EdgeToEdge;
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
import com.example.app_combined.request.RegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

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

        btn_emailcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //UserEmail로 명칭
                String UserEmail = signup_email.getText().toString();

                if (validate) {
                    return; //검증 완료
                }

                if (UserEmail.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    dialog = builder.setMessage("아이디를 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                signup_email.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료
                                btn_emailcheck.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
        });
        //회원가입 완료 버튼
        btn_submit = findViewById(R.id.signupbutton);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String UserEmail = signup_email.getText().toString();
                final String UserPwd1 = signup_pw.getText().toString();
                final String UserName = signup_name.getText().toString();
                final String UserPwd2 = signup_pw2.getText().toString();

                //이메일 중복체크 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    dialog = builder.setMessage("중복된 아이디가 있는지 확인하세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                //빈칸 체크
                if (UserEmail.equals("") || UserPwd1.equals("") || UserName.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );
                            //회원가입 성공시
                            if(UserPwd1.equals(UserPwd2)) {
                                if (success) {
                                    //성공시 로그인 창으로 회귀
                                    Toast.makeText(getApplicationContext(), String.format("%s님 가입을 환영합니다.", UserName), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Signup.this, Login.class);
                                    startActivity(intent);

                                    //회원가입 실패시
                                } else {
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                                dialog = builder.setMessage("비밀번호가 동일하지 않습니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                RegisterRequest registerRequest = new RegisterRequest( UserEmail, UserPwd1, UserName, responseListener);
                RequestQueue queue = Volley.newRequestQueue( Signup.this );
                queue.add( registerRequest );


            }
        });

    }
}