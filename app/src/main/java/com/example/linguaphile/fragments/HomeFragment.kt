package com.example.linguaphile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linguaphile.R
import com.example.linguaphile.adapters.VocabularyAdapter
import com.example.linguaphile.databinding.FragmentHomeBinding
import com.example.linguaphile.viewmodels.VocabularyViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: VocabularyAdapter
    private lateinit var vocabularyViewModel: VocabularyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize the ViewModel
        vocabularyViewModel = ViewModelProvider(this).get(VocabularyViewModel::class.java)

        // Set up RecyclerView with VocabularyAdapter
        adapter = VocabularyAdapter(
            // Navigate to update fragment with vocabulary details
            onUpdate = { vocabulary ->
            },
            // Delete the item with showing the UNDO SnackBar enabling user to retrieve
            onDelete = { vocabulary ->
                vocabularyViewModel.delete(vocabulary)
                Snackbar.make(binding.root, "Vocabulary deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        vocabularyViewModel.insert(vocabulary)
                    }.show()
            }
        )

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe all vocabulary and set up data in the adapter
        vocabularyViewModel.allVocabulary.observe(viewLifecycleOwner) { vocabularyList ->
            adapter.submitList(vocabularyList)
        }

        // Set up Date Filter Spinner
        binding.filterDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Update filter based on the selected date range
                filterVocabularyByDate(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Set up Type Filter Spinner
        binding.filterTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = parent.getItemAtPosition(position).toString()
                filterVocabularyByType(selectedType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return binding.root
    }

    // Filter vocab sorted out by timeframe
    private fun filterVocabularyByDate(position: Int) {
        val now = Calendar.getInstance()
        val startDate: Calendar = Calendar.getInstance()

        when (position) {
            1 -> startDate.add(Calendar.DAY_OF_YEAR, -1)       // Today
            2 -> startDate.add(Calendar.DAY_OF_YEAR, -7)       // This week
            3 -> startDate.add(Calendar.MONTH, -1)             // This month
            4 -> startDate.add(Calendar.YEAR, -1)              // This year
        }

        vocabularyViewModel.getVocabularyByDateRange(startDate.time, now.time)
            .observe(viewLifecycleOwner) { filteredList ->
                adapter.submitList(filteredList)
            }
    }

    // Filter vocab sorted out by their type
    private fun filterVocabularyByType(type: String) {
        if (type == "All") {
            vocabularyViewModel.allVocabulary.observe(viewLifecycleOwner) { vocabularyList ->
                adapter.submitList(vocabularyList)
            }
        } else {
            vocabularyViewModel.getVocabularyByType(type).observe(viewLifecycleOwner) { filteredList ->
                adapter.submitList(filteredList)
            }
        }
    }
}
