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
    private var selectedImageUri: Uri? = null

    // Save the image to the app's internal storage (e.g., res/drawable) with their Uri as string, return the path
    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            // Setup context of the stream (e.g., file, uri)
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            // File context
            val fileName = "profile_picture.png"
            val file = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(file)
            // Format bitmap context
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath // Absolute path to the image
        } catch (e: IOException) {
            Log.e("ProfileFragment", "Error saving image: ${e.message}")
            null
        }
    }

    // Pick image from external storage (e.g., gallery), assert result status and set context (file, path, view)
    private val pickImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Set data
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri = result.data?.data
            // Set path (uri)
            imageUri?.let {
                val imagePath = saveImageToInternalStorage(it)
                // Casing image path has been defined, change the app's component view visibility
                if (imagePath != null) {
                    binding.editProfileImageHolder.visibility = View.GONE // Hide the upload icon
                    binding.profileImageView.setImageURI(Uri.fromFile(File(imagePath))) // Load image from internal storage
                    binding.profileImageView.visibility = View.VISIBLE
                    // Parse the path
                    selectedImageUri = Uri.parse(imagePath) // Save the path to use in updateUserDetails()
                }
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
        // Parse the profilePicture string path or use default resource URI
        val imagePath = user?.profilePicture
        // Assert image path not null then set up view with image uri, else shows default upload image
        if (imagePath != null && File(imagePath).exists()) {
            binding.profileImageView.setImageURI(Uri.fromFile(File(imagePath)))
        } else {
            binding.profileImageView.setImageResource(R.drawable.user)
        }
        binding.profileImageView.visibility = View.VISIBLE
        binding.editProfileImageHolder.visibility = View.GONE // Hide in view mode
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
        // Name and email EditText widgets
        binding.editUserName.setText(currentUser?.name ?: "")
        binding.editEmail.setText(currentUser?.email ?: "")
        val imagePath = currentUser?.profilePicture
        if (imagePath != null && File(imagePath).exists()) {
            binding.profileImageView.setImageDrawable(null) // Clear previous drawable to force refresh
            binding.profileImageView.setImageURI(Uri.fromFile(File(imagePath)))
        } else {
            binding.profileImageView.setImageResource(R.drawable.user)
        }
        binding.profileImageView.invalidate() // Force view to refresh
        Log.d("ProfileFragment", "Displayed Image path in edit mode: $imagePath") // Logs
        binding.editProfileImageHolder.setImageResource(R.drawable.upload) // Set upload icon
        binding.editProfileImageHolder.visibility = View.VISIBLE // Show the upload icon
        // Bind and set selected image after chosen from gallery
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
            id = currentUser?.id ?: 1, // Always set to 1 if there is only one user record
            name = newName,
            email = newEmail,
            //profilePicture = selectedImageUri?.toString() ?: currentUser?.profilePicture // Save the picture uri
            //profilePicture = selectedImageUri?.path // Save the path instead of the URI
            profilePicture = selectedImageUri?.path ?: currentUser?.profilePicture // Save the path or use current profile picture
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
        selectedImageUri = null
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
