package de.frejasundalexchat.dritterchat.editBook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.frejasundalexchat.dritterchat.R

class EditBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)
        if (savedInstanceState != null) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.editBookActivity_root, EditBookFragment().also { it.arguments = this.intent.extras!! }).commit()

    }

}
