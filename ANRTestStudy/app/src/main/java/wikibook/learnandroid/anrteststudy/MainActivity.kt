package wikibook.learnandroid.anrteststudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.anrteststudy.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        setContentView(R.layout.anr_test_activity)

        val result = findViewById<TextView>(R.id.result)
        val progressStatus = findViewById<TextView>(R.id.progress_status)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val progress = findViewById<TextView>(R.id.progress)

        findViewById<Button>(R.id.btn).setOnClickListener {
            Toast.makeText(applicationContext, "Clicked!", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.anr).setOnClickListener {
            progressStatus.text = "작업을 수행 중"
            progressBar.visibility = View.VISIBLE
            Thread(Runnable {
                var sum = 0.0
                var count = 0
                for(i in 1 until 60) {
                    sum += sqrt(Random.nextDouble())
                    Thread.sleep(100)
                    runOnUiThread {
                        progress.text = "%.1f".format(((count + 1) / 60.toDouble()) * 100) + "% 완료"
                    }
                    count++
                }
                runOnUiThread {
                    result.text = sum.toString()
                    progressStatus.text = "작업 수행 완료"
                    progressBar.visibility = View.GONE
                }
            }).start()

        }
         */

        setContentView(R.layout.activity_main)
        MyAsyncTask(this).execute("Hello", "Android", "AsyncTask")



    }
}