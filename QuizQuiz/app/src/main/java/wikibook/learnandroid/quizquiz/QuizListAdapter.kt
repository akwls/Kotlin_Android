package wikibook.learnandroid.quizquiz

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import wikibook.learnandroid.quizquiz.database.Quiz

class QuizListAdapter(private val dataList: List<Quiz>, private val fragment: Fragment) : RecyclerView.Adapter<QuizListAdapter.ItemViewHolder>() {
    class ItemViewHolder(val view: View, val fragment: Fragment) :RecyclerView.ViewHolder(view) {
        lateinit var quiz: Quiz
        val quizQuestion = view.findViewById<TextView>(R.id.question)

        init {
            view.setOnClickListener {
                val intent = Intent(it.context, QuizManageActivity::class.java)
                intent.putExtra("mode", "modify")
                intent.putExtra("quiz", quiz)
                intent.putExtra("position", adapterPosition)
               fragment.startActivityForResult(intent, 1)
            }
        }

        fun bind(q: Quiz) {
            this.quiz = q
            quizQuestion.text = q.question
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return ItemViewHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(dataList[position])

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.quiz_list_item
    }


}