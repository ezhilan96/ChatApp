package com.example.chatapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var userName: String

    val gson: Gson = Gson()

    private lateinit var mSocket: Socket

    private val onNewMessage = Emitter.Listener { args ->
        runOnUiThread {
            val chatList: MutableList<Message> = mutableListOf()
            val data = JSONArray(args[0].toString())
            for (i in 0 until data.length()) {
                var username = ""
                var message = ""
                try {
                    username = data.getJSONObject(i).getString("userName")
                    message = data.getJSONObject(i).getString("message")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                chatList.add(Message(username, message))
            }
            chatListAdapter.submitList(chatList)
            binding.chatRv.smoothScrollToPosition(chatList.size - 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSocket()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            userNameInput.requestFocus()
            userNameInput.setOnEditorActionListener { v, actionId, event ->
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    userNameSubmitBtn.callOnClick()
                    hideSoftKeyboard()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
            userNameSubmitBtn.setOnClickListener {
                if (userNameInput.text.isNullOrEmpty() || userNameInput.text.isNullOrBlank()) {
                    userNameInput.text?.clear()
                    userNameInputContainer.error = "this field is required."
                } else {
                    userNameInput.clearFocus()
                    userName = userNameInput.text.toString()
                    userSection.visibility = View.GONE
                    setChatView()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
        mSocket.off("chat", onNewMessage)
    }

    private fun initSocket() {
        SocketHandler.setSocket()
        mSocket = SocketHandler.getSocket()
        mSocket.on("chat", onNewMessage)
        SocketHandler.establishConnection()
    }

    private fun setChatView() {
        binding.apply {
            chatSection.visibility = View.VISIBLE

            chatRv.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            chatListAdapter = ChatListAdapter(userName)
            chatRv.adapter = chatListAdapter

            chatInputContainer.hint = userName
            chatInput.requestFocus()
            chatInput.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendButton.callOnClick()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            sendButton.setOnClickListener {
                mSocket.emit(
                    "chat", gson.toJson(
                        Message(
                            userName = userName, message = chatInput.text.toString()
                        )
                    )
                )
                chatInput.text?.clear()
            }
        }
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    data class Message(
        val userName: String, val message: String
    )
}