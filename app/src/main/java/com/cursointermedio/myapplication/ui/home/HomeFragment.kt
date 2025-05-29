package com.cursointermedio.myapplication.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.databinding.FragmentHomeBinding
import com.cursointermedio.myapplication.ui.home.dialog.AddNoteDialog
import com.cursointermedio.myapplication.ui.home.dialog.TracDialog
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initListeners()
    }

    private fun initListeners() {
        binding.cvTrac.setupTouchAction {
            createTracDialog(homeViewModel.tracInfo.value)
        }
        binding.ivTracInfo.setupTouchAction {
            val popup = PopupMenu(requireContext(), binding.ivTracInfo)
            popup.menu.add("Eliminar trac")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Eliminar trac" -> {
                        homeViewModel.deleteTrac()
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
        binding.cvNotes.setupTouchAction {
            createAddNoteDialog()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.tracInfo.collectLatest { tracInfo ->
                    tracInfo?.let {
                        binding.tvResultTrac.visibility = View.GONE
                        binding.lyResultTrac.visibility = View.VISIBLE
                        binding.ivNotTracInfo.visibility = View.GONE
                        binding.ivTracInfo.visibility = View.VISIBLE

                        val push = tracInfo.push ?: 0
                        val pull = tracInfo.pull ?: 0
                        val leg = tracInfo.leg ?: 0
                        val mental = (tracInfo.motivation ?: 0) +
                                (tracInfo.recuperation ?: 0) +
                                (tracInfo.rest ?: 0) +
                                (tracInfo.technique ?: 0)

                        binding.tvResultPush.text = getString(R.string.home_sub_trac_push, push)
                        binding.tvResultPull.text = getString(R.string.home_sub_trac_pull, pull)
                        binding.tvResultLeg.text = getString(R.string.home_sub_trac_leg, leg)
                        binding.tvResultMental.text =
                            getString(R.string.home_sub_trac_mental, mental)

                    } ?: run {
                        binding.lyResultTrac.visibility = View.GONE
                        binding.tvResultTrac.visibility = View.VISIBLE
                        binding.ivTracInfo.visibility = View.GONE
                        binding.ivNotTracInfo.visibility = View.VISIBLE

                    }
                }
            }
        }
    }

    private fun createAddNoteDialog() {
        val view = binding.cvNotes
        hideViewWithAnimation(view)
        val dialog = AddNoteDialog { showViewWithAnimation(view) }
        dialog.show(parentFragmentManager, "dialog")

    }

    private fun createTracDialog(tracInfo: TracEntity?) {
        val dialog = TracDialog(tracInfo, onItemSave = { trac ->
            homeViewModel.insertOrUpdateTrac(trac)
        })
        dialog.show(parentFragmentManager, "dialog")
    }

    private fun hideViewWithAnimation(view: View) {
        view.animate()
            .alpha(0f)
            .translationY(view.height.toFloat() / 2) // baja media altura, ajustá como quieras
            .setDuration(300)
            .withEndAction {
                view.visibility = View.GONE
                view.translationY = 0f  // reset para la próxima vez que aparezca
                view.alpha = 1f         // reset alpha también
            }
            .start()
    }

    private fun showViewWithAnimation(view: View) {
         view.apply {
            alpha = 0f
            translationY = view.height.toFloat() / 2
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start()
        }
    }

    private fun initUI() {

        binding.lyToSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        binding.cvTraining.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_navigation)
        }
        observeDataUser()

    }

    private fun observeDataUser() {
        homeViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.userName.text = getString(R.string.home_user_name, user.name)

                Glide.with(this)
                    .load(user.photoUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_circle) // Imagen temporal mientras carga
                    .error(R.drawable.ic_circle)       // Imagen por si falla
                    .into(binding.ivProfileImage)  // tu ImageView
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

}





