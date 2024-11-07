package com.example.linguaphile.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private var currentUser: User? = null
    private var selectedImageUri: Uri? = null

    // Update Profile picture launcher
    private val pickImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                binding.editProfileImageHolder.visibility = View.GONE  // Hide the upload icon
                binding.profileImageView.setImageURI(selectedImageUri) // Show the selected image
                binding.profileImageView.visibility = View.VISIBLE
            }
        }
    }

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
        // Observe user data (casing null)
        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            currentUser = user
            if (user != null) {
                showUserDetails(user)
            } else {
                showDefaultDetails()
            }
        }
        // Trigger edit mode on listener
        binding.updateDetailsButton.setOnClickListener { enterEditMode() }
        // Confirm and update user details on data
        binding.confirmButton.setOnClickListener { updateUserDetails() }
        // Cancel update
        binding.cancelButton.setOnClickListener { showUserDetails(currentUser) }
        // Pick image on listener
        binding.editProfileImageHolder.setOnClickListener { pickImageFromGallery() }
        return binding.root
    }

    // View mode with default details and hide edit mode
    private fun showUserDetails(user: User?) {
        binding.editModeLayout.visibility = View.GONE
        binding.viewModeLayout.visibility = View.VISIBLE
        // Parse the profilePicture string to URI or use default resource URI
        val imageUri = user?.profilePicture?.let { Uri.parse(it) }
            ?: Uri.parse("android.resource://${requireContext().packageName}/${R.drawable.user}")
        binding.profileImageView.setImageURI(imageUri)
        binding.profileImageView.visibility = View.VISIBLE
        binding.editProfileImageHolder.visibility = View.GONE // Hide in view mode
        // Name and email TextViews
        binding.userNameTextView.text = user?.name ?: "User Name"
        binding.emailTextView.text = user?.email ?: "user@example.com"
    }

    // Default details passing null user item back
    private fun showDefaultDetails() {
        showUserDetails(null)
    }

    // View mode with editing holders (EditText) and hide view mode
    private fun enterEditMode() {
        binding.viewModeLayout.visibility = View.GONE
        binding.editModeLayout.visibility = View.VISIBLE
        // Name and email EditText widgets
        binding.editUserName.setText(currentUser?.name ?: "")
        binding.editEmail.setText(currentUser?.email ?: "")

        val imageUri = currentUser?.profilePicture?.let { Uri.parse(it) }
            ?: Uri.parse("android.resource://${requireContext().packageName}/${R.drawable.user}")
        Log.d("ProfileFragment", "Displayed Image URI in edit mode: $imageUri") // Logs
        binding.editProfileImageHolder.setImageResource(R.drawable.upload) // Set upload icon
        binding.editProfileImageHolder.visibility = View.VISIBLE // Show the upload icon
        // Bind and set selected image after chosen from gallery
        binding.profileImageView.setImageURI(imageUri)
        binding.profileImageView.visibility = View.VISIBLE // Show current or default profile image
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
            id = currentUser?.id ?: 1,
            name = newName,
            email = newEmail,
            profilePicture = selectedImageUri?.toString() ?: currentUser?.profilePicture
        )
        // Update with ViewModel
        userViewModel.updateUser(updatedUser)
        Toast.makeText(context, "Details updated successfully", Toast.LENGTH_SHORT).show()
        showUserDetails(updatedUser)
    }

    // Use intent to allow import image from gallery
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        pickImageResultLauncher.launch(intent)
    }

    // Destroy
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
