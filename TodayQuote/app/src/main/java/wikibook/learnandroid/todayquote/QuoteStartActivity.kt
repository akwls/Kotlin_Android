package wikibook.learnandroid.todayquote

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.learnandroid.todayquote.R
import wikibook.learnandroid.todayquote.database.QuoteDatabase

class QuoteStartActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences
    lateinit var db: QuoteDatabase
    lateinit var quoteText: TextView
    lateinit var quoteFrom: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quote_start)


        db = QuoteDatabase.getInstance(this)

        pref = this.getSharedPreferences("quotes", Context.MODE_PRIVATE)

        quoteText = findViewById<TextView>(R.id.quote_text)
        quoteFrom = findViewById<TextView>(R.id.quote_from)


        AsyncTask.execute {
            val quotes = db.quoteDAO().getAll()

            runOnUiThread {
                if(quotes.isNotEmpty()) {
                    val randomIdx = kotlin.random.Random.nextInt(quotes.size)
                    val randomQuote = quotes[randomIdx]
                    quoteText.text = randomQuote.text
                    quoteFrom.text = randomQuote.from
                }
                else {
                    quoteText.text = "저장된 명언이 없습니다."
                    quoteFrom.text = ""
                }

                var toQuoteEditListButton = findViewById<Button>(R.id.quote_edit_btn)
                toQuoteEditListButton.setOnClickListener() {
                    val intent = Intent(this, QuoteEditActivity::class.java)
                    startActivity(intent)
                }
                var toQuoteListButton = findViewById<Button>(R.id.quote_list_btn)
                toQuoteListButton.setOnClickListener() {
                    val intent = Intent(this, QuoteListActivity::class.java)
                    intent.putExtra("quote_size", quotes.size)
                    startActivity(intent)
                }
            }
        }




    }
}