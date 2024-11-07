package com.example.linguaphile.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

    // Update Profile picture
    private val pickImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            binding.editProfileImageHolder.setImageURI(selectedImageUri)
        }
    }

    // Create Views
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        // Instantiate UserRepository
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
        binding.updateDetailsButton.setOnClickListener {
            enterEditMode()
        }
        // Confirm and update user details on data
        binding.confirmButton.setOnClickListener {
            updateUserDetails()
        }
        // Or cancel return current data
        binding.cancelButton.setOnClickListener {
            showUserDetails(currentUser)
        }
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
        binding.editUserName.setText(currentUser?.name ?: "")
        binding.editEmail.setText(currentUser?.email ?: "")
        // Load current image or default
        val imageUri = currentUser?.profilePicture?.let { Uri.parse(it) }
            ?: Uri.parse("android.resource://${requireContext().packageName}/${R.drawable.user}")
        binding.editProfileImageHolder.setImageURI(imageUri)
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
        // Setup updated item
        val updatedUser = User(
            id = currentUser?.id ?: 0,
            name = newName,
            email = newEmail,
            profilePicture = selectedImageUri?.toString() ?: currentUser?.profilePicture
            ?: "android.resource://${requireContext().packageName}/${R.drawable.user}"
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
