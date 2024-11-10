package com.example.linguaphile

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.linguaphile.databases.UserDatabase
import com.example.linguaphile.databinding.ActivityMainBinding
import com.example.linguaphile.repositories.UserRepository
import com.example.linguaphile.viewmodels.UserViewModel
import com.example.linguaphile.viewmodels.UserViewModelFactory
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout and binding data
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel to bind navigation header in the sidebar
        val userDao = UserDatabase.getInstance(this).userDao()
        val repository = UserRepository(userDao)
        val factory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        // Get navigation header view
        val headerView = binding.navView.getHeaderView(0)
        // Find views in the header
        val headerImageView: ImageView = headerView.findViewById(R.id.imageView)
        val headerTitle: TextView = headerView.findViewById(R.id.nav_header_title)
        val headerSubtitle: TextView = headerView.findViewById(R.id.nav_header_subtitle)

        // Observe user data and update header content dynamically
        userViewModel.getUser().observe(this) { user ->
            if (user != null) {
                headerTitle.text = user.name
                headerSubtitle.text = user.email
                // Load profile picture if available, otherwise use default
                if (user.profilePicture != null && user.profilePicture.toIntOrNull() != null) {
                    val imageResId = user.profilePicture.toInt()
                    headerImageView.setImageResource(imageResId)
                    headerImageView.setBackgroundColor(getBackgroundColorForImage(imageResId))
                } else {
                    headerImageView.setImageResource(R.drawable.user)
                    headerImageView.setBackgroundColor(resources.getColor(R.color.ivory, null))
                }
            }
        }

        // Support customized toolbar widgets and navigation action
        setSupportActionBar(binding.toolbar)
        // Initialise navigation
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Configure AppBar with navigation destinations for the drawer
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.addVocabularyFragment,
                R.id.miniGameFragment,
                R.id.profileFragment
            ), binding.drawerLayout
        )
        // Set up the navigation UI with the toolbar and drawer
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    // Navigate up (<-) from children fragments
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Function to get background color based on the drawable resource ID (class), dynamically update each time user update
    private fun getBackgroundColorForImage(imageResId: Int): Int {
        return when (imageResId) {
            // Regular class
            R.drawable.user, R.drawable.cat, R.drawable.dog, R.drawable.frog,
            R.drawable.jaguar, R.drawable.kangaroo, R.drawable.ox, R.drawable.rabbit -> {
                resources.getColor(R.color.ivory, null)
            }
            // Rare class
            R.drawable.bee1, R.drawable.elephant, R.drawable.monkey -> {
                resources.getColor(R.color.green, null)
            }
            // Epic class
            R.drawable.bee2, R.drawable.bear, R.drawable.dolphin -> {
                resources.getColor(R.color.blue, null)
            }
            // Legendary class
            R.drawable.bee3, R.drawable.lion, R.drawable.robot -> {
                resources.getColor(R.color.purple, null)
            }
            else -> resources.getColor(R.color.ivory, null) // Default to ivory if not matched
        }
    }
}
