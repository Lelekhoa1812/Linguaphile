package com.example.linguaphile.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaphile.R
import com.example.linguaphile.entities.Achievement

class AchievementAdapter(
    private var achievementList: List<Achievement> = emptyList(), // Get listing (from scratch or) from submitList
    private var progressData: Map<Int, Int> = emptyMap() // Progress data map for each achievement by ID from submitList
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    // Initialise content for ViewHolder
    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val achievementName: TextView = itemView.findViewById(R.id.achievementName)
        val achievementDescription: TextView = itemView.findViewById(R.id.achievementDescription)
        val achievementUnlockImage: ImageView = itemView.findViewById(R.id.achievementUnlockImage) // Corresponding image
        val achievementUnlockFrame: ImageView = itemView.findViewById(R.id.achievementUnlockFrame) // Frame wrapped
        val achievementStatusImage: ImageView = itemView.findViewById(R.id.achievementStatusImage) // Yes or No (unlocked yet?)
        val achievementStatusThreshold: TextView = itemView.findViewById(R.id.statusThreshold)     // How far has the user progress / achieved merit
    }

    // Create ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.achievement_item, parent, false)
        return AchievementViewHolder(view)
    }

    // Function to get background color based on the drawable resource ID (classify avatars)
    private fun getBackgroundColorForImage(imageResId: Int, context: View): Int {
        return when (imageResId) {
            // regular class
            R.drawable.user, R.drawable.cat, R.drawable.dog, R.drawable.frog,
            R.drawable.jaguar, R.drawable.kangaroo, R.drawable.ox, R.drawable.rabbit -> {
                ContextCompat.getColor(context.context, R.color.ivory)
            }
            // rare class
            R.drawable.bee1, R.drawable.elephant, R.drawable.peacock1, R.drawable.eagle1,
            R.drawable.owl1, R.drawable.parrot1, R.drawable.monkey -> {
                ContextCompat.getColor(context.context, R.color.green)
            }
            // epic class
            R.drawable.bee2, R.drawable.bear, R.drawable.dolphin -> {
                ContextCompat.getColor(context.context, R.color.blue)
            }
            // legendary class
            R.drawable.bee3, R.drawable.lion, R.drawable.peacock2, R.drawable.eagle2,
            R.drawable.owl2, R.drawable.parrot2, R.drawable.robot -> {
                ContextCompat.getColor(context.context, R.color.purple)
            }
            else -> ContextCompat.getColor(context.context, R.color.ivory) // Default color if not matched
        }
    }

    // Bind ViewHolder with data
    @SuppressLint("SetTextI18n") // Set text content dynamically as TextView
    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievementList[position]
        holder.achievementName.text = achievement.name
        holder.achievementDescription.text = achievement.description
        // Set possible unlocked image on this achievement
        achievement.unlockImageResId?.let {
            holder.achievementUnlockImage.setImageResource(it) // Set image resource if null, skipped
            holder.achievementUnlockFrame.setImageResource(R.drawable.frame) // Set frame if there is the resource
            holder.achievementUnlockImage.setBackgroundColor(getBackgroundColorForImage(achievement.unlockImageResId, holder.achievementUnlockImage)) // Set background color on class
        }
        // Set image resource dynamically on achievement task status (yes or no drawable resource)
        holder.achievementStatusImage.setImageResource(achievement.status)
        // Map the current progress to bind statusThreshold TextView by the Achievement id (set null to error signifying nullable or data failure)
        val currentProgress = progressData[achievement.id] ?: "error"
        // Achievement id -> their maximum threshold mapped/ Get the current progress and the maximum threshold
        val maxProgress = when (achievement.id) {
            5 -> 10   // "Studious Bee I"
            6 -> 100  // "Studious Bee II"
            7 -> 200  // "Studious Bee III"
            8 -> 500  // "Studious Bee IV"
            9 -> 1000 // "Studious Bee V"
            10 -> 10  // "Noun Expert I"
            11 -> 50  // "Noun Expert II"
            12 -> 100 // "Noun Expert III"
            13 -> 200 // "Noun Expert IV"
            14 -> 500 // "Noun Expert V"
            15 -> 10  // "Verb Expert I"
            16 -> 50  // "Verb Expert II"
            17 -> 100 // "Verb Expert III"
            18 -> 200 // "Verb Expert IV"
            19 -> 500 // "Verb Expert V"
            20 -> 10  // "Adjective Expert I"
            21 -> 50  // "Adjective Expert II"
            22 -> 100 // "Adjective Expert III"
            23 -> 200 // "Adjective Expert IV"
            24 -> 500 // "Adjective Expert V"
            25 -> 10  // "Adverb Expert I"
            26 -> 50  // "Adverb Expert II"
            27 -> 100 // "Adverb Expert III"
            28 -> 200 // "Adverb Expert IV"
            29 -> 500 // "Adverb Expert V"
            30 -> 1   // "Hunter I"
            31 -> 10  // "Hunter II"
            32 -> 20  // "Hunter III"
            33 -> 50  // "Hunter IV"
            34 -> 100 // "Hunter V"
            else -> 0 // Not available
        }
        // Text content on showing (current progress to be how far has you progressed on that particular task, over the maximum threshold to reach/match that task)
        holder.achievementStatusThreshold.text = "$currentProgress/$maxProgress"
    }

    // Count total number of item
    override fun getItemCount(): Int {
        return achievementList.size
    }

    // Get the set of achievement listing and list mapper from MyAchievementFragment
    @SuppressLint("NotifyDataSetChanged") // Notify data changing when submitting the final list to make sure changes are set on real time
    fun submitList(list: List<Achievement>, progressData: Map<Int, Int>) {
        achievementList = list // Store the achievement list and content in the adapter
        this.progressData = progressData // Store the progress data (list mapper) in the adapter
        notifyDataSetChanged() // Notify and adapt changing on real time (e.g. user progress out of this fragment and come back)
    }
}
