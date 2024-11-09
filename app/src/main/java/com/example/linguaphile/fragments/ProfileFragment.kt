package com.example.linguaphile.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linguaphile.R
import com.example.linguaphile.databinding.FragmentProfileBinding
import com.example.linguaphile.entities.User
import com.example.linguaphile.viewmodels.UserViewModel
import com.example.linguaphile.repositories.UserRepository
import com.example.linguaphile.databases.UserDatabase
import com.example.linguaphile.viewmodels.UserViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        // Observe user data (casing null and default)
        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d("Profile Fragment", "User is not null") // Logs
                currentUser = user
                showUserDetails(user)
            } else if (user == null || user.name == "User Name") {
                Log.d("Profile Fragment", "User is null or default") // Logs
                showDefaultDetails()
            }
        }
        setupAvatarSelection() // Setup scroller view and enable data selection
        // Handle avatar selection
//        binding.avatarUser.setOnClickListener { selectAvatar(R.drawable.user) }
//        binding.avatarCat.setOnClickListener { selectAvatar(R.drawable.cat) }
//        binding.avatarDog.setOnClickListener { selectAvatar(R.drawable.dog) }
//        binding.avatarRabbit.setOnClickListener { selectAvatar(R.drawable.rabbit) }
//        binding.avatarFrog.setOnClickListener { selectAvatar(R.drawable.frog) }
//        binding.avatarElephant.setOnClickListener { selectAvatar(R.drawable.elephant) }
//        binding.avatarKangaroo.setOnClickListener { selectAvatar(R.drawable.kangaroo) }
//        binding.avatarOx.setOnClickListener { selectAvatar(R.drawable.ox) }
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
        val avatarIds = listOf(
            R.id.avatarUser, R.id.avatarCat, R.id.avatarDog,
            R.id.avatarFrog, R.id.avatarElephant, R.id.avatarKangaroo,
            R.id.avatarOx, R.id.avatarRabbit
        )
        avatarIds.forEach { id ->
            binding.root.findViewById<ImageView>(id).setOnClickListener {
                val drawableResource = when (id) {
                    R.id.avatarUser -> R.drawable.user
                    R.id.avatarCat -> R.drawable.cat
                    R.id.avatarDog -> R.drawable.dog
                    R.id.avatarFrog -> R.drawable.frog
                    R.id.avatarElephant -> R.drawable.elephant
                    R.id.avatarKangaroo -> R.drawable.kangaroo
                    R.id.avatarOx -> R.drawable.ox
                    R.id.avatarRabbit -> R.drawable.rabbit
                    else -> R.drawable.user // Default case
                }
                selectedImageResId = drawableResource // Choose the targeted image resource
                binding.selectedProfileImageView.setImageResource(drawableResource)
                binding.selectedProfileImageView.visibility = View.VISIBLE
                binding.avatarScrollView.visibility =
                    View.GONE // Hide scroller after selection            }
            }
        }
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

    // View mode with default details and hide edit mode
    private fun showUserDetails(user: User?) {
        binding.editModeLayout.visibility = View.GONE
        binding.viewModeLayout.visibility = View.VISIBLE
        // Parse the profilePicture string path or use default resource URI
        val imagePath = user?.profilePicture
        // Assert image path not null then set up view with corresponding animal icon user selected
        val imageRes = user?.profilePicture?.toIntOrNull() ?: R.drawable.user
        binding.profileImageView.setImageResource(imageRes)
        binding.profileImageView.setBackgroundColor(resources.getColor(R.color.ivory, null))
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
            //profilePicture = selectedImageUri?.toString() ?: currentUser?.profilePicture // Save the picture uri
            //profilePicture = selectedImageUri?.path // Save the path instead of the URI
            //profilePicture = selectedImageUri?.path ?: currentUser?.profilePicture // Save the path or use current profile picture
            profilePicture = selectedImageResId.toString() // Set image resource id (string)
        )
        // Update with ViewModel
        userViewModel.updateUser(updatedUser)
        Toast.makeText(context, "Details updated successfully", Toast.LENGTH_SHORT).show()
        //Log.d("ProfileFragment", "Picture path now: ${updatedUser.profilePicture}") // Logs
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
