package com.example.linguaphile.fragments

import MiniGameViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linguaphile.databinding.FragmentMiniGameBinding
import com.example.linguaphile.viewmodels.VocabularyViewModel
import com.example.linguaphile.entities.Vocabulary
import androidx.appcompat.app.AlertDialog
import com.example.linguaphile.R
import com.example.linguaphile.databases.MiniGameDatabase
import com.example.linguaphile.entities.MiniGame
import com.example.linguaphile.repositories.MiniGameRepository
import com.example.linguaphile.viewmodels.MiniGameViewModelFactory
import java.util.*

class MiniGameFragment : Fragment() {
    // Bind ViewModel
    private var _binding: FragmentMiniGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var vocabularyViewModel: VocabularyViewModel
    private lateinit var minigameViewModel: MiniGameViewModel

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
        // Initialize vocabularyViewModel
        vocabularyViewModel = ViewModelProvider(this)[VocabularyViewModel::class.java]
        // Initialize minigameViewModel
        val repository = MiniGameRepository(MiniGameDatabase.getInstance(requireContext()).miniGameDao())
        minigameViewModel = ViewModelProvider(this, MiniGameViewModelFactory.Factory(repository))[MiniGameViewModel::class.java]
        Log.d("MiniGameFragment", "Binding initialized successfully") // Logs
        // Initially display the mode selection
        showModeSelection()
        return _binding!!.root // Use _binding directly if there's an issue with accessing `binding`
    }

    // Toggle layout upon user choice of mode, switch mode choice visibility
    private fun showModeSelection() {
        binding.modeSelectionLayout.visibility = View.VISIBLE
        binding.testLayout.visibility = View.GONE
        // Start meaning mode (1)
        binding.meaningTestButton.setOnClickListener {
            testMode = TestMode.MEANING
            startTest()
        }
        // Start synonym mode (2)
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
        // Check if allVocabulary has been loaded (for debug)
        vocabularyViewModel.allVocabulary.observe(viewLifecycleOwner) { allVocabularyList ->
            if (allVocabularyList.isNullOrEmpty()) {
                Log.d("MiniGameFragment", "All vocabulary list is empty. Cannot proceed.")
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.mgFragmentNullTitle)
                    .setMessage(R.string.mgFragmentNullDesc)
                    .setPositiveButton("OK") { _, _ -> // Set dialog with button casing null data fetched allowing user to return without catching error
                        showModeSelection()
                    }
                    .setCancelable(false)
                    .show()
                return@observe
            }
            // Set desired time range for testing
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, -30) // Test for this month (30 days)
            val startDate = calendar.time
            // Fetch vocabulary items from the last 30 days
            vocabularyViewModel.getVocabularyByDateRange(startDate, endDate)
                .observe(viewLifecycleOwner) { vocabList ->
                    // Sort out vocab item without synonym from the list in Synonym test mode
                    val filteredVocabList = if (testMode == TestMode.SYNONYM) {
                        vocabList.filter { hasSynonym(it) }
                    } else {
                        vocabList
                    }
                    // Shuffle the question order
                    questions = filteredVocabList.shuffled()
                    totalQuestions = questions.size
                    // Case no question available (in this mode), reflect message as an alert dialog
                    if (totalQuestions == 0) {
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.mgFragmentNullTitle)
                            .setMessage(R.string.mgFragmentModeNullDesc)
                            .setPositiveButton("OK") { _, _ ->
                                showModeSelection()
                            }
                            .setCancelable(false)
                            .show()
                    } else { // If valid
                        showNextQuestion()
                    }
                }
        }
    }

    // Shows question each time and move to the next one
    @SuppressLint("SetTextI18n")
    private fun showNextQuestion() {
        if (currentQuestionIndex >= totalQuestions) {
            showResultDialog() // Test finished
            return
        }
        // Current index of question with the content
        val currentVocab = questions[currentQuestionIndex]
        binding.questionTextView.text = currentVocab.name
        // Initialise the correct and list of options (incorrect choices) from the vocab obtained
        val correctAnswer: String
        val options = mutableListOf<String>()
        // Set parameter for the 2 modes
        if (testMode == TestMode.MEANING) {
            correctAnswer = getAnyMeaning(currentVocab) // Get a meaning from any of the chosen vocab
            options.add(correctAnswer)
            options.addAll(getRandomMeanings(3, currentVocab))
            binding.testTitleTextView.text = R.string.mgFragmentMeaningSet.toString()
            binding.instructionTextView.text = R.string.mgFragmentMeaningSetDesc.toString()
        } else {
            correctAnswer = getAnySynonym(currentVocab) // Get a synonym from any of the chosen vocab
            options.add(correctAnswer)
            options.addAll(getRandomSynonyms(3, currentVocab))
            binding.testTitleTextView.text = R.string.mgFragmentSynonymSet.toString()
            binding.instructionTextView.text = R.string.mgFragmentSynonymSetDesc.toString()
        }
        // Identify the binding item
        Log.d("MiniGameFragment", "Correct answer: $correctAnswer") // Logs
        Log.d("MiniGameFragment", "Incorrect options: ${options.drop(1)}") // Logs
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
                if (button.text == correctAnswer) { // Check if the answer matches
                    score++ // Increment score
                }
                currentQuestionIndex++ // Move to next question (until exhaust)
                showNextQuestion() // Iterate the loop again
            }
        }
    }

    // Fallback filling remaining options with placeholders or duplicates if needed
    private fun generateFallbackOptions(count: Int): List<String> {
        val placeholders = List(count) { index -> "Option ${index + 1}" }
        Log.d("MiniGameFragment", "Fallback options added: $placeholders") // Logs
        return placeholders
    }

    // If this or that vocab item has any meaning (1 to 4, any)
    private fun getAnyMeaning(vocab: Vocabulary): String {
        val meanings = listOfNotNull(vocab.meaning1, vocab.meaning2, vocab.meaning3, vocab.meaning4)
        return meanings.random() // Randomly select one meaning if available
    }

    // If this or that vocab item has any synonym (1 to 4, each)
    private fun getAnySynonym(vocab: Vocabulary): String {
        val synonyms = listOfNotNull(vocab.synonym1, vocab.synonym2, vocab.synonym3, vocab.synonym4)
        return synonyms.random() // Randomly select one synonym if available
    }

    // Exclude vocabularies without synonyms
    private fun hasSynonym(vocab: Vocabulary): Boolean {
        return listOfNotNull(vocab.synonym1, vocab.synonym2, vocab.synonym3, vocab.synonym4).isNotEmpty()
    }

    // Set any random meaning from other item rather than this one
    private fun getRandomMeanings(count: Int, excludeVocab: Vocabulary): List<String> {
        val allMeanings = vocabularyViewModel.allVocabulary.value
            ?.filter { it.id != excludeVocab.id }
            ?.flatMap { listOfNotNull(it.meaning1, it.meaning2, it.meaning3, it.meaning4) }
            ?.shuffled() ?: emptyList()
        Log.d("MiniGameFragment", "Generated random meanings: $allMeanings")
        return allMeanings.take(count)
    }

    // Set any random synonym from other item rather than this one
    private fun getRandomSynonyms(count: Int, excludeVocab: Vocabulary): List<String> {
        val allSynonyms = vocabularyViewModel.allVocabulary.value
            ?.filter { it.id != excludeVocab.id }
            ?.flatMap { listOfNotNull(it.synonym1, it.synonym2, it.synonym3, it.synonym4) }
            ?.shuffled() ?: emptyList()
        Log.d("MiniGameFragment", "Generated random synonyms: $allSynonyms")
        return allSynonyms.take(count)
    }

    // Show result message dialog and their corresponding feedback for user score, with option to try again (start the test again) or quit (go back to mode selection state)
    private fun showResultDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.mgFragmentFinishTitle)
            .setMessage("${R.string.mgFragmentFinishDesc} $score/$totalQuestions")
            // Try again start this mode again
            .setPositiveButton(R.string.mgFragmentTryAgain) { _, _ ->
                startTest()
            } // Exit return with mode selection state
            .setNegativeButton(R.string.mgFragmentExit) { _, _ ->
                showModeSelection()
            }
            .setCancelable(false)
            .show()
        // Check if the mini-game is completed with a perfect score
        if (score == totalQuestions) {
            val miniGame = MiniGame(
                completed = true,
                score = score,
                totalQuestions = totalQuestions
            )
            minigameViewModel.insertMiniGame(miniGame) // Add this aced mini game with details for incrementation
            Log.d("MiniGameFragment", "Mini-game completed with a perfect score.")
        }
    }

    // Destroy afterwards
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
