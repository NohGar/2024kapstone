package com.example.app_combined.frags;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app_combined.R;
import com.example.app_combined.info.Login;

public class Profile extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // LayoutInflater를 사용해 레이아웃 초기화
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 로그아웃 버튼 설정
        TextView logoutText = view.findViewById(R.id.text_logout);
        logoutText.setOnClickListener(v -> {
            // LoginActivity로 이동
            Intent intent = new Intent(getActivity(), Login.class);
            // 이전 화면 스택 제거 (로그인 화면으로 돌아가지 않도록)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view; // 초기화된 View를 반환
    }
}
