package com.example.linguaphile.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaphile.R
import com.example.linguaphile.adapters.SimpleVocabularyAdapter

class ShowIncorrectAnswerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_incorrect_answer, container, false)
        // Passed argument from MiniGame fragment using SafeArgs
        val args = ShowIncorrectAnswerFragmentArgs.fromBundle(requireArguments())
        // Retrieve the incorrect question list from the arguments using Parcelable Array
        val incorrectQuestionList = args.incorrectQuestions.toList() // Ensure non-null conversion
        // Set up RecyclerView.
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        Log.d("SIAFragment", "IncorrectQuestionList $incorrectQuestionList")
        // Bind data to simple vocabulary adapter class
        recyclerView.adapter = SimpleVocabularyAdapter().apply {
            submitList(incorrectQuestionList)
        }
        // Set up return button, handling return to previous fragment/view
        view.findViewById<View>(R.id.returnButton).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return view
    }
}
