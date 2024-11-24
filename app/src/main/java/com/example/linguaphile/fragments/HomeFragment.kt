package com.example.linguaphile.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linguaphile.R
import com.example.linguaphile.adapters.VocabularyAdapter
import com.example.linguaphile.databinding.FragmentHomeBinding
import com.example.linguaphile.viewmodels.VocabularyViewModel
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.fragment.findNavController
import com.example.linguaphile.entities.Vocabulary
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: VocabularyAdapter
    private lateinit var vocabularyViewModel: VocabularyViewModel
    // Track selected filter states
    private var selectedDateFilterPosition = 0
    private var selectedTypeFilter = "All"

    // Create Views
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        vocabularyViewModel = ViewModelProvider(this)[VocabularyViewModel::class.java]
        // Set up RecyclerView with VocabularyAdapter
        adapter = VocabularyAdapter(
            // Navigate to update fragment while passed the id parameter as an argument
            onUpdate = { vocabulary ->
                val action = HomeFragmentDirections.actionHomeFragmentToUpdateVocabularyFragment(vocabulary.id)
                findNavController().navigate(action)
            },
            // Delete item with SnackBar widget and UNDO option enabling retrieval
            onDelete = { vocabulary ->
                vocabularyViewModel.delete(vocabulary)
                // Filtering function will hide changes to the deleted item since they kept previous result on filter trigger
                // Hence, recall to dynamically update list with deletion
                applyCombinedFilter(vocabularyViewModel.allVocabulary.value)
                Snackbar.make(binding.root, R.string.homeFragmentVocabDeleted, Snackbar.LENGTH_LONG)
                    .setTextColor(requireContext().getColor(R.color.colorUNDOText))       // Set text colorway for title
                    .setActionTextColor(requireContext().getColor(R.color.colorUNDOText)) // Set text colorway for UNDO btn
                    .setAction("UNDO") { vocabularyViewModel.insert(vocabulary) }
                    .show()
            }
        )
        // Binding RecyclerView data to the adapter
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Observe all vocabulary items with listing enabled double filtering by both date and type
        vocabularyViewModel.allVocabulary.observe(viewLifecycleOwner) { vocabularyList ->
            applyCombinedFilter(vocabularyList) // Filtering function
        }

        // Set up search bar (TextWatcher) to filter vocabulary by name or any meanings
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            // Can enhance logic to adapt filter sorting nothing if the search work doesn't match any
            // Review previous commit history to see how this logics work
            // This version will stop sorting with all available item upon the latest available search
            override fun afterTextChanged(s: Editable?) {
                applyCombinedFilter(vocabularyViewModel.allVocabulary.value) // Filtering function parsing all vobab VM
            }
            // pre-init
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Date Filter Spinner Options
        val dateFilterOptions = listOf("All", "Today", "This Week", "This Month", "This Year")
        val dateFilterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dateFilterOptions)
        dateFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Bind with adapted entries
        binding.filterDateSpinner.adapter = dateFilterAdapter
        binding.filterDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Sort filtration on chosen option
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedDateFilterPosition = position
                applyCombinedFilter(vocabularyViewModel.allVocabulary.value)
            }
            // Show all if no option is selected
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Type Filter Spinner Options
        val typeFilterOptions = listOf("All", "Noun", "Verb", "Adjective", "Adverb")
        val typeFilterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeFilterOptions)
        typeFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Bind with adapted entries
        binding.filterTypeSpinner.adapter = typeFilterAdapter
        binding.filterTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Sort filtration on chosen option
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedTypeFilter = parent.getItemAtPosition(position).toString()
                applyCombinedFilter(vocabularyViewModel.allVocabulary.value)
            }
            // Show all if no option is selected
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        // Return root binding for all methods
        return binding.root
    }

    // Apply both date and type filters on the vocabulary list, including search query combined
    private fun applyCombinedFilter(vocabularyList: List<Vocabulary>?) {
        // Casing vocab item listed is null, return, this only shows the latest available listing and not explicitly show empty listing
        if (vocabularyList == null) return

        // Filter by search query (TextWatcher), if not matching any or empty, show all
        val query = binding.searchBar.text.toString().lowercase()
        val filteredByQuery = vocabularyList.filter { vocab ->
            // If the query matches any name/meanings (to lowercase), filtered them
            // Could integrate remove empty space here)
            vocab.name.lowercase().contains(query) ||
                    listOfNotNull(vocab.meaning1, vocab.meaning2, vocab.meaning3, vocab.meaning4)
                        .any { it.lowercase().contains(query) }
        }

        // Apply date filter on selection
        val now = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        when (selectedDateFilterPosition) {
            1 -> startDate.add(Calendar.DAY_OF_YEAR, -1) // Today (plus yesterday since today's input is incompletance)
            2 -> startDate.add(Calendar.DAY_OF_YEAR, -7) // This week
            3 -> startDate.add(Calendar.MONTH, -1)       // This month
            4 -> startDate.add(Calendar.YEAR, -1)        // This year
        }

        // Filter by date casing all -> filter by query, which can show all at empty, else if valid, append this and that time range
        val filteredByDate = if (selectedDateFilterPosition == 0) {
            filteredByQuery // No date filter
        } else {
            filteredByQuery.filter { it.date.after(startDate.time) && it.date.before(now.time) }
        }

        // Filter by type casing all -> filter by query, which can show all at empty, else if valid, append selected type
        val finalFilteredList = if (selectedTypeFilter == "All") {
            filteredByDate // No type filter
        } else {
            filteredByDate.filter { it.type == selectedTypeFilter }
        }

        // Submit final list after sorting to adapter
        adapter.submitList(finalFilteredList)
        Log.d("HomeFragment", "Final filtration $finalFilteredList")
    }

    // Destroy view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
