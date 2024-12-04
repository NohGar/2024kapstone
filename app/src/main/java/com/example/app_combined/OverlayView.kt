/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.app_combined

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var pointPaint = Paint()
    private var linePaint = Paint()
    private var boxPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    // 운동관련 변수

    private var exerciseType: String = ""
    private var exerciseCount: Int = 0
    private var exerciseAngle: Int = 0
    private var feedback1: String = ""
    private var feedback2: String = ""
    private var feedback1_status : Boolean = false;
    private var feedback2_status : Boolean = false;

    private var textPaint = Paint()
    private var textView: TextView? = null

    // 피드백 메시지 관련 변수
    private var feedbackMessage: String? = null
    private var feedbackColor: Int = Color.GREEN

    init {
        initPaints()
        textView = TextView(context)
        textView?.setTextSize(100f)
        textView?.setTextColor(Color.WHITE)
        textView?.visibility = View.INVISIBLE
    }


    fun clear() {
        results = null
        pointPaint.reset()
        linePaint.reset()
        invalidate()
        initPaints()
    }

    fun setTextColor(){
        if(feedback1_status)
            feedbackColor = Color.GREEN
        else
            feedbackColor = Color.RED
    }



    private fun initPaints() {
        linePaint.color =
            ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = 100f
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeJoin = Paint.Join.ROUND

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL

        textPaint.color = Color.WHITE
        textPaint.textSize = 50f
        textPaint.isAntiAlias = true
    }

    // 피드백 메시지를 설정하는 함수
    fun showFeedback(message: String, color: Int) {
        feedbackMessage = message
        feedbackColor = color
        invalidate()  // 화면을 다시 그려 피드백 메시지가 반영되도록 함
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        results?.let { poseLandmarkerResult ->
            // 원하는 인덱스만 화면에 출력

            val selectiveLandmarks = when(exerciseType) {
                "PUSH_UP" -> listOf(11,12,13,14,15,16,23,24,25,26)
                "PULL_UP" -> listOf(11,12,13,14,15,16)
                "SQUAT" -> listOf(11,12,23,24,25,26,27,28,29,30)
                else -> listOf()
            }

            for (landmark in poseLandmarkerResult.landmarks()) {
//                for ((index, normalizedLandmark) in landmark.withIndex()) {
//                    // 필요 없는 랜드마크 인덱스는 스킵
//                    if (index !in selectiveLandmarks) continue
//
//                    // 각 랜드마크 포인트 그리기
//                    canvas.drawPoint(
//                        normalizedLandmark.x() * imageWidth * scaleFactor,
//                        normalizedLandmark.y() * imageHeight * scaleFactor,
//                        pointPaint
//                    )
//                }

//                // PoseLandmarker.POSE_LANDMARKS에서 특정 라인만 그리기
//                PoseLandmarker.POSE_LANDMARKS.forEach {
//                    if (it!!.start() in selectiveLandmarks && it.end() in selectiveLandmarks) {
//                        canvas.drawLine(
//                            poseLandmarkerResult.landmarks().get(0).get(it.start()).x() * imageWidth * scaleFactor,
//                            poseLandmarkerResult.landmarks().get(0).get(it.start()).y() * imageHeight * scaleFactor,
//                            poseLandmarkerResult.landmarks().get(0).get(it.end()).x() * imageWidth * scaleFactor,
//                            poseLandmarkerResult.landmarks().get(0).get(it.end()).y() * imageHeight * scaleFactor,
//                            linePaint
//                        )
//                    }
//                }

                if (exerciseType == "PUSH_UP") {
                    // 특정 라인(예: 어깨에서 팔꿈치, 팔꿈치에서 손목)을 수동으로 그림
                    val landmarks = poseLandmarkerResult.landmarks().get(0)

                    // 팔꿈치(14) -> 손목(16)
                    linePaint.color = if (feedback1_status) Color.argb(90, 0, 255, 0) else Color.argb(90, 255, 0, 0)
                    canvas.drawLine(
                        landmarks.get(14).x() * imageWidth * scaleFactor,
                        landmarks.get(14).y() * imageHeight * scaleFactor,
                        landmarks.get(16).x() * imageWidth * scaleFactor,
                        landmarks.get(16).y() * imageHeight * scaleFactor,
                        linePaint
                    )

                    // 팔꿈치(13) -> 손목(15)
                    canvas.drawLine(
                        landmarks.get(13).x() * imageWidth * scaleFactor,
                        landmarks.get(13).y() * imageHeight * scaleFactor,
                        landmarks.get(15).x() * imageWidth * scaleFactor,
                        landmarks.get(15).y() * imageHeight * scaleFactor,
                        linePaint
                    )

                    linePaint.color = if (feedback2_status) Color.argb(90, 0, 255, 0) else Color.argb(90, 255, 0, 0)
                    //왼쪽 어깨-허리
                    canvas.drawLine(
                        landmarks.get(11).x() * imageWidth * scaleFactor,
                        landmarks.get(11).y() * imageHeight * scaleFactor,
                        landmarks.get(23).x() * imageWidth * scaleFactor,
                        landmarks.get(23).y() * imageHeight * scaleFactor,
                        linePaint
                    )

                    //왼쪽 허리-무릎
                    canvas.drawLine(
                        landmarks.get(23).x() * imageWidth * scaleFactor,
                        landmarks.get(23).y() * imageHeight * scaleFactor,
                        landmarks.get(25).x() * imageWidth * scaleFactor,
                        landmarks.get(25).y() * imageHeight * scaleFactor,
                        linePaint
                    )

                    //오른쪽 어깨-허리
                    canvas.drawLine(
                        landmarks.get(12).x() * imageWidth * scaleFactor,
                        landmarks.get(12).y() * imageHeight * scaleFactor,
                        landmarks.get(24).x() * imageWidth * scaleFactor,
                        landmarks.get(24).y() * imageHeight * scaleFactor,
                        linePaint
                    )

                    //오른쪽 허리-무릎
                    canvas.drawLine(
                        landmarks.get(24).x() * imageWidth * scaleFactor,
                        landmarks.get(24).y() * imageHeight * scaleFactor,
                        landmarks.get(26).x() * imageWidth * scaleFactor,
                        landmarks.get(26).y() * imageHeight * scaleFactor,
                        linePaint
                    )
                }
                else if(exerciseType == "PULL_UP"){

                    val landmarks = poseLandmarkerResult.landmarks().get(0)

                    linePaint.color = if (feedback1_status) Color.argb(90, 0, 255, 0) else Color.argb(90, 255, 0, 0)
                    canvas.drawLine(
                        landmarks.get(15).x() * imageWidth * scaleFactor,
                        landmarks.get(15).y() * imageHeight * scaleFactor,
                        landmarks.get(16).x() * imageWidth * scaleFactor,
                        landmarks.get(16).y() * imageHeight * scaleFactor,
                        linePaint
                    )
                }
                else{//스쿼트
                    val landmarks = poseLandmarkerResult.landmarks().get(0)

                    //상체 기울기 피드백 컬러 변환
                    linePaint.color = if (feedback1_status) Color.argb(90, 0, 255, 0) else Color.argb(90, 255, 0, 0)
                    //오른쪽 어깨-허리
                    canvas.drawLine(
                        landmarks.get(12).x() * imageWidth * scaleFactor,
                        landmarks.get(12).y() * imageHeight * scaleFactor,
                        landmarks.get(24).x() * imageWidth * scaleFactor,
                        landmarks.get(24).y() * imageHeight * scaleFactor,
                        linePaint
                    )
                    //왼쪽 어깨-허리
                    canvas.drawLine(
                        landmarks.get(11).x() * imageWidth * scaleFactor,
                        landmarks.get(11).y() * imageHeight * scaleFactor,
                        landmarks.get(23).x() * imageWidth * scaleFactor,
                        landmarks.get(23).y() * imageHeight * scaleFactor,
                        linePaint
                    )

                    linePaint.color = if (feedback2_status) Color.argb(90, 0, 255, 0) else Color.argb(90, 255, 0, 0)
                    //오른쪽 발바닥
                    canvas.drawLine(
                        landmarks.get(30).x() * imageWidth * scaleFactor,
                        landmarks.get(30).y() * imageHeight * scaleFactor,
                        landmarks.get(32).x() * imageWidth * scaleFactor,
                        landmarks.get(32).y() * imageHeight * scaleFactor,
                        linePaint
                    )

                    //왼쪽 발바닥
                    canvas.drawLine(
                        landmarks.get(29).x() * imageWidth * scaleFactor,
                        landmarks.get(29).y() * imageHeight * scaleFactor,
                        landmarks.get(31).x() * imageWidth * scaleFactor,
                        landmarks.get(31).y() * imageHeight * scaleFactor,
                        linePaint
                    )


                }
            }
        }

    }

    fun setResults(
        poseLandmarkerResults: PoseLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE,
        exerciseType: String,
        exerciseCount: Int,
        exerciseAngle: Int,
        feedback1 : String,
        feedback2 : String,
        feedback1_status: Boolean,
        feedback2_status: Boolean

    ) {
        results = poseLandmarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        this.exerciseType = exerciseType
        this.exerciseCount = exerciseCount
        this.exerciseAngle = exerciseAngle

        this.feedback1 = feedback1
        this.feedback2 = feedback2

        this.feedback1_status = feedback1_status
        this.feedback2_status = feedback2_status

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F
    }
}