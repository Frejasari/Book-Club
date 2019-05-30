package de.frejasundalexchat.dritterchat

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import de.frejasundalexchat.dritterchat.db.ObjectBox

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        ObjectBox.init(this)
    }
}