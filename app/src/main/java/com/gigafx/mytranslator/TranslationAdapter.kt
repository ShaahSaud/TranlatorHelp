package com.gigafx.mytranslator

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.solutionturbo.gptai.searchchat.gpt3.R
import com.solutionturbo.gptai.searchchat.gpt3.databinding.ItemMessageRecivedBinding
import com.solutionturbo.gptai.searchchat.gpt3.databinding.ItemMessageSentBinding
import com.solutionturbo.gptai.searchchat.gpt3.mvvm.viewmodel.MessageViewModel
import com.solutionturbo.gptai.searchchat.gpt3.repository.roomdatabase.entitiy.MessageEntity

class TranslationAdapter(private val viewModel: MessageViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var getAllMessage: MutableList<MessageEntity> = mutableListOf()
    private val viewTypeSender = 1
    private val viewTypeReceived = 2

    var chatAdapterListener: ChatAdapterListener? = null

    class MessageViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewTypeSender -> {
                MessageViewHolder(
                    ItemMessageSentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            viewTypeReceived -> {
                MessageViewHolder(
                    ItemMessageRecivedBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("You must supply a valid type")
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MessageViewHolder) {
            when (holder.binding) {
                is ItemMessageSentBinding -> {

                    if(viewModel.selectedMessageList.contains(getAllMessage[position])){
                        holder.itemView.setBackgroundColor(ResourcesCompat.getColor(holder.itemView.resources, R.color.selected_message, null))
                    }else
                        holder.itemView.setBackgroundColor(ResourcesCompat.getColor(holder.itemView.resources, R.color.transparent, null))


                    holder.binding.txtMessage.text = getAllMessage[position].message
                    holder.binding.txtDateTime.text = getAllMessage[position].date

                    holder.itemView.setOnClickListener {
                        chatAdapterListener?.onMessageSingleClick(getAllMessage[position],position)
                    }

                    holder.itemView.setOnLongClickListener {
                        chatAdapterListener?.onMessageLongClick(getAllMessage[position],position)
                        true
                    }

                }
                is ItemMessageRecivedBinding -> {

                    if(viewModel.selectedMessageList.contains(getAllMessage[position])){
                        holder.itemView.setBackgroundColor(ResourcesCompat.getColor(holder.itemView.resources, R.color.selected_message, null))
                    }else
                        holder.itemView.setBackgroundColor(ResourcesCompat.getColor(holder.itemView.resources, R.color.transparent, null))


                    holder.binding.txtMessage.text = getAllMessage[position].message
                    holder.binding.txtDateTime.text = getAllMessage[position].date


                    holder.itemView.setOnClickListener {
                        chatAdapterListener?.onMessageSingleClick(getAllMessage[position],position)
                    }

                    holder.itemView.setOnLongClickListener {
                        chatAdapterListener?.onMessageLongClick(getAllMessage[position],position)
                        true
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: MutableList<MessageEntity>) {
        getAllMessage.clear()
        getAllMessage.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        //return messages.size
        return getAllMessage.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (getAllMessage[position].userType == "User") {
            viewTypeSender
        } else {
            viewTypeReceived
        }
    }


    interface ChatAdapterListener {
        fun onMessageSingleClick(messageEntity: MessageEntity,position: Int)
        fun onMessageLongClick(messageEntity: MessageEntity,position: Int)
    }


}