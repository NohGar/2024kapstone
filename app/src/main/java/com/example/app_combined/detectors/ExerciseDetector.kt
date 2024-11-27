package com.example.app_combined

import android.util.Log
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.roundToInt

class ExerciseDetector(private var exerciseType: ExerciseType) {
    private var count = 0
    private var angle = 0
    private var isUp = false
    private var isDown = true
    private var frameCounter = 0
    private val frameInterval = 2
    var feedback1 = exerciseType.feedback1
    var feedback2 = exerciseType.feedback2
    var feedback1_status = exerciseType.feedback1_status
    var feedback2_status = exerciseType.feedback2_status


    fun detectMovement(landmarks: List<NormalizedLandmark>?) {
        if (checkFrame() || landmarks == null) return

        val left = landmarks.getOrNull(exerciseType.leftStartPoint)
        val right = landmarks.getOrNull(exerciseType.rightStartPoint)

        if (left == null || right == null) return


        //for count

        val (start, middle, end) =
            if ((exerciseType.name == "PUSH_UP" || exerciseType.name == "SQUAT")
                && left.z() < right.z()) {
                Triple(
                    landmarks.getOrNull(exerciseType.leftStartPoint),
                    landmarks.getOrNull(exerciseType.leftMidPoint),
                    landmarks.getOrNull(exerciseType.leftEndPoint),
                )
            }
            else  if (exerciseType.name == "PUSH_UP" || exerciseType.name == "SQUAT")
            {
                Triple(
                    landmarks.getOrNull(exerciseType.rightStartPoint),
                    landmarks.getOrNull(exerciseType.rightMidPoint),
                    landmarks.getOrNull(exerciseType.rightEndPoint)
                )
            }else{
                Triple(
                    //우 어깨
                    landmarks.getOrNull(exerciseType.rightStartPoint),
                    //좌 어깨
                    landmarks.getOrNull(exerciseType.leftStartPoint),
                    //우 엘보
                    landmarks.getOrNull(exerciseType.rightMidPoint)
                )
            }

        angle = calculateAngle(start, middle, end).roundToInt()

        //for feedback
        val (point1, point2)=
            if((exerciseType.name == "PUSH_UP" || exerciseType.name == "SQUAT")
                && left.z() < right.z()){
                Pair(
                    landmarks.getOrNull(exerciseType.add_leftPoint1),
                    landmarks.getOrNull(exerciseType.add_leftPoint2)
                )
            }
            else if((exerciseType.name == "PUSH_UP" || exerciseType.name == "SQUAT")
            ){
                Pair(
                    landmarks.getOrNull(exerciseType.add_rightPoint1),
                    landmarks.getOrNull(exerciseType.add_rightPoint2)
                )
            }
            else{
                Pair(
                    landmarks.getOrNull(exerciseType.add_rightPoint1),
                    landmarks.getOrNull(exerciseType.add_leftPoint1)
                )
            }



        if(exerciseType.name == "PUSH_UP") {

            val Knee = point1
            val waist = point2
            val shoulder = start
            var waistAngle = calculateAngle(shoulder,waist,Knee)

            //푸쉬업 피드백1 : 아래팔이 지면과 90도에 근접하게 이루어져야한다
            if(end != null && middle != null){

                var W_y = end.y();
                var E_y= middle.y();
                var diff = Math.abs(W_y - E_y)

                var forearm_length = length(start, middle)

                if(diff < forearm_length/3)
                    feedback1_status = true
                else
                    feedback1_status = false

            }
            //푸쉬업 피드백2 : 허리의 각도가 적당히 펴져있어야 한다
            if(waistAngle < 20) {
                feedback2_status = true
            }
            else{
                feedback2_status = false
            }

            if(feedback1_status && feedback2_status) {
                // 특정 각도로 올라간 상태를 감지
                if (angle <= exerciseType.minAngle) {
                    isUp = true
                } else if (angle >= exerciseType.maxAngle && isUp) {
                    isUp = false
                    count++
                    Log.d("OverlayView", "pull-up count: $count")
                }
            }
        }
        else if(exerciseType.name == "PULL_UP") {

            val left_sh = landmarks.get(exerciseType.leftStartPoint)
            //val left_el = landmarks.get(exerciseType.leftMidPoint)
            val left_wr = landmarks.get(exerciseType.leftEndPoint)
            // val left_eye = landmarks.get(exerciseType.add_leftPoint1)

            val right_sh = landmarks.get(exerciseType.rightStartPoint)
            val right_el = landmarks.get(exerciseType.rightMidPoint)
            val right_wr = landmarks.get(exerciseType.rightEndPoint)
            val right_mouth = landmarks.get(exerciseType.add_rightPoint1)

            val dis_sh = x_length(right_sh,left_sh)
            val dis_wr = x_length(right_wr, left_wr)

            //풀업 피드백1 : 양손의 너비가 어깨 기준 일정 범위 안에 있어야 한다
            if(dis_wr > dis_sh*1.8 && dis_wr < dis_sh*2.3){
                feedback1_status = true
            }
            else{
                feedback1_status = false
            }

            if(feedback1_status) {
                if (right_sh.y() < right_wr.y() && left_sh.y() < left_wr.y() && isDown) {
                    count++;
                    isDown = false
                } else if (right_mouth.y() > right_el.y() && !isDown) {
                    isDown = true
                }
            }
        }
        else {// 스쿼트 부분

            // 스쿼트 피드백1 : 상체의 각도는 55도 이상을 유지
            if(point1 != null && start != null) {
                var upper = simple_calAng(point1, start)

                if(upper < 35)
                    feedback1_status = true
                else
                    feedback1_status = false
            }

            // 스쿼트 피드백2 : 발바닥이 거의 뜨지 않도록
            if(point2 != null && end != null){
                var foot = simple_calAng(point2, end)

                if(foot > 75)
                    feedback2_status = true
                else
                    feedback2_status = false
            }

            var knee_angle = calculateAngle(start,middle,end)

            if(feedback1_status && feedback2_status) {

                if(start != null && middle != null) {
                    if (start.y() > middle.y())
                        isDown = true

                    if(knee_angle > 160 && isDown){
                        count++
                        isDown = false
                    }

                }
            }

        }

    }

    fun getType() = exerciseType.name

    fun getCount() = count

    fun getAngle() = angle

    fun getfeedback1() = feedback1

    fun getfeedback2() = feedback2

    fun getfeedback1_status() = feedback1_status

    fun getfeedback2_status() = feedback2_status

    fun setType(exerciseType: ExerciseType) {
        this.exerciseType = exerciseType
        resetCount()
        resetFeedbacks()
    }

    private fun resetFeedbacks(){
        feedback1 = exerciseType.feedback1
        feedback2 = exerciseType.feedback2
    }
    private fun resetCount() {
        count = 0
    }

    private fun checkFrame(): Boolean {
        return frameCounter++ % frameInterval != 0
    }

    private fun x_length(
        a: NormalizedLandmark?,
        b: NormalizedLandmark?
    ): Double{
        if(a==null || b==null)return 0.0

        return Math.abs(a.x().toDouble() - b.x().toDouble())
    }

    private fun length(
        a: NormalizedLandmark?,
        b: NormalizedLandmark?
    ): Double{
        if(a==null || b==null)return 0.0

        return Math.sqrt(Math.pow(a.x().toDouble()-b.x().toDouble(),2.0)+
                Math.pow(a.y().toDouble() -b.y().toDouble(),2.0))
    }

    private fun simple_calAng(
        a:NormalizedLandmark,
        b:NormalizedLandmark
    ):Double{
        if( a == null || b == null )return 0.0

        val ab = Math.sqrt(
            Math.pow(b.x().toDouble() - a.x().toDouble(), 2.0) + Math.pow(
                b.y().toDouble() - a.y().toDouble(), 2.0
            )
        )
        val bc = b.y().toDouble() - a.y().toDouble();
        val ac = b.x().toDouble() - a.x().toDouble();

        val cosTheta = (ab * ab + bc * bc - ac * ac) / (2 * ab * bc)
        return Math.toDegrees(Math.acos(cosTheta)).let { if (it.isNaN()) 180.0 else it }
    }

    private fun calculateAngle(
        a: NormalizedLandmark?,
        b: NormalizedLandmark?,
        c: NormalizedLandmark?
    ): Double {
        if (a == null || b == null || c == null) return 0.0
        val ab = Math.sqrt(
            Math.pow(b.x().toDouble() - a.x().toDouble(), 2.0) + Math.pow(
                b.y().toDouble() - a.y().toDouble(), 2.0
            )
        )
        val bc = Math.sqrt(
            Math.pow(c.x().toDouble() - b.x().toDouble(), 2.0) + Math.pow(
                c.y().toDouble() - b.y().toDouble(), 2.0
            )
        )
        val ac = Math.sqrt(
            Math.pow(c.x().toDouble() - a.x().toDouble(), 2.0) + Math.pow(
                c.y().toDouble() - a.y().toDouble(), 2.0
            )
        )

        val cosTheta = (ab * ab + bc * bc - ac * ac) / (2 * ab * bc)
        return Math.toDegrees(Math.acos(cosTheta)).let { if (it.isNaN()) 180.0 else it }
    }
}
