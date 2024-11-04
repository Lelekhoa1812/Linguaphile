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
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: VocabularyAdapter
    private lateinit var vocabularyViewModel: VocabularyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        vocabularyViewModel = ViewModelProvider(this).get(VocabularyViewModel::class.java)
        // Set up RecyclerView with VocabularyAdapter
        adapter = VocabularyAdapter(
            // Navigate to update fragment while passed the id parameter as an arguement
            onUpdate = { vocabulary ->
                val action = HomeFragmentDirections.actionHomeFragmentToUpdateVocabularyFragment(vocabulary.id)
                findNavController().navigate(action)
            },
            // Delete item with SnackBar widget and UNDO option enabling retrieval
            onDelete = { vocabulary ->
                vocabularyViewModel.delete(vocabulary)
                Snackbar.make(binding.root, "Vocabulary deleted", Snackbar.LENGTH_LONG)
                    .setTextColor(requireContext().getColor(R.color.white))
                    .setActionTextColor(requireContext().getColor(R.color.white))
                    .setAction("UNDO") {
                        vocabularyViewModel.insert(vocabulary)
                    }.show()
            }
        )
        // Bind rv to adapter
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // In onCreateView, inside the LiveData observer for allVocabulary
        // Set an initial empty list only once
        adapter.submitList(emptyList())

        // Observe vocabulary only once and handle multiple triggers
        vocabularyViewModel.allVocabulary.observe(viewLifecycleOwner) { vocabularyList ->
            Log.d("HomeFragment", "Observer triggered with ${vocabularyList.size} items.")
            vocabularyList.forEachIndexed { index, vocab ->
                Log.d("HomeFragment", "Vocabulary item $index: ${vocab.name}")
            }
            if (vocabularyList.isNotEmpty()) {
                adapter.submitList(vocabularyList) // Only set if non-empty to avoid clearing
            }
        }

        // Set up search bar to filter vocabulary by name and meanings
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                Log.d("HomeFragment", "query: $query")
                if (query.isEmpty()) {
                    // Display all vocabulary items if search bar is empty
                    vocabularyViewModel.allVocabulary.value?.let { adapter.submitList(it) }
                } else {
                    filterVocabulary(query)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Date Filter Spinner Options
        val dateFilterOptions = listOf("All", "Today", "This Week", "This Month", "This Year")
        val dateFilterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dateFilterOptions)
        dateFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterDateSpinner.adapter = dateFilterAdapter

        // Date Filter Spinner Selection
        binding.filterDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterVocabularyByDate(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Type Filter Spinner Options
        val typeFilterOptions = listOf("All", "Noun", "Verb", "Adjective", "Adverb")
        val typeFilterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeFilterOptions)
        typeFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterTypeSpinner.adapter = typeFilterAdapter

        // Type Filter Spinner Selection
        binding.filterTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterVocabularyByType(parent.getItemAtPosition(position).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        return binding.root
    }

    // Filter vocabulary based on search bar input
    private fun filterVocabulary(query: String) {
        val queryLower = query.lowercase()//.replace(" ", "") // Uncomment for usage, meant to remove any empty string
        val filteredList = vocabularyViewModel.allVocabulary.value?.filter { vocabulary ->
            vocabulary.name.lowercase().contains(queryLower) ||
                    listOfNotNull(
                        vocabulary.meaning1, vocabulary.meaning2,
                        vocabulary.meaning3, vocabulary.meaning4
                    ).any { it.lowercase().contains(queryLower) }
        }
        adapter.submitList(filteredList)
        adapter.notifyDataSetChanged() // Force refresh after filtering
    }

    // Filter vocabulary by selected date range
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

    // Filter vocabulary by selected type
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
