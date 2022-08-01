package com.bikcodeh.dogrecognizer.presentation.doglist

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bikcodeh.dogrecognizer.R
import com.bikcodeh.dogrecognizer.databinding.FragmentDogsBinding
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.presentation.account.login.LoginActivity
import com.bikcodeh.dogrecognizer.presentation.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val GRID_SPAN_COUNT = 3

@AndroidEntryPoint
class DogsFragment : Fragment() {

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
        dogViewModel.dogsLivedata.observe(viewLifecycleOwner) {
            dogAdapter.submitList(it)
        }

        observeFlows { scope ->
            scope.launch {
                dogViewModel.addDogState.collect { state ->
                    if (state.isSuccess == true) {
                        requireView().snack(getString(R.string.added))
                    } else {
                        requireView().snack(requireContext().getSafeString(state.error))
                    }

                    if (state.isLoading) {
                        binding.addDogLoadingPb.show()
                    } else {
                        binding.addDogLoadingPb.hide()
                    }
                    state.error?.let {
                        requireView().snack(getString(it))
                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding.menuBtn.setOnClickListener {
            showMenu(it)
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
}