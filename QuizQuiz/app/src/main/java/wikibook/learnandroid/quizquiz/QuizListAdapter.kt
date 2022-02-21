package wikibook.learnandroid.quizquiz

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import wikibook.learnandroid.quizquiz.database.Quiz

class QuizListAdapter(private val dataList: List<Quiz>) : RecyclerView.Adapter<QuizListAdapter.ItemViewHolder>() {
    class ItemViewHolder(val view: View) :RecyclerView.ViewHolder(view) {
        lateinit var quiz: Quiz
        val quizQuestion = view.findViewById<TextView>(R.id.question)

        init {
            view.setOnClickListener {
                val intent = Intent(it.context, QuizManageActivity::class.java)
                intent.putExtra("mode", "modify")
                intent.putExtra("quiz", quiz)
                it.context.startActivity(intent)
            }
        }

        fun bind(q: Quiz) {
            this.quiz = q
            quizQuestion.text = q.question
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(dataList[position])

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.quiz_list_item
    }


}