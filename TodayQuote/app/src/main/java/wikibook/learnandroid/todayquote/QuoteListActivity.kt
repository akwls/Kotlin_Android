package wikibook.learnandroid.todayquote

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learnandroid.todayquote.R
import wikibook.learnandroid.todayquote.database.QuoteDatabase

class QuoteListActivity : AppCompatActivity() {
    lateinit var db: QuoteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quote_list_acitivity)

        db = QuoteDatabase.getInstance(this)

        val currentQuotesSize = intent.getIntExtra("quote_size", 0)
        Toast.makeText(this, "현재 ${currentQuotesSize}개의 명언이 저장되어 있습니다.", Toast.LENGTH_SHORT).show()


        AsyncTask.execute {
            val quotes = db.quoteDAO().getAll().toList()

            val layoutManager = LinearLayoutManager(this)

            Log.d("getAll", quotes.toString())

            val adapter = QuoteAdapter(quotes)

            val recyclerView = findViewById<RecyclerView>(R.id.quote_list)
            recyclerView.setHasFixedSize(false)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }

    }
}