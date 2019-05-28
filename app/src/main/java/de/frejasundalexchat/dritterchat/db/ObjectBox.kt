package de.frejasundalexchat.dritterchat.db

import android.content.Context
import de.frejasundalexchat.dritterchat.model.MyObjectBox
import io.objectbox.BoxStore


object ObjectBox {

    private lateinit var boxStore: BoxStore

    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }

    fun get(): BoxStore {
        return boxStore
    }
}