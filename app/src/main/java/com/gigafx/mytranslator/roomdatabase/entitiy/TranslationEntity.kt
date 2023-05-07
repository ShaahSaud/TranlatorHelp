package com.gigafx.mytranslator.roomdatabase.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_translation")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int,
    @ColumnInfo(name="original_text") var original_text:String,
    @ColumnInfo(name="translated_text") var translated_text:String,
    @ColumnInfo(name="translation_type") var translation_type:String,
)
