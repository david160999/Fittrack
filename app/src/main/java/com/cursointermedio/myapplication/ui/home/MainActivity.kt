package com.cursointermedio.myapplication.ui.home

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.data.database.local.UserPreferences
import com.cursointermedio.myapplication.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        UserPreferences.applyDarkMode(this)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(this)
        setContentView(binding.root)

        initUI()
    }

    override fun attachBaseContext(newBase: Context) {
        val updatedContext = UserPreferences.applyLanguage(newBase)
        super.attachBaseContext(updatedContext)
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.NavHostFragment, fragment)
        fragmentTransaction.commit()
    }

    private fun initUI() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost: NavHostFragment = supportFragmentManager.findFragmentById(R.id.NavHostFragment) as NavHostFragment
        navController = navHost.navController
        binding.buttonNavView.setupWithNavController(navController)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.homeFragment -> openFragment(HomeFragment())
        }
        return true
    }
}