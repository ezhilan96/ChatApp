package com.example.chatapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.chatapp.MainActivity.Message
import com.example.chatapp.databinding.ItemRecievedChatBinding
import com.example.chatapp.databinding.ItemSentChatBinding

class ChatListAdapter(private val myName: String) :
    ListAdapter<Message, ChatListAdapter.ItemVH>(DiffCallback()) {

    val ItemTypeSent = 1
    val ItemTypeReceived = 2

    abstract class ItemVH(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        abstract fun bind(position: Int)
    }

    inner class SentChatViewHolder(private val binding: ItemSentChatBinding) : ItemVH(binding) {

        override fun bind(position: Int) {
            binding.apply {
                userTv.text = getItem(position).userName
                contentTv.text = getItem(position).message
            }
        }

    }

    inner class ReceivedChatViewHolder(private val binding: ItemRecievedChatBinding) :
        ItemVH(binding) {

        override fun bind(position: Int) {
            binding.apply {
                userTv.text = getItem(position).userName
                contentTv.text = getItem(position).message
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).userName == myName) ItemTypeSent
        else ItemTypeReceived
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        return if (viewType == ItemTypeSent) {
            SentChatViewHolder(
                ItemSentChatBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            ReceivedChatViewHolder(
                ItemRecievedChatBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.bind(position)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Message>() {

        override fun areContentsTheSame(
            oldItem: Message, newItem: Message
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(
            oldItem: Message, newItem: Message
        ): Boolean {
            return oldItem.userName == newItem.userName && oldItem.message == newItem.message
        }
    }

}