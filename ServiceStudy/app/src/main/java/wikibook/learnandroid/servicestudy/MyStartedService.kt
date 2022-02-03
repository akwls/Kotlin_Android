package wikibook.learnandroid.servicestudy

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import kotlin.random.Random

class MyStartedService : Service() {
    var from : Int? = null
    var until : Int? = null

    lateinit var broadcastReceiver: BroadcastReceiver

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("mytag", "service : onCreate")

        broadcastReceiver = BroadcastReceiverForService(this)

        val filter = IntentFilter()
        filter.addAction("GENERATE_RANDOM_NUMBER")

        registerReceiver(broadcastReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("mytag", "service : onStartCommand")

        from = intent?.getIntExtra("from", 0)
        until = intent?.getIntExtra("until", 100)

        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("mytag", "service : onDestroy")

        unregisterReceiver(broadcastReceiver)
    }

    fun getRandomNum(from : Int, until: Int) = Random.nextInt(from, until)

}