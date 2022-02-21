package wikibook.learnandroid.quizquiz

import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import wikibook.learnandroid.quizquiz.database.Quiz
import wikibook.learnandroid.quizquiz.database.QuizDatabase

class QuizManageActivity: AppCompatActivity() {
    lateinit var mode: String
    lateinit var quiz: Quiz
    lateinit var db: QuizDatabase
    lateinit var choices: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_manage_activity)

        mode = intent.getStringExtra("mode")!!
        quiz = intent.getParcelableExtra<Quiz>("quiz")!!

        db = QuizDatabase.getInstance(this)

        val spinner = findViewById<Spinner>(R.id.quiz_type)
        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.quiz_type, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner.adapter = spinnerAdapter

        val categoryEdit = findViewById<EditText>(R.id.category_edit)
        categoryEdit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                quiz.category = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        val questionEdit = findViewById<EditText>(R.id.question_edit)
        questionEdit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                quiz.question = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when {
                    p2 == 0 -> changeLayoutToOXQuizManage()
                    p2 == 1 -> changeLayoutToMultipleChoiceQuizManage()
                }
            }
        }

        choices = findViewById(R.id.choices)

        findViewById<Button>(R.id.confirm).setOnClickListener {
            AsyncTask.execute {
                if(quiz.type == "multiple_choice") {
                    val guesses = mutableListOf<String>()
                    for(i in 0 until choices.childCount) {
                        val guess = ((choices.getChildAt(i) as ViewGroup).getChildAt(0) as EditText).text.toString()
                        if(guess.isNotBlank()) {
                            guesses.add(guess)
                        }
                    }
                    quiz.guesses = guesses
                }
                quiz.category = quiz.category?.trim()
                quiz.question = quiz.question?.trim()

                db.quizDAO().update(quiz)
                finish()
            }
        }

        findViewById<Button>(R.id.delete).visibility = View.VISIBLE
        findViewById<Button>(R.id.delete).setOnClickListener {
            AsyncTask.execute {
                db.quizDAO().delete(quiz)
                finish()
            }
        }

        categoryEdit.setText(quiz.category)
        questionEdit.setText(quiz.question)

        when {
            quiz.type == "ox" -> {
                spinner.setSelection(0)
            }
            quiz.type == "multiple_choice" -> {
                spinner.setSelection(1)
            }
        }
    }

    fun changeLayoutToOXQuizManage() {
        quiz.type = "ox"
        choices.removeAllViews()

        findViewById<Button>(R.id.add_choice).visibility = View.GONE

        val listener = View.OnClickListener {
            quiz.answer = (it as Button).text.toString()
        }

        for(choice in listOf("o", "x")) {
            var btn = Button(this)
            btn.text = choice
            btn.setOnClickListener {
                choices.addView(btn)
            }
        }
    }

    fun changeLayoutToMultipleChoiceQuizManage() {
        quiz.type="multiple_choice"
        choices.removeAllViews()
        findViewById<Button>(R.id.add_choice).visibility = View.VISIBLE

        val guesses = quiz?.guesses ?: listOf("", "")

        val setAnswerListener = View.OnClickListener {
            quiz.answer = ((it.parent as ViewGroup).getChildAt(0) as EditText).text.toString()
        }
        val removeEditListener = View.OnClickListener {
            choices.removeView(it.parent as ViewGroup)
        }
        for(guess in guesses) {
            val edit = layoutInflater.inflate(R.layout.quiz_manage_multiple_choice_edit, choices, false) as ViewGroup
            (edit.getChildAt(0) as EditText).setText(guess)
            (edit.getChildAt(1) as Button).setOnClickListener(setAnswerListener)
            (edit.getChildAt(2) as Button).setOnClickListener(removeEditListener)
            choices.addView(edit)
        }

        findViewById<Button>(R.id.add_choice).setOnClickListener {
            val edit = layoutInflater.inflate(R.layout.quiz_manage_multiple_choice_edit, choices, false) as ViewGroup
            (edit.getChildAt(1) as Button).setOnClickListener(setAnswerListener)
            (edit.getChildAt(2) as Button).setOnClickListener(removeEditListener)
            choices.addView(edit)
        }
    }
}