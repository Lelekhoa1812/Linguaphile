package com.example.linguaphile.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linguaphile.R
import com.example.linguaphile.adapters.AchievementAdapter
import com.example.linguaphile.databinding.FragmentMyAchievementBinding
import com.example.linguaphile.entities.Achievement
import com.example.linguaphile.entities.Vocabulary
import com.example.linguaphile.viewmodels.VocabularyViewModel

class MyAchievementFragment : Fragment() {
    private var _binding: FragmentMyAchievementBinding? = null
    // Bind ViewModel and initialise adapter
    private val binding get() = _binding!!
    private lateinit var vocabularyViewModel: VocabularyViewModel
    private lateinit var achievementAdapter: AchievementAdapter

    // Initialise Views
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAchievementBinding.inflate(inflater, container, false)
        // Setup RecyclerView
        binding.recyclerViewAchievements.layoutManager = LinearLayoutManager(requireContext())
        achievementAdapter = AchievementAdapter()
        binding.recyclerViewAchievements.adapter = achievementAdapter
        // Initialize ViewModel to observe data
        vocabularyViewModel = ViewModelProvider(this)[VocabularyViewModel::class.java]
        // Get all vocab item from ViewModel
        vocabularyViewModel.allVocabulary.observe(viewLifecycleOwner) { vocabularyList ->
            val achievements = generateAchievements(vocabularyList)
            achievementAdapter.submitList(achievements)
        }
        return binding.root
    }

    // Generate and assert achievement milestones on the list of achievements
    private fun generateAchievements(vocabularyList: List<Vocabulary>): List<Achievement> {
        val totalVocab = vocabularyList.size
        val totalNouns = vocabularyList.count { it.type == "Noun" }
        val totalVerbs = vocabularyList.count { it.type == "Verb" }
        val totalAdjectives = vocabularyList.count { it.type == "Adjective" }
        val totalAdverbs = vocabularyList.count { it.type == "Adverb" }

        // Track and log all variable in advance
        Log.d("MyAchievementFragment", totalVocab.toString())
        Log.d("MyAchievementFragment", totalNouns.toString())
        Log.d("MyAchievementFragment", totalVerbs.toString())
        Log.d("MyAchievementFragment", totalAdjectives.toString())
        Log.d("MyAchievementFragment", totalAdverbs.toString())

        // Track mini game completed and days logging in
//        val completedMiniGames = vocabularyViewModel.getCompletedMiniGamesCount()
//        val daysLoggedIn = vocabularyViewModel.getDaysLoggedIn()

        // List of achievement trackers with their requirements and set context (e.g., image resId)
        return listOf(
            // Enthusiast Achievements
//            Achievement(1, "Enthusiast I", "Logged in for 7 days", if (vocabularyViewModel.getDaysLoggedIn() >= 7) R.drawable.yes else R.drawable.no),
//            Achievement(2, "Enthusiast II", "Logged in for 30 days", if (vocabularyViewModel.getDaysLoggedIn() >= 30) R.drawable.yes else R.drawable.no),
//            Achievement(3, "Enthusiast III", "Logged in for 60 days", if (vocabularyViewModel.getDaysLoggedIn() >= 60) R.drawable.yes else R.drawable.no),
//            Achievement(4, "Enthusiast IV", "Logged in for 90 days", if (vocabularyViewModel.getDaysLoggedIn() >= 90) R.drawable.yes else R.drawable.no),

            // Studious Bee Achievements
            Achievement(5, "Studious Bee I", "Added 10 vocabs", null, if (totalVocab >= 10) R.drawable.yes else R.drawable.no),
            Achievement(6, "Studious Bee II", "Added 100 vocabs", R.drawable.bee1, if (totalVocab >= 100) R.drawable.yes else R.drawable.no),
            Achievement(7, "Studious Bee III", "Added 200 vocabs", null, if (totalVocab >= 200) R.drawable.yes else R.drawable.no),
            Achievement(8, "Studious Bee IV", "Added 500 vocabs", R.drawable.bee2, if (totalVocab >= 500) R.drawable.yes else R.drawable.no),
            Achievement(9, "Studious Bee V", "Added 1000 vocabs", R.drawable.bee3, if (totalVocab >= 1000) R.drawable.yes else R.drawable.no),

            // Noun Expert Achievements
            Achievement(10, "Noun Expert I", "Added 10 nouns", null, if (totalNouns >= 10) R.drawable.yes else R.drawable.no),
            Achievement(11, "Noun Expert II", "Added 50 nouns", null, if (totalNouns >= 50) R.drawable.yes else R.drawable.no),
            Achievement(12, "Noun Expert III", "Added 100 nouns", R.drawable.peacock1, if (totalNouns >= 100) R.drawable.yes else R.drawable.no),
            Achievement(13, "Noun Expert IV", "Added 200 nouns", null, if (totalNouns >= 200) R.drawable.yes else R.drawable.no),
            Achievement(14, "Noun Expert V", "Added 500 nouns", R.drawable.peacock2, if (totalNouns >= 500) R.drawable.yes else R.drawable.no),

            // Verb Expert Achievements
            Achievement(15, "Verb Expert I", "Added 10 verbs", null, if (totalVerbs >= 10) R.drawable.yes else R.drawable.no),
            Achievement(16, "Verb Expert II", "Added 50 verbs", null, if (totalVerbs >= 50) R.drawable.yes else R.drawable.no),
            Achievement(17, "Verb Expert III", "Added 100 verbs", R.drawable.eagle1, if (totalVerbs >= 100) R.drawable.yes else R.drawable.no),
            Achievement(18, "Verb Expert IV", "Added 200 verbs", null, if (totalVerbs >= 200) R.drawable.yes else R.drawable.no),
            Achievement(19, "Verb Expert V", "Added 500 verbs", R.drawable.eagle2, if (totalVerbs >= 500) R.drawable.yes else R.drawable.no),

            // Adjective Expert Achievements
            Achievement(20, "Adjective Expert I", "Added 10 adjectives", null, if (totalAdjectives >= 10) R.drawable.yes else R.drawable.no),
            Achievement(21, "Adjective Expert II", "Added 50 adjectives", null, if (totalAdjectives >= 50) R.drawable.yes else R.drawable.no),
            Achievement(22, "Adjective Expert III", "Added 100 adjectives", R.drawable.owl1, if (totalAdjectives >= 100) R.drawable.yes else R.drawable.no),
            Achievement(23, "Adjective Expert IV", "Added 200 adjectives", null, if (totalAdjectives >= 200) R.drawable.yes else R.drawable.no),
            Achievement(24, "Adjective Expert V", "Added 500 adjectives", R.drawable.owl2, if (totalAdjectives >= 500) R.drawable.yes else R.drawable.no),

            // Adverb Expert Achievements
            Achievement(25, "Adverb Expert I", "Added 10 adverbs", null, if (totalAdverbs >= 10) R.drawable.yes else R.drawable.no),
            Achievement(26, "Adverb Expert II", "Added 50 adverbs", null, if (totalAdverbs >= 50) R.drawable.yes else R.drawable.no),
            Achievement(27, "Adverb Expert III", "Added 100 adverbs", R.drawable.parrot1, if (totalAdverbs >= 100) R.drawable.yes else R.drawable.no),
            Achievement(28, "Adverb Expert IV", "Added 200 adverbs", null, if (totalAdverbs >= 200) R.drawable.yes else R.drawable.no),
            Achievement(29, "Adverb Expert V", "Added 500 adverbs", R.drawable.parrot2, if (totalAdverbs >= 500) R.drawable.yes else R.drawable.no),

            // Hunter Achievements
//            Achievement(30, "Hunter I", "Aced 1 mini game", if (completedMiniGames >= 1) R.drawable.yes else R.drawable.no),
//            Achievement(31, "Hunter II", "Aced 10 mini games", if (completedMiniGames >= 10) R.drawable.yes else R.drawable.no),
//            Achievement(32, "Hunter III", "Aced 20 mini games", if (completedMiniGames >= 20) R.drawable.yes else R.drawable.no),
//            Achievement(33, "Hunter IV", "Aced 50 mini games", if (completedMiniGames >= 50) R.drawable.yes else R.drawable.no),
//            Achievement(34, "Hunter V", "Aced 100 mini games", if (completedMiniGames >= 100) R.drawable.yes else R.drawable.no)
        )
    }
}
