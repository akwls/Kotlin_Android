package wikibook.learnandroid.quizquiz

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
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

        setHasOptionsMenu(true)

        db = QuizDatabase.getInstance(context!!)

        AsyncTask.execute {
            quizzes = db.quizDAO().getAll().toMutableList()

            activity?.runOnUiThread {
                val layoutManager = LinearLayoutManager(activity)
                val adapter = QuizListAdapter(quizzes, this)

                recyclerView = view.findViewById(R.id.quiz_list)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter

                recyclerView.setHasFixedSize(true)
            }
        }

        view.findViewById<FloatingActionButton>(R.id.add_quiz).setOnClickListener {
            val intent = Intent(activity!!, QuizManageActivity::class.java)
            intent.putExtra("mode", "add")
            startActivityForResult(intent, 1)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val operation = data?.getStringExtra("operation")
            val quiz = data?.getParcelableExtra<Quiz>("quiz")

            if(operation == "modify") {
                for((i, q) in quizzes.withIndex()) {
                    if(quiz?.id == q.id) {
                        quizzes[i] = quiz!!
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
            else if(operation == "delete") {
                val position = data.getIntExtra("position", -1)
                if(position != -1) {
                    quizzes.removeAt(position)
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
            else {
                quizzes.add(quiz!!)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        AsyncTask.execute {
            val categories = db.quizDAO().getCategories()

            activity?.runOnUiThread {
                val categoryMenu = menu.add("전부")
                categoryMenu.setIcon(android.R.drawable.ic_menu_view)
                categoryMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

                for(c in categories) {
                    val categoryMenu = menu.add(c)
                    categoryMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                }
            }
        }
        true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        AsyncTask.execute {
            val category = item.toString()
            if(category == "전부") {
                quizzes = db.quizDAO().getAll().toMutableList()
            }
            else {
                quizzes = db.quizDAO().getAll(category).toMutableList()
            }

            activity?.runOnUiThread {
                val adapter = QuizListAdapter(quizzes, this)
                recyclerView.swapAdapter(adapter, false)
            }
        }
        return true
    }
}