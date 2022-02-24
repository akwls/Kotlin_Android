package wikibook.learnandroid.quizquiz

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
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
        if(mode == "modify") {
            quiz = intent.getParcelableExtra<Quiz>("quiz")!!
            findViewById<Button>(R.id.confirm).text = "퀴즈 수정"
        }
        else {
            quiz = Quiz(type="ox", question ="", answer = "o", category = "")
            findViewById<Button>(R.id.confirm).text = "퀴즈 추가"
        }

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

                var validationFail = false
                var reason: String = ""

                if(quiz.question!!.isBlank()) {
                    validationFail = true
                    reason = "문두가 있어야 합니다."
                }

                if(quiz.type == "ox") {
                    val answerShouldOorX = ((quiz.answer == "o") || (quiz.answer == "x"))
                    if(!answerShouldOorX) {
                        validationFail = true
                        reason = "정답은 o거나 x여야 합니다."
                    }
                }
                else {
                    val guesses = mutableListOf<String>()
                    for(i in 0 until choices.childCount) {
                        val guess = ((choices.getChildAt(i) as ViewGroup).getChildAt(0) as EditText).text.toString()
                        if(guess.isNotBlank()) {
                            guesses.add(guess)
                        }
                    }

                    if(guesses.size < 2) {
                        validationFail = true
                        reason = "정상적인 내용이 포함된 2개 이상의 선지가 필요합니다."
                    }
                    else {
                        quiz.guesses = guesses
                    }

                }
                quiz.category = quiz.category?.trim()
                quiz.question = quiz.question?.trim()

                if(!validationFail) {
                    if(mode == "modify") {
                        db.quizDAO().update(quiz)
                    }
                    else {
                        val id = db.quizDAO().insert(quiz)
                        quiz.id = id
                    }

                    val resultIntent = Intent()
                    resultIntent.putExtra("operation", mode)
                    resultIntent.putExtra("quiz", quiz)

                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                else {
                    runOnUiThread { Toast.makeText(this, reason, Toast.LENGTH_SHORT).show() }
                }


            }
        }

        if(mode == "modify") {
            findViewById<Button>(R.id.delete).visibility = View.VISIBLE
            findViewById<Button>(R.id.delete).setOnClickListener {
                AsyncTask.execute {
                    db.quizDAO().delete(quiz)

                    val resultIntent = Intent()
                    resultIntent.putExtra("operation", "delete")
                    resultIntent.putExtra("quiz", quiz)
                    resultIntent.putExtra("position", intent.getIntExtra("position", -1))

                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
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
            (it as Button).setTextColor(Color.RED)
            (it as Button).setTypeface(null, Typeface.BOLD)
        }

        for(choice in listOf("o", "x")) {
            var btn = Button(this)
            btn.text = choice
            btn.setOnClickListener(listener)
            choices.addView(btn)
        }
    }

    fun changeLayoutToMultipleChoiceQuizManage() {
        quiz.type="multiple_choice"
        choices.removeAllViews()
        findViewById<Button>(R.id.add_choice).visibility = View.VISIBLE

        val guesses = quiz?.guesses ?: listOf("", "")

        val setAnswerListener = View.OnClickListener {
            quiz.answer = ((it.parent as ViewGroup).getChildAt(0) as EditText).text.toString()
            ((it.parent as ViewGroup).getChildAt(0) as EditText).setTextColor(Color.RED)
            ((it.parent as ViewGroup).getChildAt(0) as EditText).setTypeface(null, Typeface.BOLD)
        }

        val removeEditListener = View.OnClickListener {
            if(choices.childCount > 2) {
                choices.removeView(it.parent as ViewGroup)
            }
            else {
                Toast.makeText(this, "N지선다 문제는 최소한 2개의 선택지를 포함해야 합니다.", Toast.LENGTH_SHORT).show()
            }
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