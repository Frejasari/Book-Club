package de.frejasundalexchat.dritterchat.bookOverview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.frejasundalexchat.dritterchat.R
import de.frejasundalexchat.dritterchat.db.ObjectBox
import de.frejasundalexchat.dritterchat.db.model.Book
import de.frejasundalexchat.dritterchat.editBook.EditBookActivity
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.boxFor
import io.objectbox.reactive.DataSubscriptionList

const val BOOK_ID = "BOOK_ID"

class BookOverviewFragment : Fragment() {

    companion object {
        fun newInstance() = BookOverviewFragment()
    }

    private lateinit var viewModel: BookOverviewViewModel

    private val dataSubscriptionList = DataSubscriptionList()

    private val bookBox = ObjectBox.get().boxFor(Book::class)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.book_overview_fragment, container, false)

        val bookRecyclerView = view.findViewById<RecyclerView>(R.id.bookRecyclerView)
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
        viewModel = ViewModelProviders.of(this).get(BookOverviewViewModel::class.java)
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
                R.layout.book_list_item,
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
        holder.bookView.setOnClickListener { onBookClick(book.id) }
        holder.title.text = book.title
        if (book.author.isEmpty()) {
            holder.author.text = "No Author"
        } else {
            holder.author.text = book.author
        }
        holder.totalPages.text = "${book.currentPage} / ${book.totalPages}"
        if (!book.coverUrl.isNullOrBlank()) {
            Picasso.get().load(book.coverUrl).fit().centerCrop().into(holder.cover)
        } else {
            holder.cover.setImageDrawable(null)
        }
    }

}

class BookItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.bookTitle)
    val author: TextView = view.findViewById(R.id.bookAuthor)
    val totalPages: TextView = view.findViewById(R.id.totalPageCount)
    val cover: ImageView = view.findViewById(R.id.bookCover)
    val bookView: ConstraintLayout = view.findViewById(R.id.book)
}
