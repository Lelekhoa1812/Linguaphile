package com.example.linguaphile.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linguaphile.databinding.FragmentMiniGameBinding
import com.example.linguaphile.viewmodels.VocabularyViewModel
import com.example.linguaphile.entities.Vocabulary
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import java.util.*

class MiniGameFragment : Fragment() {
    private var _binding: FragmentMiniGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var vocabularyViewModel: VocabularyViewModel
    // Handling test mode variables
    private var testMode: TestMode? = null
    private var questions: List<Vocabulary> = listOf()
    private var currentQuestionIndex = 0
    private var score = 0
    private var totalQuestions = 0

    // Enum class choices of Meaning and Synonym test
    private enum class TestMode {
        MEANING, SYNONYM
    }

    // Create view propagating the parameters
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMiniGameBinding.inflate(inflater, container, false)
        vocabularyViewModel = ViewModelProvider(this).get(VocabularyViewModel::class.java)
        Log.d("MiniGameFragment", "Binding initialized successfully")
        // Initially display the mode selection
        showModeSelection()
        return _binding!!.root // Use _binding directly if there's an issue with accessing `binding`
    }

    // Toggle layout upon user choice of mode, switch mode choice visibility
    private fun showModeSelection() {
        binding.modeSelectionLayout.visibility = View.VISIBLE
        binding.testLayout.visibility = View.GONE
        // Start meaning mode
        binding.meaningTestButton.setOnClickListener {
            testMode = TestMode.MEANING
            startTest()
        }
        // Start synonym mode
        binding.synonymTestButton.setOnClickListener {
            testMode = TestMode.SYNONYM
            startTest()
        }
    }

    // Start a test in either mode inheriting the corresponding usages
    private fun startTest() {
        binding.modeSelectionLayout.visibility = View.GONE
        binding.testLayout.visibility = View.VISIBLE
        // Initial values
        score = 0
        currentQuestionIndex = 0
        // Obtain data
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = calendar.time
        // Fetch vocabulary items from the last 7 days
        vocabularyViewModel.getVocabularyByDateRange(startDate, endDate).observe(viewLifecycleOwner) { vocabList ->
            val filteredVocabList = if (testMode == TestMode.SYNONYM) {
                // Exclude vocabularies without synonyms
                vocabList.filter { hasSynonym(it) }
            } else {
                vocabList
            }
            // Filter data upon usages
            questions = filteredVocabList.shuffled()
            totalQuestions = questions.size
            // Casing empty question list
            if (totalQuestions == 0) {
                // No questions available
                AlertDialog.Builder(requireContext())
                    .setTitle("No Vocabulary")
                    .setMessage("No vocabulary items available for testing in the selected mode.")
                    .setPositiveButton("OK") { _, _ ->
                        showModeSelection()
                    }
                    .setCancelable(false)
                    .show()
            } else { // Valid list
                showNextQuestion()
            }
        }
    }

    // Shows question each time and move to the next one
    private fun showNextQuestion() {
        if (currentQuestionIndex >= totalQuestions) {
            // Test finished
            showResultDialog()
            return
        }
        // Current index of question with the content
        val currentVocab = questions[currentQuestionIndex]
        binding.questionTextView.text = currentVocab.name
        // Initialise the correct and list of option (incorrect choices) from the vocab obtained
        val correctAnswer: String
        val options = mutableListOf<String>()
        // Set parameter for the 2 modes
        if (testMode == TestMode.MEANING) {
            correctAnswer = getAnyMeaning(currentVocab)
            options.add(correctAnswer)
            options.addAll(getRandomMeanings(3, currentVocab))
            binding.testTitleTextView.text = "Meaning Test"
            binding.instructionTextView.text = "Select the answer that best defines the word below:"
        } else {
            correctAnswer = getAnySynonym(currentVocab)
            options.add(correctAnswer)
            options.addAll(getRandomSynonyms(3, currentVocab))
            binding.testTitleTextView.text = "Synonym Test"
            binding.instructionTextView.text = "Select the answer that best matches the word below:"
        }
        // Ensure options list has exactly 4 elements (answer choices)
        if (options.size < 4) {
            // Fill remaining options with placeholders or duplicates if needed
            options.addAll(generateFallbackOptions(4 - options.size))
        }
        // Shuffle list of question so user can freshly restart
        options.shuffle()
        // Set options to buttons (on array position)
        binding.optionButton1.text = options[0]
        binding.optionButton2.text = options[1]
        binding.optionButton3.text = options[2]
        binding.optionButton4.text = options[3]
        // Set click listeners
        val optionButtons = listOf(binding.optionButton1, binding.optionButton2, binding.optionButton3, binding.optionButton4)
        optionButtons.forEach { button ->
            button.setOnClickListener {
                if (button.text == correctAnswer) {
                    score++
                }
                currentQuestionIndex++
                showNextQuestion()
            }
        }
    }

    // Fall back with incrementing count per each question
    private fun generateFallbackOptions(count: Int): List<String> {
        // Generate placeholder or duplicate options to ensure the list size is 4
        val fallbackOptions = mutableListOf<String>()
        for (i in 1..count) {
            fallbackOptions.add("Option $i")
        }
        return fallbackOptions
    }

    // If this or that vocab item has any meaning (1 to 4, any)
    private fun getAnyMeaning(vocab: Vocabulary): String {
        return listOfNotNull(vocab.meaning1, vocab.meaning2, vocab.meaning3, vocab.meaning4).first()
    }

    // If this vocab item has more than 1 meaning (1 to 4, any)
    private fun hasSynonym(vocab: Vocabulary): Boolean {
        return listOfNotNull(vocab.synonym1, vocab.synonym2, vocab.synonym3, vocab.synonym4).isNotEmpty()
    }

    // If this or that vocab item has any synonym (1 to 4, each)
    private fun getAnySynonym(vocab: Vocabulary): String {
        return listOfNotNull(vocab.synonym1, vocab.synonym2, vocab.synonym3, vocab.synonym4).first()
    }

    // Set any random meaning from other item rather than this one
    private fun getRandomMeanings(count: Int, excludeVocab: Vocabulary): List<String> {
        val allMeanings = mutableListOf<String>()
        // Fetch all meanings from vocabularies excluding the current one
        vocabularyViewModel.allVocabulary.value?.filter { it.id != excludeVocab.id }?.forEach { vocab ->
            allMeanings.addAll(listOfNotNull(vocab.meaning1, vocab.meaning2, vocab.meaning3, vocab.meaning4))
        }
        allMeanings.shuffle()
        return allMeanings.take(count)
    }

    // Set any random synonym from other item rather than this one
    private fun getRandomSynonyms(count: Int, excludeVocab: Vocabulary): List<String> {
        val allSynonyms = mutableListOf<String>()
        vocabularyViewModel.allVocabulary.value?.filter { it.id != excludeVocab.id }?.forEach { vocab ->
            allSynonyms.addAll(listOfNotNull(vocab.synonym1, vocab.synonym2, vocab.synonym3, vocab.synonym4))
        }
        allSynonyms.shuffle()
        return allSynonyms.take(count)
    }

    // Show result message and their corresponding feednback
    private fun showResultDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Test Finished")
            .setMessage("You scored $score/$totalQuestions")
            .setPositiveButton("Try Again") { _, _ ->
                startTest()
            }
            .setNegativeButton("Quit") { _, _ ->
                showModeSelection()
            }
            .setCancelable(false)
            .show()
    }

    // Destroy afterwards
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
