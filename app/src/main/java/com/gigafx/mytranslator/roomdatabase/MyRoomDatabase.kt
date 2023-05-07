package com.gigafx.mytranslator.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gigafx.mytranslator.roomdatabase.dao.TranslationDao
import com.gigafx.mytranslator.roomdatabase.entitiy.TranslationEntity


@Database(entities = [TranslationEntity::class], version = 2, exportSchema = false)
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun messageDao(): TranslationDao

    companion object {

        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getInstance(context: Context): MyRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyRoomDatabase::class.java,
                    "my_database"
                ).fallbackToDestructiveMigration().enableMultiInstanceInvalidation().build()
                INSTANCE = instance
                instance
            }
        }
    }
}


