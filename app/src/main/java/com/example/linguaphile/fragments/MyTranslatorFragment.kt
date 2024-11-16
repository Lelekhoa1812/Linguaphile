package com.example.linguaphile.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.linguaphile.R
import com.example.linguaphile.entities.ModelLanguage
import com.google.android.material.button.MaterialButton
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale
import kotlin.collections.ArrayList

class MyTranslatorFragment : Fragment() {

    // UI Views
    private lateinit var sourceLanguageEt: EditText
    private lateinit var targetLanguageTv: TextView
    private lateinit var sourceLanguageChooseBtn: MaterialButton
    private lateinit var targetLanguageChooseBtn: MaterialButton
    private lateinit var translateBtn: MaterialButton

    // General usage
    companion object {
        // Common use of tag for debug logs
        private const val TAG = "MyTranslatorFragment"
    }
    // Contain the list with language code and title
    private var languageArrayList: ArrayList<ModelLanguage>? = null
    // Default/selected language codes and titles
    private var sourceLanguageCode = "en"
    private var sourceLanguageTitle = "English"
    private var targetLanguageCode = "vi"
    private var targetLanguageTitle = "Vietnamese"
    // Translator options to set source and destination languages
    private lateinit var translatorOptions: TranslatorOptions
    // Translator object configuring itself by the src and targeted language
    private lateinit var translator: Translator
    // ProgressDialog to show while translation process (loading)
    private lateinit var progressDialog: AlertDialog

    // On create initialisation and binding data (if exist)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_translator, container, false)
        // Initialize UI views using 'view.findViewById'
        sourceLanguageEt = view.findViewById(R.id.sourceLanguageEt)
        targetLanguageTv = view.findViewById(R.id.targetLanguageTv)
        sourceLanguageChooseBtn = view.findViewById(R.id.sourceLanguageChooseBtn)
        targetLanguageChooseBtn = view.findViewById(R.id.targetLanguageChooseBtn)
        translateBtn = view.findViewById(R.id.translateBtn)
        // Initialize the AlertDialog as a progress dialog
        progressDialog = AlertDialog.Builder(requireContext())
            .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
            .setCancelable(false)
            .create()
        // Trigger language loader
        loadAvailableLanguages()
        // Set button click listeners
        sourceLanguageChooseBtn.setOnClickListener { sourceLanguageChoose() }
        targetLanguageChooseBtn.setOnClickListener { targetLanguageChoose() }
        translateBtn.setOnClickListener { validateData() }
        return view
    }


    // Source language before user type in will be empty
    private var sourceLanguageText = ""

    // Function to validate if the text entry is valid (not null or empty) before submission
    private fun validateData() {
        sourceLanguageText = sourceLanguageEt.text.toString().trim()
        Log.d(TAG, "validateData: sourceLanguageText: $sourceLanguageText")
        if (sourceLanguageText.isEmpty()) {
            showToast("Enter text to translate...")
        } else {
            startTranslation() // If valid data, process to submit data and start translation
        }
    }

    // Start translation process by building ML translator model
    private fun startTranslation() {
        progressDialog.setMessage("Processing language model...")
        progressDialog.show() // Show dialog of progression loader
        // Build translator option from source and target language on their code
        translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguageCode)
            .setTargetLanguage(targetLanguageCode)
            .build()
        // getClient call with translator option context bind to the Translate object
        translator = Translation.getClient(translatorOptions)
        // Define requisite conditions to build the translator, mandatory wifi
        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        // Download translator model ML  to local device, hence, allow offline translation
        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener { // If able to load ML
                Log.d(TAG, "startTranslation: model ready, start translation...")
                progressDialog.setMessage("Translating...") // Set dialog on loading
                // Bind the source translated text
                translator.translate(sourceLanguageText)
                    .addOnSuccessListener { translatedText -> // If success
                        Log.d(TAG, "startTranslation: translatedText: $translatedText")
                        progressDialog.dismiss() // Disable progress loader dialog
                        targetLanguageTv.text = translatedText } // Bind translated text data
                    .addOnFailureListener { e -> // If failed, catch error
                        progressDialog.dismiss()
                        Log.e(TAG, "startTranslation: ", e)
                        showToast("Failed to translate due to ${e.message}") // Show toast on catch exception error
                    }
            } // If not able to load ML
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Log.e(TAG, "startTranslation: ", e)
                showToast("Failed due to ${e.message}") // Show toast on catch exception error
            }
    }

    private fun loadAvailableLanguages() {
        // Initialise language array before binding data
        languageArrayList = ArrayList()
        val languageCodeList = TranslateLanguage.getAllLanguages()
        // List of language code and title
        for (languageCode in languageCodeList) {
            // Convert language title to code (English -> en)
            val languageTitle = Locale(languageCode).displayLanguage
            // Logs
            Log.d(TAG, "loadAvailableLanguages: languageCode: $languageCode")
            Log.d(TAG, "loadAvailableLanguages: languageTitle: $languageTitle")
            // Prepare language model
            val modelLanguage = ModelLanguage(languageCode, languageTitle)
            // Added to list (casing null)
            languageArrayList!!.add(modelLanguage)
        }
    }

    // Initialise Popup Menu on list of source language item that can be chosen
    private fun sourceLanguageChoose() {
        // Popup Menu layout consist of the context + required button
        val popupMenu = PopupMenu(requireContext(), sourceLanguageChooseBtn)
        // Display language titles from languageArrayList
        for (i in languageArrayList!!.indices) {
            popupMenu.menu.add(0, i, i, languageArrayList!![i].languageTitle)
        }
        popupMenu.show()
        // Handle click on menu with the corresponding position of the item clicked (mapped with item's id)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val position = menuItem.itemId
            // Get code and title from clicked item
            sourceLanguageCode = languageArrayList!![position].languageCode
            sourceLanguageTitle = languageArrayList!![position].languageTitle
            // Set title and provide hint on EditText view
            sourceLanguageChooseBtn.text = sourceLanguageTitle
            sourceLanguageEt.hint = "Enter $sourceLanguageTitle"
            // Logs
            Log.d(TAG, "sourceLanguageChoose: sourceLanguageCode: $sourceLanguageCode")
            Log.d(TAG, "sourceLanguageChoose: sourceLanguageTitle: $sourceLanguageTitle")
            // return false
            false
        }
    }

    // Initialise Popup Menu on list of target language item that can be chosen
    private fun targetLanguageChoose() {
        val popupMenu = PopupMenu(requireContext(), targetLanguageChooseBtn)        // Display language titles from languageArrayList
        for (i in languageArrayList!!.indices) {
            popupMenu.menu.add(0, i, i, languageArrayList!![i].languageTitle)
        }
        popupMenu.show()
        // Handle click on menu with the corresponding position of the item clicked (mapped with item's id)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val position = menuItem.itemId
            // Get code and title from clicked item
            targetLanguageCode = languageArrayList!![position].languageCode
            // Set title and provide hint on EditText view
            targetLanguageTitle = languageArrayList!![position].languageTitle
            targetLanguageChooseBtn.text = targetLanguageTitle
            // Logs
            Log.d(TAG, "targetLanguageChoose: targetLanguageCode: $targetLanguageCode")
            Log.d(TAG, "targetLanguageChoose: targetLanguageTitle: $targetLanguageTitle")
            // return false
            false
        }
    }

    // Define generate reflection message with Toast
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
