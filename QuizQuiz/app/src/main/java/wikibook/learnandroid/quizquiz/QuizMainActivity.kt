package wikibook.learnandroid.quizquiz

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.loader.content.AsyncTaskLoader
import com.google.android.material.internal.NavigationMenu
import com.google.android.material.navigation.NavigationView
import org.w3c.dom.Element
import org.w3c.dom.Text
import wikibook.learnandroid.quizquiz.database.Quiz
import wikibook.learnandroid.quizquiz.database.QuizDatabase
import javax.xml.parsers.DocumentBuilderFactory

class QuizMainActivity : AppCompatActivity() {
    lateinit var drawerToggle: ActionBarDrawerToggle
    lateinit var db: QuizDatabase
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_main_activity)

        db = QuizDatabase.getInstance(this)

        pref = getSharedPreferences("pref", Context.MODE_PRIVATE)

        val sp : SharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        if(sp.getBoolean("initialized", true)) {
            initQuizDataFromXMLFile()

            val editor = sp.edit()
            editor.putBoolean("initialized", false)
            editor.commit()
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.drawer_nav_view)

        supportFragmentManager.beginTransaction().add(R.id.frame, QuizFragment()).commit()

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.quiz_solve -> supportFragmentManager.beginTransaction().replace(R.id.frame, QuizFragment()).commit()
                R.id.quiz_manage -> supportFragmentManager.beginTransaction().replace(R.id.frame, QuizListFragment()).commit()
            }

            drawerLayout.closeDrawers()

            true
        }

        drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                val correctCount = pref.getInt("correctCount", 0)
                val totalQuizCount = pref.getInt("totalQuizCount", 0)

                navView.getHeaderView(0).findViewById<TextView>(R.id.correct_count).text = correctCount.toString()
                navView.getHeaderView(0).findViewById<TextView>(R.id.total_quiz_count).text = totalQuizCount.toString()
                navView.getHeaderView(0).findViewById<TextView>(R.id.correct_percent).text = "${(correctCount.toDouble() / totalQuizCount * 100)}%"
            }
        }

        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun initQuizDataFromXMLFile() {
        AsyncTask.execute {
            val stream = assets.open("quizzes.xml")

            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

            val doc = docBuilder.parse(stream)

            val quizzesFromXMLDoc = doc.getElementsByTagName("quiz")

            val quizList = mutableListOf<Quiz>()
            for(idx in 0 until quizzesFromXMLDoc.length) {
                val e = quizzesFromXMLDoc.item(idx) as Element
                val type = e.getAttribute("type")
                val question = e.getElementsByTagName("question").item(0).textContent
                val answer = e.getElementsByTagName("answer").item(0).textContent
                val category = e.getElementsByTagName("category").item(0).textContent

                when {
                    type == "ox"-> {
                        quizList.add(Quiz(type=type, question = question, answer=answer, category=category))
                    }
                    type == "multiple_choice" -> {
                        val choices = e.getElementsByTagName("choice")
                        val choiceList = mutableListOf<String>()
                        for(idx in 0 until choices.length) {
                            choiceList.add(choices.item(idx).textContent)
                        }
                        quizList.add(Quiz(type=type, question = question, answer=answer, category=category, guesses = choiceList))
                    }
                }
            }
            for(quiz in quizList) {
                db.quizDAO().insert(quiz)
            }
        }
    }
}