package wikibook.learnandroid.todayquote

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.learnandroid.todayquote.R
import wikibook.learnandroid.todayquote.database.Quote
import wikibook.learnandroid.todayquote.database.QuoteDatabase

class QuoteEditAdapter(private val datalist: List<Quote>, val context: Context): RecyclerView.Adapter<QuoteEditAdapter.QuoteItemViewHolder>() {
    class QuoteItemViewHolder(val view: View, val context: Context, val size: Int) : RecyclerView.ViewHolder(view) {
        lateinit var quote: Quote
        val quoteTextEdit = view.findViewById<EditText>(R.id.quote_text_edit)
        val quoteFromEdit = view.findViewById<EditText>(R.id.quote_from_edit)
        val quoteDeleteBtn = view.findViewById<Button>(R.id.quote_delete_btn)
        val quoteModifyBtn = view.findViewById<Button>(R.id.quote_modify_btn)
        val db = QuoteDatabase.getInstance(context)

        init {
            quoteDeleteBtn.setOnClickListener() {
                AsyncTask.execute {
                    db.quoteDAO().delete(quote)
                }
                quoteTextEdit.setText("")
                quoteFromEdit.setText("")

                Toast.makeText(it.context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }

            quoteModifyBtn.setOnClickListener() {

                val newQuoteText = quoteTextEdit.text.toString()
                val newQuoteFrom = quoteFromEdit.text.toString()

                AsyncTask.execute {
                    if(adapterPosition > size) {
                        db.quoteDAO().insert(Quote(newQuoteText, newQuoteFrom))
                    }
                    else db.quoteDAO().update(Quote(newQuoteText, newQuoteFrom))
                }

                Toast.makeText(it.context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(q: Quote) {
            quote = q
            quoteTextEdit.setText(quote.text)
            quoteFromEdit.setText(quote.from)
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuoteItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return QuoteItemViewHolder(view, context, datalist.size)
    }

    override fun onBindViewHolder(holder: QuoteItemViewHolder, position: Int) {
        holder.bind(datalist[position])
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.quote_edit_list_item
    }

}