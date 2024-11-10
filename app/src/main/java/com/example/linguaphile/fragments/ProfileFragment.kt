package com.example.linguaphile.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linguaphile.R
import com.example.linguaphile.databinding.FragmentProfileBinding
import com.example.linguaphile.entities.User
import com.example.linguaphile.viewmodels.UserViewModel
import com.example.linguaphile.repositories.UserRepository
import com.example.linguaphile.databases.UserDatabase
import com.example.linguaphile.viewmodels.UserViewModelFactory

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private var currentUser: User? = null
    private var selectedImageResId: Int? = R.drawable.user // Variable for Image Resource id from drawable, with default avatar


    // Create Views
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val userDao = UserDatabase.getInstance(requireContext()).userDao()
        val repository = UserRepository(userDao)
        val factory = UserViewModelFactory(repository)
        // Use factory to create ViewModel
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java] // Replace .get method call with proper java class indexing implementation
        // Observe user data (casing null and default)
        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d("Profile Fragment", "User is not null") // Logs
                currentUser = user
                showUserDetails(user)
            } else if (user == null || user.name == "User Name") { // This check is redundant but just try to ensure the program works correctly
                Log.d("Profile Fragment", "User is null or default") // Logs
                showDefaultDetails()
            }
        }
        setupAvatarSelection() // Setup scroller view and enable data selection
        // Trigger edit mode on listener
        binding.updateDetailsButton.setOnClickListener { enterEditMode() }
        // Confirm and update user details on data
        binding.confirmButton.setOnClickListener { updateUserDetails() }
        // Cancel update
        binding.cancelButton.setOnClickListener { showUserDetails(currentUser) }
        // Toggle visibility of scroller
        binding.selectedProfileImageView.setOnClickListener {
            toggleAvatarScrollerVisibility()
        }
        return binding.root
    }

    // Implement the click handler for selecting images from the scroller, then bind data
    private fun setupAvatarSelection() {
        val avatarResources = listOf(
            // regular
            R.drawable.user, R.drawable.cat, R.drawable.dog, R.drawable.frog,
            R.drawable.jaguar, R.drawable.kangaroo, R.drawable.ox, R.drawable.rabbit,
            // rare
            R.drawable.bee1, R.drawable.elephant, R.drawable.monkey,
            // epic
            R.drawable.bee2, R.drawable.bear, R.drawable.dolphin,
            // legendary
            R.drawable.bee3, R.drawable.lion, R.drawable.robot
        )
        // Clear existing views to prevent duplicates
        binding.avatarLinearLayout.removeAllViews()
        avatarResources.forEach { resId -> // Match image resources with resId on pick
            val imageView = ImageView(requireContext()).apply {
                val layoutParams = ViewGroup.MarginLayoutParams(100, 100).apply {
                    setMargins(8, 0, 8, 0)
                }
                this.layoutParams = layoutParams
                setImageResource(resId)
                setPadding(8, 8, 8, 8)
                isClickable = true
                setOnClickListener {
                    selectAvatar(resId)
                }
            }
            // Add ImageView to the LinearLayout inside the HorizontalScrollView
            binding.avatarLinearLayout.addView(imageView)
        }
    }

    // Function to handle avatar selection, dynamically adjust visibility
    private fun selectAvatar(resId: Int) {
        selectedImageResId = resId
        binding.selectedProfileImageView.setImageResource(resId)
        binding.selectedProfileImageView.visibility = View.VISIBLE
        binding.avatarScrollView.visibility = View.GONE
    }


    // Enable and disable scroller visibility when interact UI elements
    private fun toggleAvatarScrollerVisibility() {
        if (binding.avatarScrollView.visibility == View.GONE) {
            binding.avatarScrollView.visibility = View.VISIBLE
            binding.selectedProfileImageView.visibility = View.GONE
        } else {
            binding.avatarScrollView.visibility = View.GONE
            binding.selectedProfileImageView.visibility = View.VISIBLE
        }
    }

    // Function to get background color based on the drawable resource ID, colors are set by their class (regular, rare, epic, legendary)
    private fun getBackgroundColorForImage(imageResId: Int): Int {
        return when (imageResId) {
            // regular class
            R.drawable.user, R.drawable.cat, R.drawable.dog, R.drawable.frog,
            R.drawable.jaguar, R.drawable.kangaroo, R.drawable.ox, R.drawable.rabbit -> {
                resources.getColor(R.color.ivory, null) }
            // rare class
            R.drawable.bee1, R.drawable.elephant, R.drawable.monkey -> {
                resources.getColor(R.color.green, null) }
            // epic class
            R.drawable.bee2, R.drawable.bear, R.drawable.dolphin -> {
                resources.getColor(R.color.blue, null) }
            // legendary class
            R.drawable.bee3, R.drawable.lion, R.drawable.robot -> {
                resources.getColor(R.color.purple, null) }
            else -> resources.getColor(R.color.ivory, null) // Default to ivory if not matched
        }
    }

    // View mode with default details and hide edit mode
    private fun showUserDetails(user: User?) {
        binding.editModeLayout.visibility = View.GONE
        binding.viewModeLayout.visibility = View.VISIBLE
        // Parse the profilePicture string path or use default resource URI
        val imagePath = user?.profilePicture
        // Assert image path not null then set up view with corresponding animal icon user selected
        val imageRes = user?.profilePicture?.toIntOrNull() ?: R.drawable.user
        binding.profileImageView.setImageResource(imageRes)
        binding.profileImageView.setBackgroundColor(getBackgroundColorForImage(imageRes)) // Set corresponding background color by class
        // Name and email TextViews
        binding.userNameTextView.text = user?.name ?: "" // "User Name"
        binding.emailTextView.text = user?.email ?: ""   // "user@example.com"
    }

    // Default details passing null user item back
    private fun showDefaultDetails() {
        showUserDetails(null)
    }

    // View mode with editing holders (EditText) and hide view mode
    private fun enterEditMode() {
        binding.viewModeLayout.visibility = View.GONE
        binding.editModeLayout.visibility = View.VISIBLE
        // Set the EditText fields with current user data
        binding.editUserName.setText(currentUser?.name ?: "")
        binding.editEmail.setText(currentUser?.email ?: "")
        val imageRes = currentUser?.profilePicture?.toIntOrNull() ?: R.drawable.user
        binding.selectedProfileImageView.setImageResource(imageRes)
        binding.selectedProfileImageView.setBackgroundColor(getBackgroundColorForImage(imageRes)) // Set background color by class
        binding.avatarScrollView.visibility = View.VISIBLE
        binding.selectedProfileImageView.visibility = View.GONE
    }

    // Update new detail entries and save
    private fun updateUserDetails() {
        val newName = binding.editUserName.text.toString().trim()
        val newEmail = binding.editEmail.text.toString().trim()
        // Notify if entries are not set
        if (newName.isBlank() || newEmail.isBlank()) {
            Toast.makeText(context, "Name and Email cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        // Setup updated user item
        val updatedUser = User(
            id = currentUser?.id ?: 1, // Always set to 1 if there is only one user record
            name = newName,
            email = newEmail,
            profilePicture = selectedImageResId.toString() // Set image resource id (string)
        )
        // Update with ViewModel
        userViewModel.updateUser(updatedUser)
        Toast.makeText(context, "Details updated successfully", Toast.LENGTH_SHORT).show()
        Log.d("ProfileFragment", "Picture path now: ${updatedUser.profilePicture}") // Logs
        currentUser = updatedUser // Update UI dynamically
        showUserDetails(updatedUser)
        // Re-fetch user data from ViewModel to trigger LiveData update
        // Previously, image shown after updating is not refreshed after updating since ImageView might cache the image and not reload the URI if it appears unchanged)
        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user != null) { // Active user
                currentUser = user
                // Force refresh ImageView to avoid caching issues
                binding.profileImageView.setImageDrawable(null) // Clear current image
                showUserDetails(user)
                binding.profileImageView.invalidate() // Use on the the ImageView to force re-drawing
                binding.profileImageView.requestLayout() // Ensure UI refreshes properly
            }
        }
        // Clear `selectedImageUri` after updating, hence, will not violating the next update
        selectedImageResId = null
    }

    // Destroy
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
