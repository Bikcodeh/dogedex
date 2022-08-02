package com.bikcodeh.dogrecognizer.presentation.doglist

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.databinding.FragmentScanDogBinding
import com.bikcodeh.dogrecognizer.presentation.util.extension.hide
import com.bikcodeh.dogrecognizer.presentation.util.extension.show
import com.bikcodeh.dogrecognizer.presentation.util.extension.snack
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanDogFragment : Fragment() {

    private var _binding: FragmentScanDogBinding? = null
    private val binding: FragmentScanDogBinding
        get() = _binding!!

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanDogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setUpCamera()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        if (::cameraExecutor.isInitialized)
            cameraExecutor.shutdown()
    }

    private fun setListeners() {
        with(binding) {
            takePhotoBtn.setOnClickListener {
                takePhoto()
            }
            takeAgainBtn.setOnClickListener {
                takePhotoGroup.show()
                photoTakenGroup.hide()
            }
            okBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setUpCamera() {
        binding.cameraPreview.post {
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(binding.cameraPreview.display.rotation)
                .build()

            cameraExecutor = Executors.newSingleThreadExecutor()
            startCamera()
        }
    }

    private fun startCamera() {
        try {
            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                val preview = Preview.Builder().build()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    imageProxy.close()
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        viewLifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalysis
                    )
                    preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                } catch (exc: Exception) {
                    Log.e(DogsFragment::class.java.name, "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context!!))
        } catch (e: Exception) {
            Log.e(DogsFragment::class.java.name, "FAIL ${e.message}")
        }
    }

    private fun getOutputPhotoFile(): File? {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name) + ".jpg").apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            activity?.filesDir
        }
    }

    private fun takePhoto() {
        getOutputPhotoFile()?.let {

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(it).build()

            imageCapture.takePicture(outputFileOptions, cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val photoUri = outputFileResults.savedUri
                        handleOnSuccessPhoto(photoUri)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        binding.root.snack(getString(R.string.error_taking_photo))
                    }
                })
        }
    }

    private fun handleOnSuccessPhoto(uri: Uri?) {
        with(binding) {
            uri?.let { uri ->
                uri.path?.let { safePath ->
                    MainScope().launch {
                        takePhotoGroup.hide()
                        photoTakenIv.load(safePath)
                        photoTakenGroup.show()
                    }
                } ?: {
                    MainScope().launch {
                        takePhotoGroup.show()
                        photoTakenGroup.hide()
                    }
                }
            } ?: run {
                MainScope().launch {
                    takePhotoGroup.show()
                    photoTakenGroup.hide()
                }
            }
        }
    }
}