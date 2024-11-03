package com.example.linguaphile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linguaphile.R
import com.example.linguaphile.databinding.FragmentAddVocabularyBinding
import com.example.linguaphile.entities.Vocabulary
import com.example.linguaphile.viewmodels.VocabularyViewModel
import java.util.*

class AddVocabularyFragment : Fragment() {

    private lateinit var binding: FragmentAddVocabularyBinding
    private lateinit var vocabularyViewModel: VocabularyViewModel
    private val meanings = mutableListOf<EditText>()
    private val synonyms = mutableListOf<EditText>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddVocabularyBinding.inflate(inflater, container, false)
        vocabularyViewModel = ViewModelProvider(this).get(VocabularyViewModel::class.java)

        // Initialize the first meaning field as mandatory
        meanings.add(binding.meaning1EditText)

        binding.addMeaningButton.setOnClickListener {
            if (meanings.size < 4) {
                val meaningEditText = EditText(requireContext()).apply {
                    hint = "Meaning ${meanings.size + 1}"
                }
                binding.meaningLayout.addView(meaningEditText)
                meanings.add(meaningEditText)
            } else {
                Toast.makeText(context, "Maximum 4 meanings allowed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addSynonymButton.setOnClickListener {
            if (synonyms.size < 4) {
                val synonymEditText = EditText(requireContext()).apply {
                    hint = "Synonym ${synonyms.size + 1}"
                }
                binding.synonymLayout.addView(synonymEditText)
                synonyms.add(synonymEditText)
            } else {
                Toast.makeText(context, "Maximum 4 synonyms allowed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addVocabularyButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val type = binding.typeSpinner.selectedItem.toString()
            val date = Date()

            if (name.isNotBlank() && type.isNotBlank() && meanings[0].text.isNotBlank()) {
                val vocabulary = Vocabulary(
                    name = name,
                    type = type,
                    date = date,
                    meaning1 = meanings[0].text.toString(),
                    meaning2 = meanings.getOrNull(1)?.text?.toString()?.takeIf { it.isNotBlank() },
                    meaning3 = meanings.getOrNull(2)?.text?.toString()?.takeIf { it.isNotBlank() },
                    meaning4 = meanings.getOrNull(3)?.text?.toString()?.takeIf { it.isNotBlank() },
                    synonym1 = synonyms.getOrNull(0)?.text?.toString()?.takeIf { it.isNotBlank() },
                    synonym2 = synonyms.getOrNull(1)?.text?.toString()?.takeIf { it.isNotBlank() },
                    synonym3 = synonyms.getOrNull(2)?.text?.toString()?.takeIf { it.isNotBlank() },
                    synonym4 = synonyms.getOrNull(3)?.text?.toString()?.takeIf { it.isNotBlank() }
                )

                vocabularyViewModel.insert(vocabulary)
                Toast.makeText(context, "Vocabulary added successfully", Toast.LENGTH_SHORT).show()
                // Navigate back to the Home fragment or clear the form as required
            } else {
                Toast.makeText(context, "Please fill in required fields", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
