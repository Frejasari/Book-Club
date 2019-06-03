package de.frejasundalexchat.dritterchat.library

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.frejasundalexchat.dritterchat.db.ObjectBox
import de.frejasundalexchat.dritterchat.db.model.Book
import de.frejasundalexchat.dritterchat.edit_book.EditBookActivity
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.boxFor
import io.objectbox.reactive.DataSubscriptionList


const val BOOK_ID = "BOOK_ID"

class LibraryFragment : Fragment() {

    companion object {
        fun newInstance() = LibraryFragment()
    }

    private lateinit var viewModel: LibraryViewModel

    private val dataSubscriptionList = DataSubscriptionList()

    private val bookBox = ObjectBox.get().boxFor(Book::class)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(de.frejasundalexchat.dritterchat.R.layout.library_fragment, container, false)

        val bookRecyclerView = view.findViewById<RecyclerView>(de.frejasundalexchat.dritterchat.R.id.bookRecyclerView)
        bookRecyclerView.layoutManager = LinearLayoutManager(view.context)
        val bookListAdapter = BookListAdapter(this::onBookClicked)
        bookRecyclerView.adapter = bookListAdapter
        val query = bookBox.query().build()
        query.subscribe(dataSubscriptionList)
            .on(AndroidScheduler.mainThread())
            .observer { data ->
                bookListAdapter.books = data.reversed();
                bookListAdapter.notifyDataSetChanged()
            }

        return view
    }

    private fun onBookClicked(id: Long) {
        startActivity(Intent(this.context, EditBookActivity::class.java).apply {
            putExtra(BOOK_ID, id)
        })
        Log.i("TAG<", "MAKE CLICK HANDLING")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LibraryViewModel::class.java)
    }

    override fun onDestroyView() {
        dataSubscriptionList.cancel()
        super.onDestroyView()
    }
}

class BookListAdapter(val onBookClick: (id: Long) -> Unit) : RecyclerView.Adapter<BookItemViewHolder>() {

    var books: List<Book> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        return BookItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                de.frejasundalexchat.dritterchat.R.layout.library_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        val book = books[position]

        holder.setProgress(book)
        holder.bookView.setOnClickListener { onBookClick(book.id) }
        holder.title.text = book.title
        if (book.author.isEmpty()) {
            holder.author.text = "No Author"
        } else {
            holder.author.text = book.author
        }
        holder.totalPages.text = "${book.currentPage} / ${book.pageCount}"
        if (!book.coverUrl.isNullOrBlank()) {
            Picasso.get().load(book.coverUrl).fit().centerCrop().into(holder.cover)
        } else {
            holder.cover.setImageDrawable(null)
        }
    }
}

class BookItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(de.frejasundalexchat.dritterchat.R.id.LibraryListItem_bookTitle)
    val author: TextView = view.findViewById(de.frejasundalexchat.dritterchat.R.id.LibraryListItem_bookAuthor)
    val totalPages: TextView = view.findViewById(de.frejasundalexchat.dritterchat.R.id.LibraryListItem_pageCount)
    val cover: ImageView = view.findViewById(de.frejasundalexchat.dritterchat.R.id.LibraryListItem_bookCover)
    val bookView: ConstraintLayout = view.findViewById(de.frejasundalexchat.dritterchat.R.id.book)
    private val guideline: Guideline = view.findViewById(de.frejasundalexchat.dritterchat.R.id.LibraryListItem_backgroundGuideline)

    fun setProgress(book: Book) {
        val params = guideline.layoutParams as ConstraintLayout.LayoutParams
        val percentRead: Float = book.currentPage.toFloat() / book.pageCount
        params.guidePercent = percentRead
        guideline.layoutParams = params
    }

}
