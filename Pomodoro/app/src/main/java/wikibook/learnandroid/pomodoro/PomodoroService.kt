package wikibook.learnandroid.pomodoro

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.*

class PomodoroService : Service() {

    companion object {
        val ALARM_CHANNEL_NAME = "뽀모도로 알람"
        val ACTION_ALARM_CANCEL = "wikibook.learnandroid.pomodoro.ACTION_ALARM_CANCEL"
        val ACTION_ALARM = "wikibook.learnandroid.pomodoro.ACTION_ALARM"
        val ACTION_REMAIN_TIME_NOTIFY = "wikibook.learnandroid.pomodoro.ACTION_SEND_COUNT"
    }

    lateinit var timer : Timer

    var delayTimeInSec : Int = 0
    var startTime: Long = 0
    var endTime: Long = 0

    lateinit var vibrator: Vibrator
    lateinit var receiver: BroadcastReceiver
    lateinit var alarmBroadcastIntent: PendingIntent

    override fun onBind(intent: Intent): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        delayTimeInSec = intent!!.getIntExtra("delayTimeInSec", 0)
        startTime = intent!!.getLongExtra("startTime", 0)
        endTime = startTime + (delayTimeInSec * 1000)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationInMs : Long = 1000 * 3

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // PendingIntent : 현재가 아닌 미래 시점에 전달한 인텐트
        // 0 : 펜딩 인텐트 별 고유 정수값
        // FLAG_ONE_SHOT : 펜딩 인텐트가 일회용으로 사용되도록
        alarmBroadcastIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_ALARM), PendingIntent.FLAG_ONE_SHOT)


        val delay = 1000 * delayTimeInSec

        // setExactAndAllowWhileIdle : 정확한 시점에 도즈 모드에 영향받지 않고 알림 작업 설정
        // API 23 이상인 경우 도즈 모드에 영향을 받게 됨.
        // RTC_WAKEUP : 알림 시간 설정하는 방식 지정. 인자로 전달된 시간을 기준으로 알람이 동작
        // System.currentTimeMillis() + delay : 알람이 발생할 시간 설정.
        // alarmBroadcastIntent 알람이 발생하는 시점에 전달될 펜딩 인텐트 객체
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, alarmBroadcastIntent)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val action = p1!!.action
                when(action) {
                    ACTION_ALARM -> {
                        // 3초 동안 알람 진동.
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(vibrationInMs, VibrationEffect.DEFAULT_AMPLITUDE))
                        }
                        else {
                            vibrator.vibrate(vibrationInMs)
                        }
                        stopSelf()
                    }
                    ACTION_ALARM_CANCEL -> stopSelf()
                }
            }

        }

        val filter = IntentFilter()
        filter.addAction(ACTION_ALARM)
        filter.addAction(ACTION_ALARM_CANCEL)

        registerReceiver(receiver, filter)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널 생성은 API 26 부터 지원.
        // 알림 채널을 통해 한 앱에서 제공하는 여러 알림에 대해 알림 여부, 알림 방식 등을 조절할 수 있음.
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(ALARM_CHANNEL_NAME, "뽀모도로 상태 알림 채널", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        var builder: NotificationCompat.Builder
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = NotificationCompat.Builder(this, ALARM_CHANNEL_NAME)
        }
        else {
            builder = NotificationCompat.Builder(this)
        }

        val notification = builder.setContentTitle("뽀모도로 시작")
            .setContentText("시작됨")
            .setSmallIcon(R.drawable.ic_tomato) // 반드시 호출해야 함.
            .setOnlyAlertOnce(true)
            .build()

        // 서비스를 포어그라운드 서비스로 시작하도록 조정.
        // 생성한 알림 객체 전달.
        startForeground(1, notification)

        startRemainTimeNotifyTimer()

        return Service.START_NOT_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmBroadcastIntent)

        unregisterReceiver(receiver)
    }


    fun startRemainTimeNotifyTimer() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val diff = ((endTime - System.currentTimeMillis()) / 1000) * 1000
                val i = Intent(ACTION_REMAIN_TIME_NOTIFY)
                i.putExtra("count", diff)
                sendBroadcast(i)
            }
        }, 0, 1000)
    }

    fun cancelRemainTimeNotifyTimer() {
        timer?.cancel()
    }
}