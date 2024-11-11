package com.example.linguaphile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaphile.R
import com.example.linguaphile.entities.Achievement

class AchievementAdapter : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {
    private var achievementList: List<Achievement> = emptyList()

    // Initialise content for ViewHolder
    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val achievementName: TextView = itemView.findViewById(R.id.achievementName)
        val achievementDescription: TextView = itemView.findViewById(R.id.achievementDescription)
        val achievementUnlockImage: ImageView = itemView.findViewById(R.id.achievementUnlockImage)
        val achievementStatusImage: ImageView = itemView.findViewById(R.id.achievementStatusImage)
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
    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievementList[position]
        holder.achievementName.text = achievement.name
        holder.achievementDescription.text = achievement.description
        // Set possible unlocked image on this achievement
        achievement.unlockImageResId?.let {
            holder.achievementUnlockImage.setImageResource(it) // Set image resource if null, skipped
            holder.achievementUnlockImage.setBackgroundColor(getBackgroundColorForImage(achievement.unlockImageResId, holder.achievementUnlockImage)) // Set background color on class
        }
        // Set image resource dynamically on achievement task status
        holder.achievementStatusImage.setImageResource(achievement.status)
    }

    // Count total number of item
    override fun getItemCount(): Int {
        return achievementList.size
    }

    // Submit listing of achievement
    fun submitList(list: List<Achievement>) {
        achievementList = list
        notifyDataSetChanged()
    }
}
