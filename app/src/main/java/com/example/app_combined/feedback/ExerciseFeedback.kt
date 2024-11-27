package com.example.app_combined

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

interface ExerciseFeedback {
    fun provideFeedback(landmarks: List<List<NormalizedLandmark>>): String
}