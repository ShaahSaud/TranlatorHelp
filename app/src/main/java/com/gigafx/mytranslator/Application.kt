package com.gigafx.mytranslator

import android.app.Application
import androidx.multidex.MultiDex
import com.gigafx.mytranslator.mvvm.repository.TranslationsRepository
import com.gigafx.mytranslator.roomdatabase.MyRoomDatabase


class Application : Application(){
    val database by lazy { MyRoomDatabase.getInstance(this) }
    val messageRepository by lazy { TranslationsRepository(database.messageDao())}



    override fun onCreate() {
        super.onCreate()

        MultiDex.install(this)
    }
}