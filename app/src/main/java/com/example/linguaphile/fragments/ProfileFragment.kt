package com.example.linguaphile.fragments

import MiniGameViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linguaphile.R
import com.example.linguaphile.databases.MiniGameDatabase
import com.example.linguaphile.databinding.FragmentProfileBinding
import com.example.linguaphile.entities.User
import com.example.linguaphile.viewmodels.UserViewModel
import com.example.linguaphile.repositories.UserRepository
import com.example.linguaphile.databases.UserDatabase
import com.example.linguaphile.entities.Vocabulary
import com.example.linguaphile.repositories.MiniGameRepository
import com.example.linguaphile.viewmodels.MiniGameViewModelFactory
import com.example.linguaphile.viewmodels.UserViewModelFactory
import com.example.linguaphile.viewmodels.VocabularyViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    // Bind User ViewModel
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private var currentUser: User? = null
    private var selectedImageResId: Int? = R.drawable.user // Variable for Image Resource id from drawable, with default avatar
    // Bind Vocabulary ViewModels
    private lateinit var vocabularyViewModel: VocabularyViewModel
    // MiniGame ViewModel
    private lateinit var minigameViewModel: MiniGameViewModel

    // Create Views
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        // DAO, repository, and factory setups
        val userDao = UserDatabase.getInstance(requireContext()).userDao()
        val repository = UserRepository(userDao)
        val factory = UserViewModelFactory(repository)
        val miniGameRepository = MiniGameRepository(MiniGameDatabase.getInstance(requireContext()).miniGameDao())
        // Initialize the VocabularyViewModel
        vocabularyViewModel = ViewModelProvider(this)[VocabularyViewModel::class.java]
        // Use factory to create userViewModel
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java] // Replace .get method call with proper java class indexing implementation
        // Use factory to create minigameViewModel
        minigameViewModel = ViewModelProvider(this, MiniGameViewModelFactory.Factory(miniGameRepository))[MiniGameViewModel::class.java]
        // Observe user data (casing null and default)
        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d("Profile Fragment", "User is not null") // Logs
                currentUser = user
                showUserDetails(user)
            } else {
                Log.d("Profile Fragment", "User is null or default") // Logs
                showDefaultDetails()
            }
        }
        vocabularyViewModel.allVocabulary.observe(viewLifecycleOwner) { vocabularyList ->
            minigameViewModel.getCompletedMiniGamesCount().observe(viewLifecycleOwner) { completedMiniGamesCount ->
                setupAvatarSelection(vocabularyList, completedMiniGamesCount)
            }
        }
        // Trigger edit mode on listener
        binding.updateDetailsButton.setOnClickListener { enterEditMode() }
        // Confirm and update user details on data
        binding.confirmButton.setOnClickListener { updateUserDetails() }
        // Cancel update
        binding.cancelButton.setOnClickListener { showUserDetails(currentUser) }
        // Toggle visibility of scroller
        binding.selectedProfileImageView.setOnClickListener { toggleAvatarScrollerVisibility() }
        return binding.root
    }

    // Implement the click handler for selecting images from the horizontal scroller, then bind data
    // When avatar not met the achievement condition, from parsed Vocabulary list, signify the locking status (show a lock icon (error), set fallback opacity, prevent clicking)
    private fun setupAvatarSelection(vocabularyList: List<Vocabulary>, completedMiniGames: Int) {
        val avatarResources = listOf(
            // regular
            R.drawable.user, R.drawable.cat, R.drawable.dog, R.drawable.frog,
            R.drawable.jaguar, R.drawable.kangaroo, R.drawable.ox, R.drawable.rabbit,
            // rare
            R.drawable.bee1, R.drawable.elephant, R.drawable.peacock1, R.drawable.eagle1, R.drawable.owl1, R.drawable.parrot1, R.drawable.monkey,
            // epic
            R.drawable.bee2, R.drawable.bear, R.drawable.dolphin,
            // legendary
            R.drawable.bee3, R.drawable.lion, R.drawable.peacock2, R.drawable.eagle2, R.drawable.owl2, R.drawable.parrot2, R.drawable.robot
        )
        // Trackers on achievement progression
        val totalVocab = vocabularyList.size
        val totalNouns = vocabularyList.count { it.type == "Noun" }
        val totalVerbs = vocabularyList.count { it.type == "Verb" }
        val totalAdjectives = vocabularyList.count { it.type == "Adjective" }
        val totalAdverbs = vocabularyList.count { it.type == "Adverb" }
        // Initialise minigameViewModel to obtain total aced game count
        minigameViewModel.getCompletedMiniGamesCount().observe(viewLifecycleOwner) { count ->
            // Log the value
            Log.d("MiniGameFragment", "Completed Mini Games Count: $count") // Logs
        }
        // Debug and log all variable in advance
        Log.d("ProfileFragment", totalVocab.toString())
        Log.d("ProfileFragment", totalNouns.toString())
        Log.d("ProfileFragment", totalVerbs.toString())
        Log.d("ProfileFragment", totalAdjectives.toString())
        Log.d("ProfileFragment", totalAdverbs.toString())

        // Clear existing views to prevent duplicates
        binding.avatarLinearLayout.removeAllViews()
        avatarResources.forEach { resId -> // Match image resources with resId on pick
            // Create a FrameLayout to hold the avatar and the lock overlay
            val frameLayout = FrameLayout(requireContext()).apply {
                // size of each image container and the margin surround it
                layoutParams = ViewGroup.MarginLayoutParams(180, 180).apply {
                    setMargins(8, 0, 8, 0)
                }
            }
            // ImageView holder with styling
            val imageView = ImageView(requireContext()).apply {
                setImageResource(resId) // Image source
                setBackgroundColor(getBackgroundColorForImage(resId)) // Set the background color for each, corresponding to their class
                setPadding(8, 8, 8, 8)
            }
            frameLayout.addView(imageView)
            // Check conditions for locking the avatar
            // Upper-classed avatars only unlocked only after passing corresponding achievement conditions
            val isLocked = when (resId) {
                R.drawable.bee1 -> totalVocab < 100
                R.drawable.bee2 -> totalVocab < 500
                R.drawable.bee3 -> totalVocab < 1000
                R.drawable.peacock1 -> totalNouns < 100
                R.drawable.peacock2 -> totalNouns < 500
                R.drawable.eagle1 -> totalVerbs < 100
                R.drawable.eagle2 -> totalVerbs < 500
                R.drawable.owl1 -> totalAdjectives < 100
                R.drawable.owl2 -> totalAdjectives < 500
                R.drawable.parrot1 -> totalAdverbs < 100
                R.drawable.parrot2 -> totalAdverbs < 500
                R.drawable.monkey -> completedMiniGames < 10
                R.drawable.dolphin -> completedMiniGames < 50
                R.drawable.robot -> completedMiniGames < 100
                else -> false
            }
            // Overlay lock icon if the avatar is locked
            if (isLocked) {
                // Transparent dark opacity overlay
                val darkOverlay = View(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(resources.getColor(R.color.black, null))
                    alpha = 0.5f // Adjust transparency level
                }
                frameLayout.addView(darkOverlay) // Add fallback opacity overlay to frame
                // Lock icon overlay setup
                // ERROR: Lock icon is not showing up
                val lockOverlay = ImageView(requireContext()).apply {
                    setImageResource(R.drawable.lock) // Set image
                    layoutParams = FrameLayout.LayoutParams(
                        resources.getDimensionPixelSize(R.dimen.lock_icon_size), // Set width to 10dp in pixels
                        resources.getDimensionPixelSize(R.dimen.lock_icon_size)  // Set height to 10dp in pixels
                    ).apply {
                        gravity = android.view.Gravity.CENTER
                    }
                    scaleType = ImageView.ScaleType.CENTER
                }
                frameLayout.addView(lockOverlay) // Add the lock icon on overlay
                // Reflect Toast on listener to locked item
                imageView.setOnClickListener {
                    Toast.makeText(context, "Avatar is locked, progress with your achievements to unlock.", Toast.LENGTH_SHORT).show()
                }
            } else { // This is regular images set without locking
                imageView.isClickable = true
                imageView.setOnClickListener {
                    selectAvatar(resId)
                }
            }
            // Add ImageView to the LinearLayout inside the HorizontalScrollView
            binding.avatarLinearLayout.addView(frameLayout)
        }
    }

    // Function to handle avatar selection, dynamically adjust visibility
    private fun selectAvatar(resId: Int) {
        selectedImageResId = resId
        binding.selectedProfileImageView.setImageResource(resId) // Set the avatar user chose at edit mode
        binding.selectedProfileImageView.setBackgroundColor(getBackgroundColorForImage(resId)) // Set background color directly on selection
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
            R.drawable.bee1, R.drawable.elephant, R.drawable.peacock1, R.drawable.eagle1, R.drawable.owl1, R.drawable.parrot1, R.drawable.monkey -> {
                resources.getColor(R.color.green, null) }
            // epic class
            R.drawable.bee2, R.drawable.bear, R.drawable.dolphin -> {
                resources.getColor(R.color.blue, null) }
            // legendary class
            R.drawable.bee3, R.drawable.lion, R.drawable.peacock2, R.drawable.eagle2, R.drawable.owl2, R.drawable.parrot2, R.drawable.robot -> {
                resources.getColor(R.color.purple, null) }
            else -> resources.getColor(R.color.ivory, null) // Default to ivory if not matched
        }
    }

    // View mode with default details and hide edit mode
    private fun showUserDetails(user: User?) {
        binding.editModeLayout.visibility = View.GONE
        binding.viewModeLayout.visibility = View.VISIBLE
        // Parse the profilePicture string path or use default resource URI (no longer used since we use imageRes already)
        //val imagePath = user?.profilePicture
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
