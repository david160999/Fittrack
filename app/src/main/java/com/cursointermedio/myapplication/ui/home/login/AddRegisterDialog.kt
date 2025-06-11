package com.cursointermedio.myapplication.ui.home.login

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.DialogRegisterBinding
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// Diálogo de hoja inferior para registrar manualmente un usuario con username, email y password.
class AddRegisterDialog(
    private val onItemSave: (String, String, String) -> Unit, // Callback al guardar (username, email, password)
) : BottomSheetDialogFragment() {

    private var _binding: DialogRegisterBinding? = null
    private val binding get() = _binding!!

    // Infla la vista del diálogo
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Configura la UI y listeners tras crear la vista
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        initUi()
        initListeners()
    }

    // Inicializa los listeners de los botones del diálogo
    private fun initListeners() {
        binding.btnManualRegister.setupTouchAction {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                onItemSave(username, email, password)
            } else {
                Toast.makeText(context, "Debes de rellenar todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Puedes inicializar UI adicional aquí si lo necesitas
    private fun initUi() {}

    // Limpia el binding al destruir la vista para evitar memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Usa un estilo personalizado para el diálogo de hoja inferior
    override fun getTheme(): Int {
        return R.style.BottomSheetDialogAnimationStyle
    }

    // Ajusta la altura y comportamiento de la hoja inferior al iniciarse
    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            it.background = null
            val layoutParams = it.layoutParams
            val displayMetrics = Resources.getSystem().displayMetrics
            val maxHeight = (displayMetrics.heightPixels * 0.95).toInt()
            layoutParams.height = maxHeight
            it.layoutParams = layoutParams
            behavior.isDraggable = true
            behavior.peekHeight = maxHeight  // para que empiece con esa altura
            behavior.state = BottomSheetBehavior.STATE_EXPANDED // para abrir expandido
        }
    }
}