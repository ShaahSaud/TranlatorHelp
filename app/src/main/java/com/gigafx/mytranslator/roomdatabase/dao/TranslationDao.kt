package com.gigafx.mytranslator.roomdatabase.dao

import androidx.room.*
import com.gigafx.mytranslator.roomdatabase.entitiy.TranslationEntity

@Dao
interface TranslationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translationEntity: TranslationEntity)

    @Delete
    suspend fun deleteTranslation(translationEntity: TranslationEntity)

    @Query("DELETE FROM table_translation")
    suspend fun deleteAllTranslations()

//    @Delete
//    suspend fun deleteMessages(messages: List<TranslationEntity>)


    @get:Query("SELECT * FROM table_translation ORDER BY date ASC")
    val allTranslation: List<TranslationEntity>

//    @get:Query("SELECT * FROM table_message ORDER BY message_date ASC")
//    val allLiveMessage: LiveData<List<TranslationEntity>>
//
//    @get:Query("SELECT * FROM table_message ORDER BY _id DESC LIMIT 1")
//    val singleLiveMessage: LiveData<TranslationEntity>

}