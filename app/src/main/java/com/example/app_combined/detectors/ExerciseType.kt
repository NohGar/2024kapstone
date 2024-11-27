package com.example.app_combined

enum class ExerciseType(
    val leftStartPoint: Int, val leftMidPoint: Int, val leftEndPoint: Int,
    val rightStartPoint: Int, val rightMidPoint: Int, val rightEndPoint: Int,
    val add_leftPoint1: Int, val add_leftPoint2: Int,
    val add_rightPoint1: Int, val add_rightPoint2: Int,
    val feedback1_status: Boolean, val feedback2_status: Boolean,
    val feedback1 : String, val feedback2 : String,
    val minAngle: Int, val maxAngle: Int, name: String
) {
    PUSH_UP(11, 13, 15,
        12, 14, 16,
        23, 25,
        24,26,
        false,false,
        "아래팔 각도", "허리 각도",
        80, 160, "PUSH_UP"),

    PULL_UP(11, 13, 15,
        12, 14, 16,
        2,27,
        10,28,
        false,false,
        "양손 너비","",
        60, 160, "PULL_UP"),

    SQUAT(23, 25, 29,
        24, 26, 30,
        11,31,
        12,32,
        false,false,
        "상체 각도","발 뒤꿈치",
        60, 160, "SQUAT");

}