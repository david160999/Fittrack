package com.cursointermedio.myapplication.ui.home.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.databinding.ActivityLoginBinding
import com.cursointermedio.myapplication.domain.model.UserSettings
import com.cursointermedio.myapplication.domain.useCase.GetUserPreferencesUseCase
import com.cursointermedio.myapplication.ui.home.MainActivity
import com.cursointermedio.myapplication.ui.settings.Language
import com.cursointermedio.myapplication.utils.extensions.setupTouchAction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var getUserPreferencesUseCase: GetUserPreferencesUseCase

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth = FirebaseAuth.getInstance()

    // Launcher para el flujo de autenticación con Google
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("GoogleLogin", "Google sign in failed", e)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = auth.currentUser
        if (user != null) {
            goToMainScreen()
        }

        // Configuración para Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Del google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Botón de login con Google
        binding.btnGoogleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        // Botón de login manual
        binding.btnManualLogin.setOnClickListener {
            val password = binding.etPassword.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email = email, password = password)
            } else {
                Toast.makeText(this, getString(R.string.login_complete_fields), Toast.LENGTH_SHORT).show()
            }
        }

        // Botón de registro manual (abre diálogo)
        binding.btnManualRegister.setOnClickListener {
            createRegisterDialog()
        }

        // Recuperación de contraseña
        binding.forgotPasswordText.setupTouchAction {
            val email = binding.etEmail.text.toString()
            if (email.isNotEmpty()){
                sendRecuperationEmail(email) { success, error ->
                    if (success) {
                        Toast.makeText(this, getString(R.string.login_check_email), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(this, getString(R.string.login_email_field), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Autenticación con Google en Firebase
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val settings = UserSettings(
                        username = user?.displayName ?: "",
                        email = user?.email ?: "",
                        language = Language.ENGLISH
                    )

                    lifecycleScope.launch(Dispatchers.IO) {
                        getUserPreferencesUseCase.saveUserSettings(settings)
                    }
                    goToMainScreen()
                } else {
                    Log.w("GoogleLogin", getString(R.string.login_google_failure), task.exception)
                }
            }
    }

    // Crea una nueva cuenta de usuario
    private fun createAccount(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val settings = UserSettings(
                        username = username,
                        email = email,
                        language = Language.ENGLISH
                    )

                    lifecycleScope.launch(Dispatchers.IO) {
                        getUserPreferencesUseCase.saveUserSettings(settings)
                        goToMainScreen()
                    }
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    // Login manual con email y contraseña
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    goToMainScreen()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    // Crea el diálogo de registro manual
    private fun createRegisterDialog() {
        val dialog = AddRegisterDialog(
            onItemSave = { username, email, password ->
                createAccount(username = username, email = email, password = password)
            }
        )
        dialog.show(supportFragmentManager, "dialog")
    }

    // Envía un correo de recuperación de contraseña
    private fun sendRecuperationEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null) // Éxito
                } else {
                    onResult(false, task.exception?.localizedMessage) // Error
                }
            }
    }

    // Abre la pantalla principal y cierra el login
    private fun goToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}