package com.cursointermedio.myapplication.ui.home

import android.animation.LayoutTransition
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.entities.TracEntity
import com.cursointermedio.myapplication.databinding.FragmentHomeBinding
import com.cursointermedio.myapplication.ui.home.adapter.HomeMenuOptionAdapter
import com.cursointermedio.myapplication.ui.home.dialog.AddNoteDialog
import com.cursointermedio.myapplication.ui.home.dialog.AddWeightDialog
import com.cursointermedio.myapplication.ui.home.dialog.TracDialog
import com.cursointermedio.myapplication.ui.settings.SettingsUiState
import com.cursointermedio.myapplication.ui.training.adapter.TrainingMenuAdapter
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.cursointermedio.myapplication.utils.extensions.showSnackbar
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
        binding.ivResultTracInfo.setupTouchAction {
            createMenuOption(
                binding.ivResultTracInfo,
                ContextCompat.getString(requireContext(), R.string.home_trac_delete)
            ) { homeViewModel.deleteTrac() }
        }
        binding.cvNotes.setupTouchAction {
            createAddNoteDialog()
        }

        binding.cvWeight.setupTouchAction {
            createAddWeightDialog()
        }
        binding.ivResultWeight.setupTouchAction {
            createMenuOption(
                binding.ivResultWeight,
                ContextCompat.getString(requireContext(), R.string.home_weight_delete)
            ) { homeViewModel.deleteBodyWeight() }
        }
        binding.ivResultNotes.setupTouchAction {
            createMenuOption(
                binding.ivResultNotes,
                ContextCompat.getString(requireContext(), R.string.home_notes_delete)
            ) { homeViewModel.deleteNote() }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.tracInfo.collectLatest { tracInfo ->
                        tracInfo?.let {
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

                            binding.ivNotTracInfo.visibility = View.GONE
                            binding.tvResultTrac.visibility = View.GONE
                            binding.lyResultTrac.visibility = View.VISIBLE
                            binding.ivResultTracInfo.visibility = View.VISIBLE

                        } ?: run {
                            binding.lyResultTrac.visibility = View.GONE
                            binding.tvResultTrac.visibility = View.VISIBLE
                            binding.ivResultTracInfo.visibility = View.GONE
                            binding.ivNotTracInfo.visibility = View.VISIBLE

                        }
                    }
                }
                launch {
                    homeViewModel.dateInfo.collectLatest { dateInfo ->
                        dateInfo?.let {
                            if (it.note != null) {
                                binding.tvResultNotes.text = it.note.toString()

                                binding.tvSubNotes.visibility = View.GONE
                                binding.ivNotResultNotes.visibility = View.GONE
                                binding.lyResultNotes.visibility = View.VISIBLE
                                binding.ivResultNotes.visibility = View.VISIBLE

                            } else {
                                binding.lyResultNotes.visibility = View.GONE
                                binding.ivResultNotes.visibility = View.GONE
                                binding.tvSubNotes.visibility = View.VISIBLE
                                binding.ivNotResultNotes.visibility = View.VISIBLE
                            }

                            if (it.bodyWeight != null) {
                                binding.tvSubWeight.visibility = View.GONE
                                binding.ivNotResultWeight.visibility = View.GONE
                                binding.lyResultWeight.visibility = View.VISIBLE
                                binding.ivResultWeight.visibility = View.VISIBLE

                            } else {
                                binding.lyResultWeight.visibility = View.GONE
                                binding.ivResultWeight.visibility = View.GONE
                                binding.tvSubWeight.visibility = View.VISIBLE
                                binding.ivNotResultWeight.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                launch {
                    homeViewModel.userWeight.collectLatest { weight ->
                        weight?.isNotBlank().apply {
                            binding.tvResultWeight.text = weight
                        }

                    }

                }
                launch {
                    homeViewModel.userSettingsData.collectLatest {state->
                        when(state){
                            is SettingsUiState.Error -> {
                            }
                            SettingsUiState.Loading -> {
                            }
                            is SettingsUiState.Success -> {
                                binding.userName.text = getString(R.string.home_user_name, state.userSettings.username)
                            }
                        }

                    }
                }
            }
        }
    }

    private fun createAddNoteDialog() {
        val view = binding.cvNotes
        hideViewWithAnimation(view)
        val dialog = AddNoteDialog(
            onDismissCallback = { showViewWithAnimation(view) },
            onSaveClick = { notes -> homeViewModel.updateNote(notes) },
            notes = homeViewModel.dateInfo.value?.note
        )
        dialog.show(parentFragmentManager, "dialog")

    }

    private fun createAddWeightDialog() {
        val view = binding.cvWeight
        hideViewWithAnimation(view)
        val dialog = AddWeightDialog(
            onDismissCallback = { showViewWithAnimation(view) },
            onSaveClick = { weight -> homeViewModel.updateBodyWeight(weight) },
            weight = homeViewModel.dateInfo.value?.bodyWeight
        )
        dialog.show(parentFragmentManager, "dialog")

    }

    private fun createTracDialog(tracInfo: TracEntity?) {
        val dialog = TracDialog(tracInfo, onItemSave = { trac ->
            homeViewModel.insertOrUpdateTrac(trac)
        })
        dialog.show(parentFragmentManager, "dialog")
    }

    private fun hideViewWithAnimation(view: View) {
        val layout = binding.lyHomeFragment
        val transition = layout.layoutTransition
        layout.layoutTransition = null  // Desactiva momentáneamente

        view.animate()
            .alpha(0f)
            .translationY(view.height.toFloat() / 2) // baja media altura, ajustá como quieras
            .setDuration(300)
            .withEndAction {
                view.visibility = View.GONE
                view.translationY = 0f  // reset para la próxima vez que aparezca
                view.alpha = 1f         // reset alpha también
                layout.layoutTransition = transition // Vuelve a activar después
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

    private fun createMenuOption(view: View, text: String, onDelete: () -> Unit) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_training, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val recyclerView = popupView.findViewById<RecyclerView>((R.id.rvTrainingMenu))
        recyclerView.layoutManager = LinearLayoutManager(context)

        val items = listOf(
            text
        )

        val adapter = HomeMenuOptionAdapter(items) { position ->
            when (position) {
                0 -> onDelete()
            }
            popupWindow.dismiss()
        }

        recyclerView.adapter = adapter

        popupWindow.apply {
            isFocusable = true
            isOutsideTouchable = true
            elevation = 20f
            isClippingEnabled = true
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            animationStyle = R.style.MenuTRainingPopupFadeAnimation
        }
        popupWindow.showAsDropDown(view, 0, 0)

    }

    private fun initUI() {
        binding.lyHomeFragment.layoutTransition = LayoutTransition().apply {
            enableTransitionType(LayoutTransition.CHANGING)
        }

        binding.lyToSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        binding.cvTraining.setupTouchAction {
            findNavController().navigate(R.id.action_homeFragment_to_navigation)
        }
        observeDataUser()

    }

    private fun observeDataUser() {
        homeViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
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





