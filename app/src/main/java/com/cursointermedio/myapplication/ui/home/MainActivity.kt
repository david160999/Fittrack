package com.cursointermedio.myapplication.ui.home

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.local.UserPreferences
import com.cursointermedio.myapplication.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // ViewBinding para acceder de forma segura a las vistas
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // Controlador de navegación para gestionar los fragmentos
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplica el modo oscuro según la preferencia del usuario
        UserPreferences.applyDarkMode(this)
        super.onCreate(savedInstanceState)
        // Inicializa ViewBinding
        _binding = ActivityMainBinding.inflate(layoutInflater)
        // Inicializa Firebase si aún no está inicializado
        FirebaseApp.initializeApp(this)
        setContentView(binding.root)

        // Inicializa la interfaz de usuario (UI)
        initUI()
    }

    // Permite cambiar el contexto base para aplicar el idioma elegido por el usuario
    override fun attachBaseContext(newBase: Context) {
        val updatedContext = UserPreferences.applyLanguage(newBase)
        super.attachBaseContext(updatedContext)
    }

    // Método auxiliar para abrir un fragmento manualmente en el contenedor principal
    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.NavHostFragment, fragment)
        fragmentTransaction.commit()
    }

    // Inicializa elementos generales de la UI
    private fun initUI() {
        initNavigation()
    }

    // Configura la navegación con el NavController y la BottomNavigationView
    private fun initNavigation() {
        val navHost: NavHostFragment = supportFragmentManager.findFragmentById(R.id.NavHostFragment) as NavHostFragment
        navController = navHost.navController
        // Asocia la barra de navegación inferior con el controlador de navegación
        binding.buttonNavView.setupWithNavController(navController)
    }

    // Maneja la selección de elementos del NavigationView lateral (si lo usas)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.homeFragment -> openFragment(HomeFragment())
            // Puedes añadir más casos si tienes otros fragmentos
        }
        return true
    }
}