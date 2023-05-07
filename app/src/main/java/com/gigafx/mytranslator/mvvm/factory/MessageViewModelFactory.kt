package com.gigafx.mytranslator.mvvm.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gigafx.mytranslator.mvvm.repository.TranslationsRepository
import com.gigafx.mytranslator.mvvm.viewmodel.TranslationViewModel

class MessageViewModelFactory(private val translationsRepository: TranslationsRepository):
    ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TranslationViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return TranslationViewModel(translationsRepository) as T
            }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}