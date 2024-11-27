package com.example.app_combined

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.app_combined.OverlayView
import com.example.app_combined.R
import com.example.app_combined.ExerciseFeedback
import com.example.app_combined.PullUpFeedback
import com.example.app_combined.PushUpFeedback
import com.example.app_combined.SquatFeedback
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class PostureCorrectionFragment : Fragment() {

    private lateinit var exerciseFeedback: ExerciseFeedback
    private lateinit var overlayView: OverlayView  // overlayView 초기화, 사용자한테 피드백 문구 표시하는데 사용됨

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기 운동을 PushUp으로 설정
        exerciseFeedback = PushUpFeedback()

        // overlayView 초기화
        overlayView = view.findViewById(R.id.overlayView)

        // 운동 선택 UI에서 운동을 변경하면 해당 피드백을 설정
        //setupExerciseSelection()
    }

//    private fun setupExerciseSelection() {
//        // XML에서 설정한 버튼 ID로 버튼 참조
//        val pushUpButton = view?.findViewById<Button>(R.id.btnPushUp)
//        val squatButton = view?.findViewById<Button>(R.id.btnSquat)
//        val pullUpButton = view?.findViewById<Button>(R.id.btnPullUp)
//
//        pushUpButton?.setOnClickListener { exerciseFeedback = PushUpFeedback() }
//        squatButton?.setOnClickListener { exerciseFeedback = SquatFeedback() }
//        pullUpButton?.setOnClickListener { exerciseFeedback = PullUpFeedback() }
//    }

    fun provideFeedback(landmarks: List<List<NormalizedLandmark>>) {
        // 현재 설정된 운동의 피드백을 가져옴
        val feedbackMessage = exerciseFeedback.provideFeedback(landmarks)
        // UI에 피드백 표시
        overlayView.showFeedback(feedbackMessage, Color.GREEN)
    }
}