package wikibook.learnandroid.todayquote

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learnandroid.todayquote.R
import wikibook.learnandroid.todayquote.database.Quote
import wikibook.learnandroid.todayquote.database.QuoteDatabase

class QuoteEditActivity : AppCompatActivity() {
    lateinit var db: QuoteDatabase
    lateinit var quotes: MutableList<Quote>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quote_edit_acitivity)

        db = QuoteDatabase.getInstance(this)

        AsyncTask.execute {
            quotes = db.quoteDAO().getAll().toMutableList()

            val editQuotes = mutableListOf<Quote>()
            for(i in 0 until 20) {
                editQuotes.add(Quote("", ""))
            }
            for((idx, q) in quotes.withIndex()) {
                editQuotes[idx].text = q.text
                editQuotes[idx].from = q.from
            }

            val layoutManager = LinearLayoutManager(this)
            val adapter = QuoteEditAdapter(editQuotes, this)

            val recyclerView = findViewById<RecyclerView>(R.id.quote_edit_list)
            recyclerView.setHasFixedSize(false)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
        }





    }
}