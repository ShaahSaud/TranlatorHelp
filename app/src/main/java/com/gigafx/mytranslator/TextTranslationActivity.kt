package com.gigafx.mytranslator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gigafx.mytranslator.databinding.ActivityTextTranslationBinding

class TextTranslationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextTranslationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}