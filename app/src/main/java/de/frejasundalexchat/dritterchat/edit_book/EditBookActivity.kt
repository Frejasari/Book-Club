package de.frejasundalexchat.dritterchat.edit_book

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.frejasundalexchat.dritterchat.R

class EditBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_book_activity)
        if (savedInstanceState != null) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.editBookActivity_root, EditBookFragment().also { it.arguments = this.intent.extras!! }).commit()

    }

}
