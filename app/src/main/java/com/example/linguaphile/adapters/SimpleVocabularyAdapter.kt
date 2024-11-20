package com.example.linguaphile.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.linguaphile.R
import com.example.linguaphile.entities.Vocabulary

// Different adapter class to VocabularyAdapter to simply binding and list out Vocabulary RecyclerView item for SIA fragment without any filtration and update/delete on action
class SimpleVocabularyAdapter(
) : ListAdapter<Vocabulary, SimpleVocabularyAdapter.SimpleVocabularyViewHolder>(VocabularyDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleVocabularyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.incorrect_vocabulary_item, parent, false)
        Log.d("SimpleVocabularyAdapter", "Creating view holder") // Logs
        return SimpleVocabularyViewHolder(view)
    }
    override fun onBindViewHolder(holder: SimpleVocabularyViewHolder, position: Int) {
        val vocabulary = getItem(position)
        Log.d("SimpleVocabularyAdapter", "Binding vocabulary item at position $position: ${vocabulary.name}") // Logs
        holder.bind(vocabulary) // Bind data
    }

    // Get the vocab list obtained from MiniGameFragment submission
    override fun submitList(list: List<Vocabulary>?) {
        Log.d("SimpleVocabularyAdapter", "submitList called with ${list?.size ?: 0} items")
        if (!list.isNullOrEmpty()) {
            super.submitList(list)
        } else {
            Log.d("SimpleVocabularyAdapter", "Ignoring empty list submission to avoid clearing data.")
        }
    }

    // Bind data with styling for all listed vocabulary item, similarly to the normal VocabularyAdapter, only that we not binding option button and onDelete, onUpdate btn
    class SimpleVocabularyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.vocab_name)
        private val typeTextView: TextView = itemView.findViewById(R.id.vocab_type)
        private val meaningTextView: TextView = itemView.findViewById(R.id.vocab_meanings)
        private val synonymTextView: TextView = itemView.findViewById(R.id.vocab_synonyms)
        private val synonymBlock: LinearLayout = itemView.findViewById(R.id.synonymBlock)
        private val noteTextView: TextView = itemView.findViewById(R.id.vocab_note)
        private val noteBlock: LinearLayout = itemView.findViewById(R.id.noteBlock)

        @SuppressLint("SetTextI18n")
        fun bind(vocabulary: Vocabulary) {
            Log.d("SimpleVocabularyAdapter", "Binding vocabulary: ${vocabulary.name}") // Logs
            nameTextView.text = vocabulary.name
            typeTextView.text = vocabulary.type
            meaningTextView.text = listOfNotNull( // If any afterward meaning is null or empty, don't append to the list
                vocabulary.meaning1, vocabulary.meaning2, vocabulary.meaning3, vocabulary.meaning4
            ).joinToString(", ")
            if (vocabulary.synonym1.isNullOrEmpty()) { synonymBlock.visibility = View.GONE } // Only show synonym if this vocab item doesn't null this field (check for synonym1 first since it's pre-requisite)
            else { // There exist at least 1 synonym
                synonymBlock.visibility = View.VISIBLE // Enable the synonym block
                synonymTextView.text = listOfNotNull( // If any afterward synonym is null or empty, don't append to the list
                    vocabulary.synonym1, vocabulary.synonym2, vocabulary.synonym3, vocabulary.synonym4
                ).joinToString(", ") }
            if (vocabulary.note.isNullOrEmpty()) { noteBlock.visibility = View.GONE } // Only show note if this vocab item doesn't null this field
            else { // There exist at least 1 note
                noteBlock.visibility = View.VISIBLE // Enable the note block
                noteTextView.text = vocabulary.note } // If note is null or empty, don't append
            }
        }
    }

    class VocabularyDiffCallback : DiffUtil.ItemCallback<Vocabulary>() {
        // Make sure the vocab are not duplicated by their id
        override fun areItemsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
            return oldItem.id == newItem.id
        }
        // Make sure the vocab are not duplicated by their name
        override fun areContentsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
            return oldItem == newItem
        }
    }

