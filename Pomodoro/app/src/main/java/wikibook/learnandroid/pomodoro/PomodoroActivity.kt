package wikibook.learnandroid.pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class PomodoroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        findViewById<Button>(R.id.pomodoro_timer_start).setOnClickListener {
            val cancelIntent = Intent(PomodoroService.ACTION_ALARM_CANCEL)
            sendBroadcast(cancelIntent)

            val i = Intent(this, PomodoroService::class.java)
            i.putExtra("delayTimeInSec", 60 * 1)
            i.putExtra("startTime", System.currentTimeMillis())

            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(i)
            }
            else {
                startService(i)
            }
        }

        findViewById<Button>(R.id.pomodoro_timer_cancel).setOnClickListener {
            val cancelIntent = Intent(PomodoroService.ACTION_ALARM_CANCEL)
            sendBroadcast(cancelIntent)
        }
    }
}