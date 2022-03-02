package wikibook.learnandroid.todayquote

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.learnandroid.todayquote.R
import wikibook.learnandroid.todayquote.database.Quote
import wikibook.learnandroid.todayquote.database.QuoteDatabase
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class QuoteMainActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences
    lateinit var db: QuoteDatabase
    lateinit var quoteText: TextView
    lateinit var quoteFrom: TextView

    var quotes = mutableListOf<Quote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quote_main_activity)

        db = QuoteDatabase.getInstance(this)

        pref = this.getSharedPreferences("quotes", Context.MODE_PRIVATE)

        quoteText = findViewById<TextView>(R.id.quote_text)
        quoteFrom = findViewById<TextView>(R.id.quote_from)

        initializeQuotes()

    }

    fun initializeQuotes() {
        val initialized = pref.getBoolean("initialized", false)
        if(!initialized) {
            Log.d("func", "왔다")
            AsyncTask.execute {
                val stream = assets.open("quotes.xml")

                val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

                val doc = docBuilder.parse(stream)

                val quotesFromXMLDoc = doc.getElementsByTagName("quote")

                Log.d("length", "${quotesFromXMLDoc.length}")
                for (idx in 0 until quotesFromXMLDoc.length) {
                    val e = quotesFromXMLDoc.item(idx) as Element
                    val text = e.getElementsByTagName("text").item(0).textContent
                    val from = e.getElementsByTagName("from").item(0).textContent
                    Log.d("quote_text", text)

                    quotes.add(Quote(text = text, from = from))
                }

                for (quote in quotes) {
                    Log.d("quote_insert", quote.text!!)
                    db.quoteDAO().insert(quote)
                }

                val editor = pref.edit()
                editor.putBoolean("initialized", true)
                editor.apply()

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

        else {
            Log.d("func", "안왔다")
            AsyncTask.execute {
                quotes = db.quoteDAO().getAll().toMutableList()

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
}