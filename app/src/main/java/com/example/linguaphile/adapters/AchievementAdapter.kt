package com.example.linguaphile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

    // Bind ViewHolder with data
    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievementList[position]
        holder.achievementName.text = achievement.name
        holder.achievementDescription.text = achievement.description
        // Set possible unlocked image on this achievement
        achievement.unlockImageResId?.let { holder.achievementUnlockImage.setImageResource(it) }
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
