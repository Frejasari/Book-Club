package de.frejasundalexchat.dritterchat

import android.app.Application
import de.frejasundalexchat.dritterchat.db.ObjectBox

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)
    }
}