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

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Camera
import androidx.camera.core.AspectRatio
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.app_combined.PoseLandmarkerHelper
import com.example.app_combined.R
import com.example.app_combined.databinding.FragmentCameraBinding
import com.example.app_combined.ExerciseDetector
import com.example.app_combined.ExerciseType
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(), PoseLandmarkerHelper.LandmarkerListener {

    companion object {
        private const val TAG = "Pose Landmarker"
    }

    private var _fragmentCameraBinding: FragmentCameraBinding? = null
    private val fragmentCameraBinding get() = _fragmentCameraBinding!!

    // 여기서 생성한 PoseLandmarkerHelper 객체 mainActivity에 넘겨줌
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    // 기본값은 전면 카메라
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT
    private val isFrontCamera: Boolean
        get() = cameraFacing == CameraSelector.LENS_FACING_FRONT



    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(
                requireActivity(), R.id.fragment_container
            ).navigate(R.id.action_camera_to_permissions)
        }

        // Start the PoseLandmarkerHelper again when users come back
        // to the foreground.
        backgroundExecutor.execute {
            if(this::poseLandmarkerHelper.isInitialized) {
                if (poseLandmarkerHelper.isClose()) {
                    poseLandmarkerHelper.setupPoseLandmarker()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(this::poseLandmarkerHelper.isInitialized) {

            // 리소스 해제를 위해 releaseResources 호출
            poseLandmarkerHelper.releaseResources()

            // PoseLandmarkerHelper를 닫고 리소스를 해제
            backgroundExecutor.execute { poseLandmarkerHelper.clearPoseLandmarker() }
        }
    }

    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()

        // Shut down our background executor
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding =
            FragmentCameraBinding.inflate(inflater, container, false)
        return fragmentCameraBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Initialize our background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()

        // Initialize the PoseLandmarkerHelper here synchronously, before any background task starts.
        // PoseLandmarkerHelper 초기화
        poseLandmarkerHelper = initializePoseLandmarkerHelper()

        // Wait for the views to be properly laid out
        fragmentCameraBinding.viewFinder.post {
            // Set up the camera and its use cases
            setUpCamera()
        }



        //운동타
        fragmentCameraBinding.btnToggleMenu.findViewById<Button>(R.id.btnToggleMenu)

        fragmentCameraBinding.btnToggleMenu.setOnClickListener {
            val buttonGroup = fragmentCameraBinding.buttonGroup
            val btnToggleMenu = fragmentCameraBinding.btnToggleMenu
            if (buttonGroup.visibility == View.VISIBLE) {
                buttonGroup.visibility = View.GONE
            } else {
                buttonGroup.visibility = View.VISIBLE
                btnToggleMenu.visibility = View.GONE
            }
        }
        // 버튼 그룹 닫기 버튼 클릭 이벤트 설정
        fragmentCameraBinding.buttonGroup.findViewById<ImageButton>(R.id.btnCloseGroup).setOnClickListener {
            fragmentCameraBinding.buttonGroup.visibility = View.GONE
            fragmentCameraBinding.btnToggleMenu.visibility = View.VISIBLE
        }

        // 전환 버튼 클릭 시 카메라 전환
        fragmentCameraBinding.toggleCameraButton.setOnClickListener {
            toggleCameraFacing()
        }

        // 버튼 참조 및 클릭 이벤트 설정
        fragmentCameraBinding.buttonGroup.findViewById<Button>(R.id.btnPushUp).setOnClickListener {
            changeExerciseTo(ExerciseType.PUSH_UP)
        }
        fragmentCameraBinding.buttonGroup.findViewById<Button>(R.id.btnPullUp).setOnClickListener {
            changeExerciseTo(ExerciseType.PULL_UP)
        }
        fragmentCameraBinding.buttonGroup.findViewById<Button>(R.id.btnSquat).setOnClickListener {
            changeExerciseTo(ExerciseType.SQUAT)
        }

    }

    private fun changeExerciseTo(exerciseType: ExerciseType) {
        // 현재 PoseLandmarkerHelper의 감지기를 새로운 감지기로 설정

        if (poseLandmarkerHelper.currentDetector.getType() != exerciseType.name) {
            poseLandmarkerHelper.setExerciseDetector(exerciseType)  // 감지기 변경
            Toast.makeText(requireContext(), "${exerciseType.name} 감지기로 변경되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "${exerciseType.name} 감지기가 이미 선택되어 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializePoseLandmarkerHelper(): PoseLandmarkerHelper {

        // 기본 운동 감지기를 푸시업으로 설정
        val initialDetector = ExerciseDetector(ExerciseType.PUSH_UP)

        // This initialization could still fail if the context or viewModel properties
        // are not ready. This method assumes they are initialized before calling.
        return PoseLandmarkerHelper(
            context = requireContext(),
            runningMode = RunningMode.LIVE_STREAM,
            poseLandmarkerHelperListener = this,
            currentDetector = initialDetector
        ).also {
            // Setup or any initial processing required immediately after creation.
            it.setupPoseLandmarker()
        }
    }

    private fun toggleCameraFacing() {
        cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_FRONT) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        // 카메라 방향 변경 후 PoseLandmarkerHelper에 전달
        poseLandmarkerHelper.updateCameraDirection(isFrontCamera)

        bindCameraUseCases() // 변경된 방향으로 카메라 설정 재바인딩
    }

    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
            .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        detectPose(image)
                    }
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun detectPose(imageProxy: ImageProxy) {
        if(this::poseLandmarkerHelper.isInitialized) {
            poseLandmarkerHelper.detectLiveStream(
                imageProxy = imageProxy,
                isFrontCamera = isFrontCamera // 카메라 방향 설정
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            fragmentCameraBinding.viewFinder.display.rotation
    }

    // Update UI after pose have been detected. Extracts original
    // image height/width to scale and place the landmarks properly through
    // OverlayView
    override fun onResults(
        resultBundle: PoseLandmarkerHelper.ResultBundle
    ) {
        activity?.runOnUiThread {
            if (_fragmentCameraBinding != null) {

                // Pass necessary information to OverlayView for drawing on the canvas
                fragmentCameraBinding.overlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM,
                    resultBundle.exerciseType,
                    resultBundle.exerciseCount,
                    resultBundle.exerciseAngle,
                    resultBundle.feedback1,
                    resultBundle.feedback2,
                    resultBundle.feedback1_status,
                    resultBundle.feedback2_status
                )

                fragmentCameraBinding.exerciseCountText.text = "${resultBundle.exerciseCount}"
                fragmentCameraBinding.exerciseTypeText.text = "${resultBundle.exerciseType}"

                // Force a redraw
                fragmentCameraBinding.overlay.invalidate()
            }
        }
    }

    override fun onError(error: String, errorCode: Int) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }
}
