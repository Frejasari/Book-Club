package de.frejasundalexchat.dritterchat.add_book

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import de.frejasundalexchat.dritterchat.R
import de.frejasundalexchat.dritterchat.db.ObjectBox
import de.frejasundalexchat.dritterchat.db.model.Book
import de.frejasundalexchat.dritterchat.error.ValidationError
import io.objectbox.kotlin.boxFor
import org.threeten.bp.LocalDateTime
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


const val TITLE = "book title"
const val COVERURI = "book cover url"

class AddBookFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 19

    private var coverUri: String = ""

    private lateinit var titleInput: EditText
    private lateinit var authorInput: EditText
    private lateinit var totalPagesInput: EditText
    private lateinit var currentPageInput: EditText
    private lateinit var notesInput: EditText
    private lateinit var saveButton: Button
    private lateinit var abortButton: Button
    private lateinit var selectPictureButton: View
    private lateinit var coverImagePreview: ImageView
    private lateinit var backArrow: View

    private val bookBox = ObjectBox.get().boxFor(Book::class)

    private var currentPhotoPath: String? = null

    companion object {
        fun newInstance(): AddBookFragment {
            val fragment = AddBookFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            coverUri = it.getString(COVERURI) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_book_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleInput = view.findViewById(R.id.AddBookFragment_titleInput)
        authorInput = view.findViewById(R.id.AddBookFragment_authorInput)
        totalPagesInput = view.findViewById(R.id.AddBookFragment_totalPagesInput)
        currentPageInput = view.findViewById(R.id.AddBookFragment_currentPageInput)
        notesInput = view.findViewById(R.id.AddBookFragment_notesInput)
        saveButton = view.findViewById(R.id.AddBookFragment_saveBookButton)
        abortButton = view.findViewById(R.id.AddBookFragment_abortButton)
        selectPictureButton = view.findViewById(R.id.coverImagePreview)
        coverImagePreview = view.findViewById(R.id.coverImagePreview)
        backArrow = view.findViewById(R.id.AddBookFragment_backArrow)

        selectPictureButton.setOnClickListener(this::onSelectPicture)
        abortButton.setOnClickListener { activity?.finish() }
        backArrow.setOnClickListener { activity?.finish() }
        saveButton.setOnClickListener(this::onSave)

        loadAndShowCoverImage()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(COVERURI, coverUri)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val file = File(currentPhotoPath)
            coverUri = Uri.fromFile(file).toString()
            loadAndShowCoverImage()
        }
    }

    private fun loadAndShowCoverImage() {
        if (coverUri.isNotBlank()) {
            Picasso.get().load(coverUri).fit().centerCrop().into(coverImagePreview)
        }
    }

    private fun onSave(saveButton: View) {
        val errors = this.validateInput()
        if (errors.isEmpty()) {
            bookBox.put(
                Book(
                    0, // new Object, initialized with 0
                    LocalDateTime.now().toString(),
                    title = titleInput.text.toString(),
                    author = authorInput.text.toString(),
                    currentPage = currentPageInput.getTextAsInt(),
                    pageCount = totalPagesInput.getTextAsInt(),
                    notes = notesInput.text.toString(),
                    coverUrl = coverUri
//                    listOf(notesInput.text.toString())
                )
            )
            activity?.finish()
        } else {
            errors.forEach { view!!.findViewById<TextInputLayout>(it.viewId).error = it.message }
        }
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "de.frejasundalexchat.dritterchat.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun onSelectPicture(button: View) {
        dispatchTakePictureIntent()
    }

    private fun validateInput(): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        if (titleInput.text.isBlank()) {
            errors.add(ValidationError(R.id.EditBookFragment_bookTitleInputLayout, "Please set a title."))
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

fun EditText.getTextAsInt(): Int {
    return if (text.toString().isBlank()) 0
    else text.toString().toInt()
}

fun getLevelIcon(level: Int): String {
    var str = ""
    for (i in 0 until level) {
        str = "$str--"
    }
    return str
}
