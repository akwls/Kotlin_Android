package wikibook.learnandroid.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi

class PomodoroActivity : AppCompatActivity() {
    lateinit var remainTime : TextView
    lateinit var receiver : BroadcastReceiver

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        remainTime = findViewById(R.id.remain_time)

        findViewById<Button>(R.id.pomodoro_timer_start).setOnClickListener {
            val cancelIntent = Intent(PomodoroService.ACTION_ALARM_CANCEL)
            sendBroadcast(cancelIntent)

            val dialog = PomodoroTimeSelectFragment()
            dialog.show(supportFragmentManager, "pomodoro_time_select_dialog")

            remainTime.setTextColor(getColor(R.color.purple_200))
        }

        findViewById<Button>(R.id.pomodoro_timer_cancel).setOnClickListener {
            val cancelIntent = Intent(PomodoroService.ACTION_ALARM_CANCEL)
            sendBroadcast(cancelIntent)

            remainTime.text = "-"

            remainTime.setTextColor(getColor(R.color.purple_200))
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val action = p1?.action

                if(action == PomodoroService.ACTION_REMAIN_TIME_NOTIFY) {
                    val remainInSec = intent.getLongExtra("count", 0) / 1000
                    remainTime.text = "${remainInSec / 60}:${String.format("%02d", remainInSec % 60)}"

                    if(remainInSec <= 10) {
                        remainTime.setTextColor(getColor(R.color.purple_700))
                    }
                }
            }
        }

        val filter = IntentFilter()
        filter.addAction(PomodoroService.ACTION_REMAIN_TIME_NOTIFY)
        registerReceiver(receiver, filter)

    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }
}