//package com.gigafx.mytranslator.utils
//
//import android.util.Log
//import com.google.cloud.translate.TranslateOptions
//import com.google.mlkit.common.model.RemoteModelSource
//import com.google.mlkit.nl.languageid.LanguageIdentification
//import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
//
//import java.util.*
//
//class Translator {
//    private val TAG = "Translator"
//    private val options = TranslateOptions.Builder()
//        .setSourceLanguage(TranslateLanguage.ENGLISH)
//        .setTargetLanguage(TranslateLanguage.SPANISH)
//        .build()
//    private val translator: Translator = Translation.getClient(options)
//    private var modelDownloaded = false
//    private val languageIdentifier: LanguageIdentifier = LanguageIdentification.getClient(
//        LanguageIdentificationOptions.Builder()
//            .setConfidenceThreshold(0.1f)
//            .build()
//    )
//
//    init {
//        val model = TranslateRemoteModel.Builder(TranslateLanguage.SPANISH).build()
//        val modelManager = TranslationModelManager.getInstance()
//        modelManager.download(model, RemoteModelSource.ENABLED)
//            .addOnSuccessListener { modelDownloaded = true }
//            .addOnFailureListener { e -> Log.e(TAG, "Error downloading language model: ${e.message}") }
//    }
//
//    fun translateText(text: String, sourceLanguage: String, targetLanguage: String, callback: (String) -> Unit) {
//        if (!modelDownloaded) {
//            Log.e(TAG, "Language model not downloaded yet")
//            return
//        }
//
//        languageIdentifier.identifyLanguage(text)
//            .addOnSuccessListener { languageCode ->
//                val detectedLanguage = TranslateLanguage.fromLanguageTag(languageCode)
//                if (detectedLanguage == TranslateLanguage.UNKNOWN_LANGUAGE) {
//                    Log.e(TAG, "Could not identify source language")
//                    return@addOnSuccessListener
//                }
//
//                val options = TranslateOptions.Builder()
//                    .setSourceLanguage(detectedLanguage)
//                    .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetLanguage))
//                    .build()
//
//                val translator = Translation.getClient(options)
//                translator.translate(text)
//                    .addOnSuccessListener { translatedText ->
//                        callback(translatedText)
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e(TAG, "Error translating text: ${e.message}")
//                    }
//            }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "Error identifying language: ${e.message}")
//            }
//    }
//}
