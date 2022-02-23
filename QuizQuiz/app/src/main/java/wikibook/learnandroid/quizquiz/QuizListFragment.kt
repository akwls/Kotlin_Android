package wikibook.learnandroid.quizquiz

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import wikibook.learnandroid.quizquiz.database.Quiz
import wikibook.learnandroid.quizquiz.database.QuizDatabase

class QuizListFragment: Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var db: QuizDatabase
    lateinit var quizzes: MutableList<Quiz>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.quiz_list_fragment, container, false)

        db = QuizDatabase.getInstance(context!!)

        AsyncTask.execute {
            quizzes = db.quizDAO().getAll().toMutableList()

            activity?.runOnUiThread {
                val layoutManager = LinearLayoutManager(activity)
                val adapter = QuizListAdapter(quizzes)

                recyclerView = view.findViewById(R.id.quiz_list)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter

                recyclerView.setHasFixedSize(true)
            }
        }

        view.findViewById<FloatingActionButton>(R.id.add_quiz).setOnClickListener {
            val intent = Intent(activity!!, QuizManageActivity::class.java)
            intent.putExtra("mode", "add")
            startActivity(intent)
        }

        return view
    }
}