package wikibook.learnandroid.quizquiz

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.lang.Exception

class QuizResultFragment: Fragment() {
    interface QuizResultListener{ fun onRetry() }
    lateinit var listener: QuizResultListener

    lateinit var pref: SharedPreferences


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(parentFragment is QuizResultFragment.QuizResultListener) {
            listener = parentFragment as QuizResultListener
        }
        else {
            throw Exception("QuizResultListener 미구현")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.quiz_result_fragment, container, false)

        val correctCount = arguments?.getInt("correctCount")
        val totalQuizCount = arguments?.getInt("totalQuizCount")
        view.findViewById<TextView>(R.id.score).text = "${correctCount} / ${totalQuizCount}"

        val correctRate = correctCount!!.toDouble() / totalQuizCount!!.toDouble()
        val resultText = view.findViewById<TextView>(R.id.result_text)
        var ratingStarNum: Int

        resultText.text = when {
            correctRate == 1.0 -> {
                ratingStarNum = 5
                "Perfect"
            }
            correctRate >= 0.7 -> {
                ratingStarNum = 4
                "Excellent"
            }
            correctRate >= 0.5 -> {
                ratingStarNum = 3
                "Good"
            }
            else -> {
                ratingStarNum = 2
                "Not Bad"
            }
        }

        view.findViewById<RatingBar>(R.id.score_star).rating = ratingStarNum.toFloat()

        view.findViewById<Button>(R.id.retry).setOnClickListener {
            listener.onRetry()
        }

        pref = context!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()

        editor.putInt("correctCount", correctCount)
        editor.putInt("totalQuizCount", totalQuizCount)
        editor.commit()


        return view
    }

    companion object {
        fun newInstance(correctCount: Int, totalQuizCount: Int) : QuizResultFragment {
            val fragment = QuizResultFragment()

            val args = Bundle()
            args.putInt("correctCount", correctCount)
            args.putInt("totalQuizCount", totalQuizCount)
            fragment.arguments = args

            return fragment
        }
    }
}