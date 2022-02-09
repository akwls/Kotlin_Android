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

            val dialog = PomodoroTimeSelectFragment()
            dialog.show(supportFragmentManager, "pomodoro_time_select_dialog")
        }

        findViewById<Button>(R.id.pomodoro_timer_cancel).setOnClickListener {
            val cancelIntent = Intent(PomodoroService.ACTION_ALARM_CANCEL)
            sendBroadcast(cancelIntent)
        }
    }
}