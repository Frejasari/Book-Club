package de.frejasundalexchat.dritterchat.bookOverview

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import de.frejasundalexchat.dritterchat.R
import de.frejasundalexchat.dritterchat.db.ObjectBox
import de.frejasundalexchat.dritterchat.error.ValidationError
import de.frejasundalexchat.dritterchat.model.Book
import io.objectbox.kotlin.boxFor


private const val TITLE = "book title"
private const val COVERURL = "book cover url"

class CreateBookFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var title: String = ""
    private var coverUrl: String = ""

    private lateinit var titleInput: EditText
    private lateinit var coverUrlInput: EditText
    private lateinit var saveButton: Button
    private lateinit var abortButton: Button

    private val bookBox = ObjectBox.get().boxFor(Book::class)

    companion object {
        fun newInstance(): CreateBookFragment {
            val fragment = CreateBookFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(TITLE) ?: ""
            coverUrl = it.getString(COVERURL) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleInput = view.findViewById(R.id.bookTitleInput)
        coverUrlInput = view.findViewById(R.id.imgUrlInput)
        saveButton = view.findViewById(R.id.saveBookButton)
        abortButton = view.findViewById(R.id.abortButton)

        abortButton.setOnClickListener { activity?.finish() }
        saveButton.setOnClickListener(this::onSave)


        title.apply { titleInput.setText(title) }
        coverUrl.apply { coverUrlInput.setText(coverUrl) }

        titleInput.addOnTextChangedListener { text -> title = text }
        coverUrlInput.addOnTextChangedListener { text -> coverUrl = text }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(TITLE, title)
        outState.putString(COVERURL, coverUrl)
        super.onSaveInstanceState(outState)
    }

    private fun onSave(saveButton: View) {
        val errors = this.validateInput()
        if (errors.isEmpty()) {
            bookBox.put(Book(0, title, coverUrl, 100))
            activity?.finish()
        } else {
            errors.forEach { view!!.findViewById<TextInputLayout>(it.viewId).error = it.message }
        }
    }

    private fun validateInput(): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        if (this.title.isBlank()) {
            errors.add(ValidationError(R.id.bookNameInputLayout, "Please set a title."))
        }
        if (this.coverUrl.isBlank()) {
            errors.add(ValidationError(R.id.imgUrlInputLayout, "Please select a valid image.")) // TODO
        }
        return errors
    }
}

fun EditText.addOnTextChangedListener(onChange: (s: String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.isNullOrBlank()) {
                onChange("")
            }
            onChange(s.toString())
        }
    })
}

fun View.getChildTree(level: Int) {
    if (this !is ViewGroup) {
        Log.i("childTree", " ${getLevelIcon(level)} view ${this.javaClass.name} | ${this.width} x ${this.height}")
        return
    }

    Log.i("childTree", "${getLevelIcon(level)} viewgroup ${this.javaClass.name} | ${this.width} x ${this.height}")
    for (child in children) {
        if (child is ViewGroup) {
            child.getChildTree(level + 1)
        } else {
            Log.i(
                "childTree",
                "${getLevelIcon(level + 1)} view ${child.javaClass.name} | ${this.width} x ${this.height}"
            )
        }
    }
}

fun getLevelIcon(level: Int): String {
    var str = ""
    for (i in 0 until level) {
        str = "$str--"
    }
    return str
}
