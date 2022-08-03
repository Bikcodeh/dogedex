package com.bikcodeh.dogrecognizer.presentation.doglist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.databinding.FragmentDogsBinding
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.presentation.account.login.LoginActivity
import com.bikcodeh.dogrecognizer.presentation.util.Constants
import com.bikcodeh.dogrecognizer.presentation.util.Permissions.hasCameraPermission
import com.bikcodeh.dogrecognizer.presentation.util.Permissions.requestCameraPermission
import com.bikcodeh.dogrecognizer.presentation.util.extension.hide
import com.bikcodeh.dogrecognizer.presentation.util.extension.observeFlows
import com.bikcodeh.dogrecognizer.presentation.util.extension.show
import com.bikcodeh.dogrecognizer.presentation.util.extension.snack
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val GRID_SPAN_COUNT = 3
private const val TOTAL_REQUIRED_PERMISSIONS_COUNT = 3

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
        builder.setPositiveButton(getString(R.string.logout)) { _, _ ->
            logOut()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
        builder.setTitle(getString(R.string.logout))
        builder.setMessage(getString(R.string.logout_description))
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
                    handleViewOnLoading(state.isLoading)
                    handleViewsOnError(state.error)
                    state.dogs?.let {
                        handleViewOnSuccess(it)
                    }
                }
            }
            scope.launch {
                dogViewModel.addDogState.collect { state ->
                    if (state.isSuccess == true) {
                        binding.coordinatorParent.snack(getString(R.string.added))
                    } else {
                        state.error?.let {
                            binding.coordinatorParent.snack(getString(it))
                        }
                    }

                    state.isLoading?.let {
                        if (it) {
                            binding.loadingPb.show()
                        } else {
                            binding.loadingPb.hide()
                        }
                    }
                    state.error?.let {
                        binding.coordinatorParent.snack(getString(it))
                    }
                    dogViewModel.onDogAdded()
                }
            }
        }
    }

    private fun handleViewsOnError(resId: Int?) {
        with(binding) {
            resId?.let {
                dogListRv.hide()
                viewEmptyDogs.root.hide()
                viewErrorDogs.root.show()
                viewErrorDogs.errorTextTv.text = getString(it)
                scanDogBtn.hide()
            } ?: run {
                binding.viewErrorDogs.root.hide()
            }
        }
    }

    private fun handleViewOnLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                dogListRv.hide()
                viewErrorDogs.root.hide()
                viewEmptyDogs.root.hide()
                scanDogBtn.hide()
                loadingPb.show()
            } else {
                loadingPb.hide()
            }
        }
    }

    private fun handleViewOnSuccess(dogs: List<Dog>) {
        with(binding) {
            scanDogBtn.show()
            if (dogs.isNotEmpty()) {
                dogListRv.show()
                dogAdapter.submitList(dogs)
                viewEmptyDogs.root.hide()
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
                findNavController().navigate(R.id.action_dogsFragment_to_scanDogFragment)
            } else {
                requestCameraPermission(this)
            }
        }
    }

    private fun logOut() {
        dogViewModel.logOut()
        Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

    private fun navigateToDetail(dog: Dog) {
        val action = DogsFragmentDirections.actionDogsFragmentToDogDetailFragment(dog)
        findNavController().navigate(action)
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
        if (requestCode == Constants.PERMISSION_CAMERA_REQUEST_CODE
            && perms.count() == TOTAL_REQUIRED_PERMISSIONS_COUNT
        ) {
            findNavController().navigate(R.id.action_dogsFragment_to_scanDogFragment)
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