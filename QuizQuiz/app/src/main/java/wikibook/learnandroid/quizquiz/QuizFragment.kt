package wikibook.learnandroid.quizquiz

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import wikibook.learnandroid.quizquiz.database.Quiz
import wikibook.learnandroid.quizquiz.database.QuizDatabase

class QuizFragment: Fragment(), QuizStartFragment.QuizStartListener, QuizSolveFragment.QuizSolveListener, QuizResultFragment.QuizResultListener{

    var currentQuizIdx = 0
    var correctCount = 0
    lateinit var db : QuizDatabase
    lateinit var quizList : List<Quiz>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.quiz_fragment, container, false)

        db = QuizDatabase.getInstance(context!!)

        childFragmentManager.beginTransaction().replace(R.id.fragment_container, QuizStartFragment()).commit()

        return view

    }

    override fun onQuizStart() {
        AsyncTask.execute {
            currentQuizIdx = 0
            correctCount = 0

            quizList = db.quizDAO().getAll()
            childFragmentManager.beginTransaction().replace(R.id.fragment_container, QuizSolveFragment.newInstance(quizList[currentQuizIdx])).commit()
        }
    }

    override fun onAnswerSelected(isCorrect: Boolean) {

        if(isCorrect) correctCount++
        currentQuizIdx++

        if(currentQuizIdx == quizList.size) {
            childFragmentManager.beginTransaction().replace(R.id.fragment_container, QuizResultFragment.newInstance(correctCount, quizList.size)).commit()
        }
        else {
            childFragmentManager.beginTransaction().replace(R.id.fragment_container, QuizSolveFragment.newInstance(quizList[currentQuizIdx])).commit()
        }
    }

    override fun onRetry() {
        childFragmentManager.beginTransaction().replace(R.id.fragment_container, QuizStartFragment()).commit()
    }
}