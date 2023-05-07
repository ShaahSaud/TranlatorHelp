package com.gigafx.mytranslator.mvvm.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.gigafx.mytranslator.roomdatabase.dao.TranslationDao
import com.gigafx.mytranslator.roomdatabase.entitiy.TranslationEntity

class TranslationsRepository internal constructor(private val translationDao: TranslationDao){


    @WorkerThread
    suspend fun saveTransaltion(message: TranslationEntity){
     translationDao.insertTranslation(message)
    }

    @WorkerThread
    suspend fun deleteTranslation(message: TranslationEntity){
        translationDao.deleteTranslation(message)
    }

    @WorkerThread
    suspend fun deleteTranslations(messages:List<TranslationEntity>){
        translationDao.deleteMessages(messages)
    }

    @WorkerThread
    suspend fun deleteAllTranslations(){
        translationDao.deleteAllTranslations()
    }

    @WorkerThread
    suspend fun fetchAllTranslations(): List<TranslationEntity>{
        return translationDao.allTranslation
    }
    @WorkerThread
    fun fetchSingleLiveMessage(): LiveData<TranslationEntity>{
        return translationDao.singleLiveMessage
    }

    @WorkerThread
    fun fetchAllLiveMessage(): LiveData<List<TranslationEntity>>{
        return translationDao.allLiveMessage
    }
}