package wikibook.learnandroid.pomodoro

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment

class PomodoroTimeSelectFragment : DialogFragment() {
    lateinit var timeSelectView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)

        timeSelectView = LayoutInflater.from(context!!).inflate(R.layout.pomodoro_time_select_dialog, null)
        val timeSelect = timeSelectView.findViewById<LinearLayout>(R.id.time_select)

        val listener = View.OnClickListener {
            val sec = it.tag.toString().toLong()
            startPomodoro(sec)
        }

        val times = activity?.getSharedPreferences(SettingFragment.SETTING_PREF_FILENAME, Context.MODE_PRIVATE)?.getString("preset_times","5,10,15,20,25,30")

        times?.split(",")?.forEach {
            val time = it.trim()
            val btn = Button(activity)

            btn.setText("${time}분")
            btn.tag = "${time.toInt() * 60}"

            btn.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            btn.setOnClickListener(listener)

            timeSelect.addView(btn)
        }

        builder.setView(timeSelectView)
            .setPositiveButton("시작") { _, _ ->
                var time = timeSelectView.findViewById<EditText>(R.id.manual_time_select).text.toString().toLong()

                startPomodoro(time)
            }
            .setNegativeButton("취소") { _, _ ->
                dismiss()
            }
        return builder.create()
    }

    private fun startPomodoro(delay: Long) {
        if(!(delay <= 0)) {
            activity?.let {
                val i = Intent(it, PomodoroService::class.java)
                i.putExtra("delayTimeInSec", delay.toInt())
                i.putExtra("startTime", System.currentTimeMillis())

                i.putExtra("notifyMethod", it.getSharedPreferences(SettingFragment.SETTING_PREF_FILENAME, Context.MODE_PRIVATE)?.getString("notify_method", "vibration"))
                i.putExtra("volume", it.getSharedPreferences(SettingFragment.SETTING_PREF_FILENAME, Context.MODE_PRIVATE)?.getInt("volume", 50))
                i.putExtra("vibrateTime", it.getSharedPreferences(SettingFragment.SETTING_PREF_FILENAME, Context.MODE_PRIVATE)?.getString("vibrate_time", "3")?.toLong())
                i.putExtra("timeFormat", it.getSharedPreferences(SettingFragment.SETTING_PREF_FILENAME, Context.MODE_PRIVATE)?.getString("time_format", "h:mm:ss"))

                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    it.startForegroundService(i)
                }
                else {
                    it.startService(i)
                }
                dismiss()

            }
        }
    }


}