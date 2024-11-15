package com.example.linguaphile.fragments

import android.os.Bundle
import android.util.Log
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
import androidx.navigation.fragment.navArgs
import com.example.linguaphile.R
import com.example.linguaphile.databinding.FragmentUpdateVocabularyBinding
import com.example.linguaphile.entities.Vocabulary
import com.example.linguaphile.viewmodels.VocabularyViewModel
import java.util.*

class UpdateVocabularyFragment : Fragment() {
    // Setups parameters
    private var _binding: FragmentUpdateVocabularyBinding? = null
    private val binding get() = _binding!!
    private lateinit var vocabularyViewModel: VocabularyViewModel
    private val args: UpdateVocabularyFragmentArgs by navArgs()
    private val meanings = mutableListOf<EditText>()
    private val synonyms = mutableListOf<EditText>()
    private var isNoteAdded = false // Toggle between state whether the note layout has been opened
    private var noteEditText: EditText? = null // Reference to the dynamically added note EditText

    // Init view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateVocabularyBinding.inflate(inflater, container, false)
        // Initialize ViewModel
        vocabularyViewModel = ViewModelProvider(this)[VocabularyViewModel::class.java]
        // Load vocabulary data
        vocabularyViewModel.getVocabularyById(args.vocabularyId).observe(viewLifecycleOwner) { vocabulary ->
            vocabulary?.let { bindVocabularyData(it) }
            // Check if there is an existing note and display it
            if (!(vocabulary.note.isNullOrEmpty())) {
                Log.d("UpdateVocabularyFragment", "note: ${vocabulary.note}")
                addNoteField(vocabulary.note)
                isNoteAdded = true
                binding.addNoteButton.visibility = View.GONE // Hide the add button when a note exists
            } else {
                isNoteAdded = false
                binding.addNoteButton.visibility = View.VISIBLE // Show the add button when no note exists
            }
        }
        // Click listeners for adding new meanings and synonyms dynamically adjust with array size
        binding.addMeaningButton.setOnClickListener {
            if (meanings.size < 4) {
                addMeaningField()
            } else {
                Toast.makeText(context, "Maximum 4 meanings allowed", Toast.LENGTH_SHORT).show()
            }
        }
        binding.addSynonymButton.setOnClickListener {
            if (synonyms.size < 4) {
                addSynonymField()
            } else {
                Toast.makeText(context, "Maximum 4 synonyms allowed", Toast.LENGTH_SHORT).show()
            }
        }
        // Add dynamic note
        binding.addNoteButton.setOnClickListener {
            if (!isNoteAdded) {
                addNoteField()
                binding.addNoteButton.visibility = View.GONE // Hide the button after adding the note
                isNoteAdded = true
            } else {
                Toast.makeText(context, "Note can only be added once", Toast.LENGTH_SHORT).show()
            }
        }
        // Call update submission
        binding.updateVocabularyButton.setOnClickListener {
            updateVocabulary()
        }
        return binding.root
    }

    // Bind vocab items' data
    private fun bindVocabularyData(vocabulary: Vocabulary) {
        binding.nameEditText.setText(vocabulary.name)
        binding.typeSpinner.setSelection(getSpinnerIndex(vocabulary.type))
        meanings.clear()
        synonyms.clear()
        // Add primary meaning
        meanings.add(binding.meaning1EditText.apply {
            setText(vocabulary.meaning1)
        })
        // Add additional meanings
        listOfNotNull(vocabulary.meaning2, vocabulary.meaning3, vocabulary.meaning4).forEach {
            addMeaningField(it)
        }
        // Add additional synonyms
        listOfNotNull(vocabulary.synonym1, vocabulary.synonym2, vocabulary.synonym3, vocabulary.synonym4).forEach {
            addSynonymField(it)
        }
    }

    // Obtain spinner index for the type selection
    private fun getSpinnerIndex(type: String): Int {
        val types = resources.getStringArray(R.array.vocabulary_types)
        return types.indexOf(type).takeIf { it >= 0 } ?: 0
    }

    // Add a meaning dynamically with the ability to delete
    private fun addMeaningField(text: String = "") {
        // Initialise container for meaning
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        // Add new meaning view
        val newMeaning = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            hint = "Meaning ${meanings.size + 1}"
            setText(text)
        }
        // Delete button per each synonym container
        val deleteButton = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            // On click remove any views
            setOnClickListener {
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

    // Add a synonym dynamically with the ability to delete
    private fun addSynonymField(text: String = "") {
        // Initialise container for synonym
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        // Add new synonym view
        val newSynonym = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            hint = "Synonym ${synonyms.size + 1}"
            setText(text)
        }
        // Delete button per each synonym container
        val deleteButton = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            // On click remove any views
            setOnClickListener {
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

    // Method to add or bind a note dynamically
    private fun addNoteField(existingNote: String = "") {
        binding.noteLayout.visibility = View.VISIBLE
        // Create a container for the note EditText and delete button
        val noteContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        // Create the EditText for the note
        noteEditText = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            hint = "Any notation for this Vocabulary?"
            setText(existingNote) // Set existing note if present
        }
        // Create a delete button to remove the note
        val deleteButton = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                // Remove the note and reset state
                binding.noteLayout.removeAllViews()
                binding.noteLayout.visibility = View.GONE
                noteEditText = null // Set reference to null to ensure it's not used in submission
                isNoteAdded = false
                binding.addNoteButton.visibility = View.VISIBLE // Show the add button again
            }
        }
        // Add the EditText and delete button to the note container
        noteContainer.addView(noteEditText)
        noteContainer.addView(deleteButton)
        binding.noteLayout.addView(noteContainer)
    }


    // Update vocabulary in the database
    private fun updateVocabulary() {
        val name = binding.nameEditText.text.toString().trim()
        val type = binding.typeSpinner.selectedItem.toString().trim()
        val note = noteEditText?.text?.toString()?.trim() ?: ""
        // Name and Type is mandatory, reflect Toast feedback on user invalid attempt
        if (name.isBlank() || type.isBlank()) {
            Toast.makeText(context, "Name and Type are required", Toast.LENGTH_SHORT).show()
            return
        }
        // Primary meaning (1) can't be saved as null
        val primaryMeaning = meanings[0].text.toString().takeIf { it.isNotBlank() } ?: run {
            Toast.makeText(context, "Primary Meaning is required", Toast.LENGTH_SHORT).show()
            return
        }
        // Map additional meanings (2-4) and synonyms (1-4)
        val additionalMeanings = meanings.drop(1).mapNotNull { it.text.toString().takeIf { it.isNotBlank() } }
        val additionalSynonyms = synonyms.mapNotNull { it.text.toString().takeIf { it.isNotBlank() } }
        // Bind any available features (if not null)
        val vocabulary = Vocabulary(
            id = args.vocabularyId, // Keep existing ID
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
        vocabularyViewModel.update(vocabulary)
        Toast.makeText(context, "Vocabulary updated!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_updateVocabularyFragment_to_homeFragment)
    }

    // Update labels for meanings after adding or deleting
    private fun updateMeaningLabels() {
        meanings.forEachIndexed { index, editText ->
            editText.hint = "Meaning ${index + 1}"
        }
    }

    // Update labels for synonyms after adding or deleting
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
