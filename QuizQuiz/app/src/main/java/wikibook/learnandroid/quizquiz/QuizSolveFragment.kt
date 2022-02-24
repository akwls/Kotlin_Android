package wikibook.learnandroid.quizquiz

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.fragment.app.Fragment
import wikibook.learnandroid.quizquiz.database.Quiz
import java.lang.Exception
import java.util.*

class QuizSolveFragment:Fragment() {
    interface QuizSolveListener { fun onAnswerSelected(isCorrect: Boolean) }
    lateinit var listener: QuizSolveListener
    lateinit var quiz: Quiz

    var answerSelected: Boolean = false

    val ANIM_DURATION: Long = 250L

    lateinit var timer: Timer
    val MAX_REMAIN_TIME = 10 * 1000
    var remainTime = MAX_REMAIN_TIME

    lateinit var soundPool : SoundPool
    var soundVolume: Float = 0.5f
    var correctAnswerSoundId :Int = 0
    var incorrectAnswerSoundId: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(parentFragment is QuizSolveFragment.QuizSolveListener) {
            listener = parentFragment as QuizSolveListener
        }
        else {
            throw Exception("QuizSolveListener 미구현")
        }
    }

    companion object {
        fun newInstance(quiz: Quiz, currenntQuizIdx: Int, totalQuizCount: Int) : QuizSolveFragment {
            val fragment = QuizSolveFragment()

            val args = Bundle()
            args.putParcelable("quiz", quiz)
            args.putInt("currentQuizIdx", currenntQuizIdx)
            args.putInt("totalQuizCount", totalQuizCount)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.quiz_solve_fragment, container, false)

        val remainTimeBar = view.findViewById<ProgressBar>(R.id.remain_time_bar)

        soundPool = SoundPool.Builder().build()
        correctAnswerSoundId = soundPool.load(context, R.raw.correct, 1)
        incorrectAnswerSoundId = soundPool.load(context, R.raw.incorrect, 1)

        val currenntQuizIdx = arguments?.getInt("currentQuizIdx")
        val totalQuizCount = arguments?.getInt("totalQuizCount")
        view.findViewById<TextView>(R.id.quiz_progress).text = "${currenntQuizIdx}/${totalQuizCount}"

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                remainTime -= 1000
                remainTimeBar.progress = ((remainTime / MAX_REMAIN_TIME.toDouble()) * 100).toInt()

                if(remainTime <= 0) {
                    timer.cancel()
                    soundPool.play(incorrectAnswerSoundId, soundVolume, soundVolume, 1, 0, 1f)
                    activity?.runOnUiThread { listener.onAnswerSelected(false) }
                }
            }
        }, 0, 1000)

        quiz = arguments?.getParcelable("quiz")!!
        view.findViewById<TextView>(R.id.question).text = quiz.question

        val choices =  view.findViewById<ViewGroup>(R.id.choices)

        val answerSelectListener = View.OnClickListener {

            if(!answerSelected) {
                timer.cancel()
                val guess = (it as Button).text.toString()

                val transition = it.background as TransitionDrawable
                transition.startTransition(ANIM_DURATION.toInt())

                val image = view.findViewById<ImageView>(R.id.answer_feedback_image)

                if(quiz.answer == guess) {
                    soundPool.play(correctAnswerSoundId, soundVolume, soundVolume, 1, 0, 1f)
                    image.setImageResource(R.drawable.ic_happy)

                }
                else {
                    soundPool.play(incorrectAnswerSoundId, soundVolume, soundVolume, 1, 0, 1f)
                    image.setImageResource(R.drawable.ic_unhappy)
                }

                val imageAlphaAnimator = ObjectAnimator.ofFloat(image, "alpha", 0.0F, 1.0F)
                val imageScaleXAnimator = ObjectAnimator.ofFloat(image, "scaleX", 1.0F, 1.5F)
                val imageScaleYAnimator = ObjectAnimator.ofFloat(image, "scaleY", 1.0F, 1.5F)

                val animatorSet = AnimatorSet()
                animatorSet.playTogether(imageAlphaAnimator, imageScaleXAnimator, imageScaleYAnimator)
                animatorSet.duration = ANIM_DURATION
                animatorSet.interpolator = LinearInterpolator()
                animatorSet.start()

                Timer().schedule(object: TimerTask() {
                    override fun run() {
                        activity?.runOnUiThread {
                            listener.onAnswerSelected(quiz.answer == guess)
                        }
                    }
                }, 1500)
            }

            answerSelected = true
        }

        when {
            quiz.type == "ox" -> {
                for(sign in listOf("o", "x")) {
                    var btn = inflater.inflate(R.layout.answer_choice_button, choices, false) as Button
                    btn.text = sign
                    btn.setOnClickListener(answerSelectListener)
                    choices.addView(btn)
                }
            }
            quiz.type == "multiple_choice" -> {
                for(guess in quiz.guesses!!) {
                    var btn = inflater.inflate(R.layout.answer_choice_button, choices, false) as Button
                    btn.text = guess
                    btn.isAllCaps = false
                    btn.setOnClickListener(answerSelectListener)
                    choices.addView(btn)
                }
            }
        }
        return view
    }
}