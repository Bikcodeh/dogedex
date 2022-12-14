package com.bikcodeh.dogrecognizer.scandogpresentation.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.bikcodeh.dogrecognizer.core.util.Util
import com.bikcodeh.dogrecognizer.core.util.extension.*
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.dogrecognizer.scandogpresentation.databinding.FragmentScanDogBinding
import com.bikcodeh.dogrecognizer.scandogpresentation.ml.Classifier
import com.bikcodeh.dogrecognizer.scandogpresentation.ui.state.Effect
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import com.bikcodeh.dogrecognizer.core.R as RC

@AndroidEntryPoint
class ScanDogFragment : Fragment() {

    private var _binding: FragmentScanDogBinding? = null
    private val binding: FragmentScanDogBinding
        get() = _binding!!

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    @Inject
    lateinit var classifier: Classifier

    private val scanDogViewModel: ScanDogViewModel by viewModels()
    private lateinit var safeContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanDogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
        setListeners()
        setUpCollectors()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setListeners() {
        with(binding) {
            takePhotoBtn.setOnClickListener {
                takePhoto()
            }
        }
    }

    private fun setUpCollectors() {
        observeFlows { scope ->

            scope.launch {
                scanDogViewModel.effect.collect { state ->
                    when (state) {
                        is Effect.NavigateToDetail -> {
                            val moshi = Moshi.Builder().build()
                            val jsonAdapter: JsonAdapter<Dog> = moshi.adapter(Dog::class.java)
                            val itemDog = state.dog.copy()
                            itemDog.imageUrl = itemDog.imageUrl.encode()
                            val jsonDog = jsonAdapter.toJson(itemDog)
                            val request =
                                NavDeepLinkRequest.Builder.fromUri(
                                    "android-app://DogDetailFragment/${jsonDog.encode()}".toUri()
                                ).build()
                            findNavController().navigate(
                                request,
                                Util.setDefaultTransitionAnimation()
                            )
                        }
                        is Effect.ShowSnackBar -> binding.root.snack(
                            getString(
                                state.resId
                            )
                        )
                        is Effect.IsLoading -> {
                            if (state.loading) {
                                binding.loadingScanDogPb.show()
                            } else {
                                binding.loadingScanDogPb.hide()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startCamera() {
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder().build()
                imageCapture = ImageCapture.Builder().build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    imageProxy.close()
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this as LifecycleOwner,
                        cameraSelector,
                        imageAnalysis,
                        preview,
                        imageCapture
                    )
                    preview?.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                } catch (exc: Exception) {
                    Log.e(this::class.java.name, "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(safeContext))
        } catch (e: Exception) {
            Log.e(this::class.java.name, "FAIL ${e.message}")
        }
    }

    private fun getOutputPhotoFile(): File? {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(RC.string.app_name) + ".jpg").apply { mkdirs() }
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

            val image = imageCapture ?: return

            image.takePicture(outputFileOptions, cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val photoUri = outputFileResults.savedUri
                        val bitmap = BitmapFactory.decodeFile(photoUri?.path)
                        val dogRecognition = classifier.recognizeImage(bitmap).first()

                        classifier.recognizeImage(bitmap)
                        scanDogViewModel.recognizeDogById(dogRecognition.id)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        binding.root.snack(getString(RC.string.error_taking_photo))
                    }
                })
        }
    }
}