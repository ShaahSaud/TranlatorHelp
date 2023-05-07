package com.gigafx.mytranslator

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.gigafx.mytranslator.mvvm.factory.MessageViewModelFactory
import com.gigafx.mytranslator.mvvm.viewmodel.TranslationViewModel

open class BaseActivity : AppCompatActivity() {

    open val messageViewModel:TranslationViewModel by viewModels{
        MessageViewModelFactory(((this).application as Application).messageRepository)
    }


    open fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            return connectivityManager.isDefaultNetworkActive
        }
    }


    open fun share(sharedText:String){

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sharedText)
            type = "text/plain"
        }
        startActivity(sendIntent)
    }



}