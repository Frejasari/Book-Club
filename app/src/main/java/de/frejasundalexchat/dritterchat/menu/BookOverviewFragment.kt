package de.frejasundalexchat.dritterchat.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.frejasundalexchat.dritterchat.R
import de.frejasundalexchat.dritterchat.bookOverview.CreateBookActivity

class BookOverviewFragment : Fragment() {

    companion object {
        fun newInstance() = BookOverviewFragment()
    }

    private lateinit var viewModel: BookOverviewViewModel
    private lateinit var addBookButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.book_overview_fragment, container, false)

        val bookRecyclerView = view.findViewById<RecyclerView>(R.id.bookRecyclerView)
        bookRecyclerView.layoutManager = LinearLayoutManager(view.context)
        val bookListAdapter = BookListAdapter()
        bookRecyclerView.adapter = bookListAdapter

        bookListAdapter.books = listOf(
            BookItem("Buch1", "https://www.gruender.de/wp-content/uploads/2016/01/buch-schreiben.png"),
            BookItem(
                "NEXT",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Book_icon_%28closed%29_-_Blue_and_gold.svg/170px-Book_icon_%28closed%29_-_Blue_and_gold.svg.png"
            ),
            BookItem("DANACH", "https://lorempixel.com/400/200/"),
            BookItem("DANACH", "https://lorempixel.com/400/200/"),
            BookItem("DANACH", "https://lorempixel.com/400/200/"),
            BookItem("DANACH", "https://lorempixel.com/400/200/"),
            BookItem("DANACH", "https://lorempixel.com/400/200/"),
            BookItem("DANACH", "https://lorempixel.com/400/200/")
        )
        bookListAdapter.notifyDataSetChanged()

        addBookButton = view.findViewById(R.id.addBookButton)
        addBookButton.setOnClickListener { showEditDialog() }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BookOverviewViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun showEditDialog() {
        context!!.startActivity(Intent(context, CreateBookActivity::class.java))
    }
}

data class BookItem(val title: String, val imgUrl: String)

class BookListAdapter : RecyclerView.Adapter<BookItemViewHolder>() {

    var books: List<BookItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        return BookItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.book_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        holder.title.text = books[position].title
        Picasso.get().load(books[position].imgUrl).into(holder.cover)
    }

}

class BookItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.bookTitle)
    val cover: ImageView = view.findViewById(R.id.bookCoverInput)
}
