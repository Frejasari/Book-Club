package de.frejasundalexchat.dritterchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var deleteViewModelListener: () -> Unit
    private lateinit var sendButton: Button
    private lateinit var messagesView: RecyclerView
    private lateinit var messageInput: EditText

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        this.sendButton = findViewById(R.id.sendButton)
        this.messagesView = findViewById(R.id.messagesView)
        this.messageInput = findViewById(R.id.messageInput)

        this.messagesView.layoutManager = LinearLayoutManager(this)
        val messagesViewAdapter = MessagesViewAdapter()
        this.messagesView.adapter = messagesViewAdapter

        deleteViewModelListener = viewModel.addListener { history: ChatHistory ->
            messagesViewAdapter.chatHistory = history
            messagesViewAdapter.notifyDataSetChanged()
        }
        sendButton.setOnClickListener { this.viewModel.onSendButtonClick(this.messageInput.text.toString()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteViewModelListener()
    }
}

class MessagesViewAdapter : RecyclerView.Adapter<MessagesViewAdapter.MessagesViewHolder>() {

    var chatHistory: ChatHistory = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_view, parent, false))
    }

    override fun getItemCount(): Int {
        return chatHistory.size
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val message = chatHistory[position]
        holder.bind(message)
    }

    class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageView: TextView = itemView.findViewById(R.id.message)

        fun bind(message: String) {
            messageView.text = message
        }
    }
}
