package de.frejasundalexchat.dritterchat

import androidx.lifecycle.ViewModel

typealias ChatHistory = List<String>

class MainViewModel : ViewModel() {
    private val chat = Chat()
    private val listeners = Listeners<ChatHistory>()

    init {
        chat.addListener(::onMessageReceived)
    }

    fun addListener(listener: (ChatHistory) -> Unit): () -> Unit {
        return listeners.add(listener)
    }

    private fun onMessageReceived(message: String) {
        listeners.notify(this.chat.getMessages())
    }

    fun onSendButtonClick(message: String) {
        if (message.isEmpty()) {
            return
        }
        this.chat.sendMessage(message)
    }

}

class Listeners<T> {
    val listeners = mutableListOf<(T) -> Unit>()

    fun notify(message: T) {
        listeners.forEach { listener -> listener(message) }
    }

    fun add(listener: (T) -> Unit): () -> Unit {
        listeners.add(listener)
        return { listeners.remove(listener) }
    }

}

class Chat {
    private val messages = mutableListOf<String>()
    private val listeners = Listeners<String>()

    fun addListener(listener: (String) -> Unit) {
        listeners.add(listener)
    }


    fun getMessages() = messages as ChatHistory

    fun sendMessage(message: String) {
        messages.add(message)
        listeners.notify(message)
    }
}