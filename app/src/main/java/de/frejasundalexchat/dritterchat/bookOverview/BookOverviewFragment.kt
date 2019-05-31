package de.frejasundalexchat.dritterchat.bookOverview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.frejasundalexchat.dritterchat.R
import de.frejasundalexchat.dritterchat.db.ObjectBox
import de.frejasundalexchat.dritterchat.db.model.Book
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.boxFor
import io.objectbox.reactive.DataSubscriptionList


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
        val bookListAdapter = BookListAdapter()
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BookOverviewViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        dataSubscriptionList.cancel()
        super.onDestroyView()
    }
}

class BookListAdapter : RecyclerView.Adapter<BookItemViewHolder>() {

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
        holder.title.text = book.title
        if (book.author.isEmpty()) {
            holder.author.text = "No Author"
        } else {
            holder.author.text = book.author
        }
        holder.totalPages.text = "0 / ${book.pageCount}"
        if (!book.imgUrl.isNullOrBlank()) {
            Picasso.get().load(book.imgUrl).fit().centerCrop().into(holder.cover)
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
}
