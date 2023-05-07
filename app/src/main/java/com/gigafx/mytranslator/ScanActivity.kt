package com.gigafx.mytranslator

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.gigafx.mytranslator.databinding.ActivityScanBinding
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_PICK_IMAGE = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnGallery.setOnClickListener {
                openGallery(it) }
        }

    }

    fun openGallery(view: android.view.View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PICK_IMAGE -> {
                    val selectedImage = data?.data
                    if (selectedImage != null) {
                        binding.prevImg.setImageURI(selectedImage)
                        try {
                            val inputStream = contentResolver.openInputStream(selectedImage)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            recognizeText(bitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun recognizeText(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val result: Task<Text> = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                val resultText = visionText.text
                showCustomDialog(resultText)
            }
            .addOnFailureListener { exception ->
                // Task failed with an exception
                exception.printStackTrace()
            }
    }

    private fun showCustomDialog(resultText: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(LayoutInflater.from(this).inflate(R.layout.custom_dialog, null))
       // dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val textView = dialog.findViewById<TextView>(R.id.textView)
        textView.text = resultText

        val button = dialog.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}