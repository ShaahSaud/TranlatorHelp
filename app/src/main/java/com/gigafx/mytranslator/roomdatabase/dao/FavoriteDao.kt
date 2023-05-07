package com.gigafx.mytranslator.roomdatabase.dao

import androidx.room.*
import com.gigafx.mytranslator.roomdatabase.entitiy.FavoriteEntity
import com.gigafx.mytranslator.roomdatabase.entitiy.TranslationEntity

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteEntity: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favoriteEntity: FavoriteEntity)

    @Query("DELETE FROM table_favorite")
    suspend fun deleteAllTranslations()

//    @Delete
//    suspend fun deleteMessages(messages: List<TranslationEntity>)


    @get:Query("SELECT * FROM table_favorite ORDER BY date ASC")
    val allFavorites: List<FavoriteEntity>

//    @get:Query("SELECT * FROM table_message ORDER BY message_date ASC")
//    val allLiveMessage: LiveData<List<TranslationEntity>>
//
//    @get:Query("SELECT * FROM table_message ORDER BY _id DESC LIMIT 1")
//    val singleLiveMessage: LiveData<TranslationEntity>
}