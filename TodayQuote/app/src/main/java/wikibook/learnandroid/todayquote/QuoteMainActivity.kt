package wikibook.learnandroid.todayquote

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.learnandroid.todayquote.R
import wikibook.learnandroid.todayquote.database.Quote
import wikibook.learnandroid.todayquote.database.QuoteDatabase
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class QuoteMainActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences
    lateinit var db: QuoteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quote_main_activity)

        db = QuoteDatabase.getInstance(this)

        pref = this.getSharedPreferences("quotes", Context.MODE_PRIVATE)

        if(pref.getBoolean("initialized", false)) {
            initializeQuotes()

            val editor = pref.edit()
            editor.putBoolean("initialized", true)
            editor.commit()
        }
        else {
            Log.d("func", "안왔다")
        }


        findViewById<Button>(R.id.quote_start).setOnClickListener {
            var intent = Intent(this, QuoteStartActivity::class.java)
            startActivity(intent)
        }

    }

    fun initializeQuotes() {
        Log.d("intializedFunc", "왔다")
        AsyncTask.execute {
            val stream = assets.open("quotes.xml")

            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

            val doc = docBuilder.parse(stream)

            val quotesFromXMLDoc = doc.getElementsByTagName("quote")

            val quotes = mutableListOf<Quote>()

            Log.d("length", "${quotesFromXMLDoc.length}")
            for(idx in 0 until quotesFromXMLDoc.length) {
                val e = quotesFromXMLDoc.item(idx) as Element
                val text = e.getElementsByTagName("text").item(0).textContent
                val from = e.getElementsByTagName("from").item(0).textContent
                Log.d("quote_text", text)

                quotes.add(Quote(text = text, from = from))
            }

            for(quote in quotes) {
                Log.d("quote_insert", quote.text!!)
                db.quoteDAO().insert(quote)
            }
        }
    }
}