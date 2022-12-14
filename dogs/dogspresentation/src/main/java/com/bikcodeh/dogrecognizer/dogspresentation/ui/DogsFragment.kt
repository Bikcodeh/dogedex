package com.bikcodeh.dogrecognizer.dogspresentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bikcodeh.dogrecognizer.core.util.Permissions.hasCameraPermission
import com.bikcodeh.dogrecognizer.core.util.Permissions.requestCameraPermission
import com.bikcodeh.dogrecognizer.core.util.Util
import com.bikcodeh.dogrecognizer.core.util.extension.*
import com.bikcodeh.dogrecognizer.core_model.Dog
import com.bikcodeh.dogrecognizer.dogspresentation.R
import com.bikcodeh.dogrecognizer.dogspresentation.databinding.FragmentDogsBinding
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.bikcodeh.dogrecognizer.core.R as RC

private const val GRID_SPAN_COUNT = 3
private const val TOTAL_REQUIRED_PERMISSIONS_COUNT = 3
private const val PERMISSION_CAMERA_REQUEST_CODE = 2

@AndroidEntryPoint
class DogsFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentDogsBinding? = null
    private val binding: FragmentDogsBinding
        get() = _binding!!

    private val dogViewModel by viewModels<DogListViewModel>()

    private val dogAdapter: DogListAdapter by lazy {
        DogListAdapter(
            onItemClick = {
                navigateToDetail(it)
            }, onLongClick = {
                dogViewModel.addDogToUser(it)
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setListeners()
        setUpObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {
        binding.dogListRv.apply {
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
            adapter = dogAdapter
        }
    }

    private fun confirmLogout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(RC.string.logout)) { _, _ ->
            logOut()
        }
        builder.setNegativeButton(getString(RC.string.cancel)) { _, _ -> }
        builder.setTitle(getString(RC.string.logout))
        builder.setMessage(getString(RC.string.logout_description))
        builder.create().show()
    }

    private fun showMenu(view: View) {
        val popUp = PopupMenu(requireContext(), view)
        popUp.menuInflater.inflate(R.menu.menu_logout, popUp.menu)
        popUp.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.itemLogout -> {
                    confirmLogout()
                    popUp.dismiss()
                    true
                }
                else -> false
            }
        }
        popUp.show()
    }

    private fun setUpObservers() {
        observeFlows { scope ->
            scope.launch {
                dogViewModel.dogsUiState.collect { state ->
                    state.dogs?.let {
                        handleViewOnSuccess(it)
                    }
                    state.error?.let {
                        handleViewsOnError(it)
                    } ?: run {
                        binding.viewErrorDogs.root.hide()
                    }
                }
            }

            scope.launch {
                dogViewModel.effect.collect { state ->
                    when (state) {
                        DogListViewModel.Effect.HideLoading -> binding.loadingPb.hide()
                        DogListViewModel.Effect.ShowLoading -> binding.loadingPb.show()
                        is DogListViewModel.Effect.NavigateToDetail -> {}
                        is DogListViewModel.Effect.ShowSnackBar -> binding.root.snack(
                            getString(state.resId)
                        )
                        is DogListViewModel.Effect.IsLoadingDogs -> handleViewOnLoading(state.isLoading)
                    }
                }
            }
        }
    }

    private fun handleViewsOnError(resId: Int?) {
        with(binding) {
            resId?.let {
                dogListRv.hide()
                viewErrorDogs.root.show()
                viewErrorDogs.errorTextTv.text = getString(it)
            } ?: run {
                binding.viewErrorDogs.root.hide()
            }
        }
    }

    private fun handleViewOnLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                viewLoadingDogs.root.show()
                viewLoadingDogs.root.elevation = 10f
            } else {
                viewLoadingDogs.root.hide()
            }
        }
    }

    private fun handleViewOnSuccess(dogs: List<Dog>) {
        with(binding) {
            if (dogs.isNotEmpty()) {
                scanDogBtn.show()
                dogListRv.show()
                dogAdapter.submitList(dogs)
            } else {
                binding.dogListRv.hide()
                binding.viewEmptyDogs.root.show()
            }
        }
    }

    private fun setListeners() {
        binding.menuBtn.setOnClickListener {
            showMenu(it)
        }

        binding.viewErrorDogs.tryAgainBtn.setOnClickListener {
            dogViewModel.downloadDogs()
        }

        binding.scanDogBtn.setOnClickListener {
            if (hasCameraPermission(requireContext())) {
                val request =
                    NavDeepLinkRequest.Builder.fromUri("android-app://ScanDogFragment".toUri())
                        .build()
                findNavController().navigate(request, Util.setDefaultTransitionAnimation())
            } else {
                requestCameraPermission(this)
            }
        }
    }

    private fun logOut() {
        dogViewModel.logOut()
        launchSafeActivity(
            "com.bikcodeh.dogrecognizer.authpresentation.AuthActivity",
            clear = true
        )
        activity?.finish()
    }

    private fun navigateToDetail(dog: Dog) {
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Dog> = moshi.adapter(Dog::class.java)
        val itemDog = dog.copy()
        itemDog.imageUrl = itemDog.imageUrl.encode()
        val jsonDog = jsonAdapter.toJson(itemDog)
        val request =
            NavDeepLinkRequest.Builder.fromUri(
                "android-app://DogDetailFragment/${jsonDog.encode()}".toUri()
            ).build()
        findNavController().navigate(request, Util.setDefaultTransitionAnimation())
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity())
                .build().show()
        } else {
            requestCameraPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE
            && perms.count() == TOTAL_REQUIRED_PERMISSIONS_COUNT
        ) {
            val request =
                NavDeepLinkRequest.Builder.fromUri("android-app://ScanDogFragment".toUri()).build()
            findNavController().navigate(request)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}