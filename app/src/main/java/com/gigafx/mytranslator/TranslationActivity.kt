package com.gigafx.mytranslator

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.solutionturbo.gptai.searchchat.gpt3.R
import com.solutionturbo.gptai.searchchat.gpt3.adapters.ChatAdapter
import com.solutionturbo.gptai.searchchat.gpt3.ads.CheckConnections
import com.solutionturbo.gptai.searchchat.gpt3.api.OpenAiService
import com.solutionturbo.gptai.searchchat.gpt3.databinding.ActivityChatBinding
import com.solutionturbo.gptai.searchchat.gpt3.remoteconfig.RemoteConfig
import com.solutionturbo.gptai.searchchat.gpt3.repository.roomdatabase.entitiy.MessageEntity
import com.solutionturbo.gptai.searchchat.gpt3.utils.BaseActivity
import com.theokanning.openai.completion.CompletionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class TranslationActivity : BaseActivity(), ChatAdapter.ChatAdapterListener {

    private val chatAdapter by lazy {
        ChatAdapter(messageViewModel)
    }

    private var getAllMessageList: MutableList<MessageEntity> = mutableListOf()

    private lateinit var binding: ActivityChatBinding

    var isFirsTimeOpen: Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showCaseView()
        init()
        initListener()
        initObserver()
        initChatRecyclerView()
    }

    private fun showCaseView() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val hasVisited = sharedPreferences.getBoolean("hasVisited", false)

        if (!hasVisited) {
            // Show the fullscreen dialog if the activity has not been visited before
            showFullscreenDialog()

            // Save the hasVisited flag to SharedPreferences
            sharedPreferences.edit().putBoolean("hasVisited", true).apply()
        }
    }

    private fun showFullscreenDialog() {
        // Create the dialog object
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.showcase_view)

        // Set the close button click listener
        dialog.findViewById<Button>(R.id.btnCloseWindow).setOnClickListener {
            // Close the dialog
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    private fun initChatRecyclerView() {
        binding.recView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        fetchOldMessages()
        binding.recView.adapter = chatAdapter
        chatAdapter.chatAdapterListener=this
    }

    private fun fetchOldMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            messageViewModel.getAllMessages().let {
                getAllMessageList.clear()
                getAllMessageList.addAll(it)
            }

            withContext(Main){
                chatAdapter.setData(getAllMessageList)
                binding.recView.smoothScrollToPosition(getAllMessageList.size)
                checkItemPlaceHolder()
                isFirsTimeOpen=false
            }
        }
    }

    private fun init() {
        binding.txtTitle.text = intent.getStringExtra("title")
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnBackTools.setOnClickListener { clearSelection() }
        binding.btnClear.setOnClickListener {
            deleteAllMessages()
        }

        binding.btnSend.setOnClickListener {
            var msg = binding.txtMessage.text.toString()
            if (TextUtils.isEmpty(msg)) {
                Toast.makeText(this, "Type A message", Toast.LENGTH_SHORT).show()
            } else {
                //messages!!.add(Message("sender",msg,date()))

                if (CheckConnections(this).isInternetConnected()) {
                    binding.btnSend.visibility = View.GONE
                    binding.progress.visibility = View.VISIBLE
                    binding.txtMessage.setText("")
                    //updateAnimViewVisibility()

                    Thread {
                        messageViewModel.addMessage(MessageEntity(0, "User", msg, date()))

                        //code for hi
                        if (msg.lowercase() == "hi") msg = "Hi hello"

                        responseChatGPT(msg)
                    }.start()
                } else {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                }

            }
        }

        binding.btnShare.setOnClickListener { share("${resources.getString(R.string.share_msg)}\n\nhttps://play.google.com/store/apps/details?id=com.solutionturbo.gptai.searchchat.gpt3") }

        binding.btnDelete.setOnClickListener {
            deleteMessages()
        }

        binding.btnShareMessage.setOnClickListener {
            val messages = messageViewModel.selectedMessageList.map { "${it.userType}: ${it.message}" }

            val listMessages = StringBuilder()
            for (message in messages) {
                listMessages.append(message)
                listMessages.append("\n")
            }

            share(listMessages.toString())
        }

        binding.btnCopyMessage.setOnClickListener {
            val messages = messageViewModel.selectedMessageList.map {it.message }

            val listMessages = StringBuilder()
            for (message in messages) {
                listMessages.append(message)
                listMessages.append("\n")
            }

            copyMessage(listMessages.toString())
        }
    }

    private fun initObserver() {
        messageViewModel.getAllLiveMessages().observe(this) { list ->
            CoroutineScope(Dispatchers.IO).launch {
                getAllMessageList.clear()
                getAllMessageList.addAll(list)

                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    chatAdapter.setData(getAllMessageList)
                    binding.recView.smoothScrollToPosition(getAllMessageList.size)
                    checkItemPlaceHolder()
                }
            }
        }

        //TODO: we will need to implement for single messsage
//        messageViewModel.getSingleLiveMessage().observe(this) { singleMessage ->
//            CoroutineScope(Dispatchers.IO).launch {
//                if(!isFirsTimeOpen){
//                    getAllMessageList.add(singleMessage)
//
//                    val handler = Handler(Looper.getMainLooper())
//                    handler.post {
//                        chatAdapter.setSingleMessageData(singleMessage)
//                        binding.recView.smoothScrollToPosition(getAllMessageList.size)
//                        checkItemPlaceHolder()
//                    }
//                }
//            }
//        }
    }

    private fun checkItemPlaceHolder() {
        if (getAllMessageList.size > 0) {
            binding.noItemPlaceholder.visibility = View.GONE
            binding.btnClear.visibility = View.VISIBLE
        } else {
            binding.noItemPlaceholder.visibility = View.VISIBLE
            binding.btnClear.visibility = View.GONE
        }
    }

    private fun clearSelection(){
        messageViewModel.selectedMessageList.clear()
        messageSectionAction()
        chatAdapter.notifyDataSetChanged()
    }


    private fun responseChatGPT(send: String) {
        val stopSequence = arrayListOf<String>("Human:","AI:")

        Log.d("test_error", "sending api: ${RemoteConfig.apiKey}")
        val response: String = try {
            val service = OpenAiService(RemoteConfig.apiKey)
            val completionRequest =
                CompletionRequest.builder()
                    .model("text-davinci-003")
                    .prompt(send)
                    .maxTokens(150)
                    .temperature(0.9)
                    .topP(1.0)
                    .frequencyPenalty(0.0)
                    .presencePenalty(0.6)
                    .build()

            service.createCompletion(completionRequest).choices[0].text
        } catch (e: Exception) {
            Log.d("test_error", "responseChatGPT: ${e.localizedMessage}")
            "Check Your Internet & Restart The Application"
        }

        val handler = Handler(Looper.getMainLooper())
        handler.post {
            binding.btnSend.visibility = View.VISIBLE
            binding.progress.visibility = View.GONE
            if (TextUtils.isEmpty(response)) {
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
            } else {
                val simpleResponse = response.replace("\n","")
                messageViewModel.addMessage(MessageEntity(0, "GPT AI SEARCH", simpleResponse, date()))
            }
        }

    }

    private fun date(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd MMM - yy")
            current.format(formatter)
        } else {
            val date = Date()
            val formatter = SimpleDateFormat("MMM-dd-yy")
            formatter.format(date)
        }
    }

    private fun deleteMessages() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete")
            .setMessage("Are you sure to Delete ${messageViewModel.selectedMessageList.size} Messages?")
            .setNeutralButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->

                messageViewModel.selectedMessageList.forEach {messageEntity ->
                    messageViewModel.deleteSingleMessage(messageEntity)
                }
                messageViewModel.selectedMessageList.clear()
                messageSectionAction()
            }.show()
    }
    private fun deleteAllMessages() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete")
            .setMessage("Are you sure to clear all Messages?")
            .setNeutralButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                messageViewModel.deleteAllMessage()
            }.show()
    }

    override fun onMessageSingleClick(messageEntity: MessageEntity, position: Int) {

        if(messageViewModel.selectedMessageList.size>0){
            if(messageViewModel.selectedMessageList.contains(messageEntity)){
                messageViewModel.selectedMessageList.remove(messageEntity)
                binding.textViewCounter.text= messageViewModel.selectedMessageList.size.toString()
            }else{
                messageViewModel.selectedMessageList.add(messageEntity)
                binding.textViewCounter.text= messageViewModel.selectedMessageList.size.toString()
            }

            chatAdapter.notifyItemChanged(position)
            Log.d("test_msg", "onMessageLongClick: in main on: ${messageEntity.message} and position:   $position")


            messageSectionAction()
        }
    }

    override fun onMessageLongClick(messageEntity: MessageEntity,position: Int) {

            if(messageViewModel.selectedMessageList.contains(messageEntity)){
                messageViewModel.selectedMessageList.remove(messageEntity)
                binding.textViewCounter.text= messageViewModel.selectedMessageList.size.toString()
            }else{
                messageViewModel.selectedMessageList.add(messageEntity)
                binding.textViewCounter.text= messageViewModel.selectedMessageList.size.toString()

            }

            chatAdapter.notifyItemChanged(position)
            Log.d("test_msg", "onMessageLongClick: in main on: ${messageEntity.message} and position:   $position")


            messageSectionAction()
    }

    private fun messageSectionAction(){
        if(messageViewModel.selectedMessageList.size>0){
            binding.msgActionToolBar.visibility=View.VISIBLE
            binding.titleToolbar.visibility=View.GONE
        }else{
            binding.msgActionToolBar.visibility=View.GONE
            binding.titleToolbar.visibility=View.VISIBLE
        }
    }

    private fun copyMessage(message: String?) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("message", message)
        clipboard.setPrimaryClip(clip)

        clearSelection()
        Toast.makeText(this, "Copied", Toast.LENGTH_LONG).show()
    }

}