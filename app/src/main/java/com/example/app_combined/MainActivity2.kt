
package com.example.app_combined

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment

import com.example.app_combined.ExerciseDetector
import com.example.app_combined.databinding.ActivityMain2Binding

//for posing
class MainActivity2 : AppCompatActivity(){
    private lateinit var activityMainBinding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //뷰 바인딩
        activityMainBinding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)


    }

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
    override fun onBackPressed() {
        finish()
    }
}