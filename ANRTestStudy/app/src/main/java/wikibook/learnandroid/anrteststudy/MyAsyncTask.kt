package wikibook.learnandroid.anrteststudy

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.example.anrteststudy.R
import java.lang.Exception

class MyAsyncTask(val activity: Activity) : AsyncTask<String, Int, String>() {
    lateinit var progressBar: ProgressBar
    lateinit var completedTask: TextView
    lateinit var resultText: TextView
    private var completedTaskCount = 0

    override fun onPreExecute() {
        super.onPreExecute()
        Log.d("mytag", "onPreExecute : ${Thread.currentThread().name}")

        progressBar = activity.findViewById(R.id.progress)
        completedTask = activity.findViewById(R.id.completed_task)
        resultText = activity.findViewById(R.id.result_text)

        progressBar.visibility = View.VISIBLE
        completedTask.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg p0: String?): String {
        var ret = ""

        if(p0.isNotEmpty()) {
            for(s in p0) {
                for((idx, c) in s!!.withIndex()) {
                    Thread.sleep(250)
                    var progressCurrent = (((idx + 1).toDouble() / s.length.toDouble()) * 100).toInt()
                    Log.d("mytag", "doInBackground : ${Thread.currentThread().name}")
                    publishProgress(progressCurrent)
                }
                ret += s.reversed() + " "
            }
            return ret
        }
        else {
            throw Exception("처리할 작업이 없습니다.")
        }

    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        Log.d("mytag", "onProgressUpdate : ${Thread.currentThread().name}")

        if(values[0]!! == 100) {
            completedTask.text = "${completedTaskCount+1}개의 작업이 완료되었습니다."
            completedTaskCount++
        }
        progressBar.progress = values[0]!!
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        Log.d("mytag", "onPostExecute : ${Thread.currentThread().name}")

        progressBar.visibility = View.GONE
        completedTask.text = "모든 작업이 완료되었습니다. (총 ${completedTaskCount}개의 작업 수행)"
        resultText.text = result
        resultText.visibility = View.VISIBLE

    }

}