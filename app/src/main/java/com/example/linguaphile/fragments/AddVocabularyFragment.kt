package com.example.linguaphile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.linguaphile.R
import com.example.linguaphile.databinding.FragmentAddVocabularyBinding
import com.example.linguaphile.entities.Vocabulary
import com.example.linguaphile.viewmodels.VocabularyViewModel
import java.util.Date

class AddVocabularyFragment : Fragment() {
    // Setups parameters
    private var _binding: FragmentAddVocabularyBinding? = null
    private val binding get() = _binding!!
    private lateinit var vocabularyViewModel: VocabularyViewModel
    private val meanings = mutableListOf<EditText>()
    private val synonyms = mutableListOf<EditText>()
    private var isNoteAdded = false // Toggle between state whether the note layout has been openned
    private var noteEditText: EditText? = null // Reference to the dynamically added note EditText

    // Init view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddVocabularyBinding.inflate(inflater, container, false)
        // Initialize the ViewModel
        vocabularyViewModel = ViewModelProvider(this)[VocabularyViewModel::class.java]
        // Initial setup for the first/mandatory meaning (Meaning1)
        meanings.add(binding.meaning1EditText)
        // Add dynamic meanings
        binding.addMeaningButton.setOnClickListener {
            if (meanings.size < 4) {
                addMeaningField()
            } else {
                Toast.makeText(context, "Maximum 4 meanings allowed", Toast.LENGTH_SHORT).show()
            }
        }
        // Add dynamic synonyms
        binding.addSynonymButton.setOnClickListener {
            if (synonyms.size < 4) {
                addSynonymField()
            } else {
                Toast.makeText(context, "Maximum 4 synonyms allowed", Toast.LENGTH_SHORT).show()
            }
        }
        // Add dynamic note
        binding.addNoteButton.setOnClickListener {
            if (!isNoteAdded) { // State: no note has been added
                binding.noteLayout.visibility = View.VISIBLE
                // Create a new LinearLayout container for each dynamic meaning
                val noteContainer = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                }
                // Add note TextView
                val noteEditText = EditText(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    hint = "Any notation for this Vocabulary?"
                }
                // Create a delete button to hide the note layout
                val deleteButton = ImageButton(requireContext()).apply {
                    setImageResource(android.R.drawable.ic_delete) // Set the delete icon
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
//                    .apply { // Optional: Applying some unique stylings
//                        setMargins(8, 0, 0, 0) // Add margins for positioning
//                    }
//                    setPadding(16, 16, 16, 16) // Add padding for better touch area
                    setOnClickListener {
                        // Clear the note EditText and hide the note layout
                        binding.noteLayout.removeAllViews()
                        binding.noteLayout.visibility = View.GONE
                        isNoteAdded = false // Revert state
                    }
                }
                // Add note EditText widget and delete button to the container
                noteContainer.addView(noteEditText)
                noteContainer.addView(deleteButton)
                // Add the container to the note layout
                binding.noteLayout.addView(noteContainer)
                isNoteAdded = true
            } else { // Note already added, handle removal, deny adding any extra note on limitation of 1 allowed
                Toast.makeText(context, "Note can only being added once", Toast.LENGTH_SHORT).show()
            }
        }
        // Submit vocabulary and navigate back to HomeFragment
        binding.addVocabularyButton.setOnClickListener {
            submitVocabulary()
        }
        return binding.root
    }

    // Add new meaning with indentation
    private fun addMeaningField() {
        // Create a new LinearLayout container for each dynamic meaning
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        // Add additional meanings view
        val newMeaning = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            hint = "Meaning ${meanings.size + 1}"
        }
        // Delete button per each synonym container
        val deleteButton = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                // Remove this meaning from the layout and list
                binding.meaningLayout.removeView(container)
                meanings.remove(newMeaning)
                updateMeaningLabels()
            }
        }
        // Add View for extra components
        container.addView(newMeaning)
        container.addView(deleteButton)
        binding.meaningLayout.addView(container)
        meanings.add(newMeaning)
    }

    // Add new synonym with indentation
    private fun addSynonymField() {
        // Create a new LinearLayout container for each dynamic synonym
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        // Add additional synonyms view
        val newSynonym = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            hint = "Synonym ${synonyms.size + 1}"
        }
        // Delete button per each synonym container
        val deleteButton = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                // Remove this synonym from the layout and list
                binding.synonymLayout.removeView(container)
                synonyms.remove(newSynonym)
                updateSynonymLabels()
            }
        }
        // Add View for extra components
        container.addView(newSynonym)
        container.addView(deleteButton)
        binding.synonymLayout.addView(container)
        synonyms.add(newSynonym)
    }

    // Final Submission
    private fun submitVocabulary() {
        // Get the vocabulary name and type
        val name = binding.nameEditText.text.toString().trim()
        val type = binding.typeSpinner.selectedItem.toString().trim()
        val note = noteEditText?.text.toString().trim() // Get text from the dynamically created EditText

        // Validate required fields
        if (name.isBlank() || type.isBlank()) {
            Toast.makeText(context, "Name and Type are required", Toast.LENGTH_SHORT).show()
            return
        }
        // Validate primary meaning
        val primaryMeaning = meanings[0].text.toString().takeIf { it.isNotBlank() } ?: run {
            Toast.makeText(context, "Primary Meaning is required", Toast.LENGTH_SHORT).show()
            return
        }
        // Collect additional meanings and synonyms and drop indentation if one is removed
        val additionalMeanings = meanings.drop(1).map { it.text.toString().takeIf { it.isNotBlank() } }
        val additionalSynonyms = synonyms.map { it.text.toString().takeIf { it.isNotBlank() } }
        // Create a new Vocabulary object
        val vocabulary = Vocabulary(
            id = 0, // Room auto-generates this
            name = name,
            type = type,
            date = Date(),
            meaning1 = primaryMeaning,
            meaning2 = additionalMeanings.getOrNull(0),
            meaning3 = additionalMeanings.getOrNull(1),
            meaning4 = additionalMeanings.getOrNull(2),
            synonym1 = additionalSynonyms.getOrNull(0),
            synonym2 = additionalSynonyms.getOrNull(1),
            synonym3 = additionalSynonyms.getOrNull(2),
            synonym4 = additionalSynonyms.getOrNull(3),
            note = if (note.isNotBlank()) note else null // Only set note if it's not blank
        )
        // Insert vocabulary into the database
        vocabularyViewModel.insert(vocabulary)
        // Notify user
        Toast.makeText(context, "Vocabulary added!", Toast.LENGTH_SHORT).show()
        // Navigate back to HomeFragment if data are valid (just extra validation)
        if (name.isNotBlank() && type.isNotBlank() && meanings[0].text.toString().isNotBlank()) {
            findNavController().navigate(R.id.action_addVocabularyFragment_to_homeFragment)
        }
    }

    // Dynamically control the insert and deletion of new meaning data
    private fun updateMeaningLabels() {
        meanings.forEachIndexed { index, editText ->
            editText.hint = "Meaning ${index + 1}"
        }
    }

    // Dynamically control the insert and deletion of new synonym data
    private fun updateSynonymLabels() {
        synonyms.forEachIndexed { index, editText ->
            editText.hint = "Synonym ${index + 1}"
        }
    }

    // Destroy
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
