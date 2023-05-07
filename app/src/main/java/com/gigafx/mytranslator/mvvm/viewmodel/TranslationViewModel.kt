package com.gigafx.mytranslator.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gigafx.mytranslator.mvvm.repository.TranslationsRepository
import com.gigafx.mytranslator.roomdatabase.entitiy.TranslationEntity
import kotlinx.coroutines.launch

class TranslationViewModel(private val translationsRepository: TranslationsRepository): ViewModel() {

    //val selectedMessageList: MutableList<TranslationEntity> = mutableListOf()

//    fun getAllLiveMessages(): LiveData<List<TranslationEntity>>{
//        return messageRepository.fetchAllLiveMessage()
//    }

    suspend fun getAllTranslation(): List<TranslationEntity>{
        return translationsRepository.fetchAllTranslations()
    }

//    fun getSingleLiveMessage(): LiveData<TranslationEntity>{
//        return messageRepository.fetchSingleLiveMessage()
//    }

    fun addTranslation(message: TranslationEntity) = viewModelScope.launch {
        translationsRepository.saveTransaltion(message)
    }
    fun deleteSingleTranslation(message: TranslationEntity) = viewModelScope.launch {
        translationsRepository.deleteTranslation(message)
    }

    fun deleteTranslations(messages:List<TranslationEntity>) = viewModelScope.launch{
        translationsRepository.deleteTranslations(messages)
    }
    fun deleteAllTranslations() = viewModelScope.launch {
        translationsRepository.deleteAllTranslations()
    }

}