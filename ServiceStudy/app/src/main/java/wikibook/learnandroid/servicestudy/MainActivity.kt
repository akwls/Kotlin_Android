package wikibook.learnandroid.servicestudy

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.Toast

class BroadCastReceiverForActivity : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action == "SEND_RANDOM_NUMBER") {
            val num = p1.getIntExtra("num", -1)

            Toast.makeText(p0, num.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}

class SystemBroadcastMessageReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        when(p1?.action) {
            Intent.ACTION_POWER_CONNECTED -> Log.d("mytag", "충전 연결")
            Intent.ACTION_POWER_DISCONNECTED -> Log.d("mytag", "충전 연결 해제")
            Intent.ACTION_HEADSET_PLUG -> Log.d("mytag", "헤드셋 연결 상태 변경")
            Intent.ACTION_SCREEN_ON -> Log.d("mytag", "화면 켜짐")
            Intent.ACTION_SCREEN_OFF -> Log.d("mytag", "화면 꺼짐")
        }
    }

}

class MainActivity : AppCompatActivity(), ServiceConnection {

    var myBinder : MyBoundService.MyBinder? = null
    lateinit var broadcastReceiver : BroadcastReceiver
    lateinit var systemBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val boundServiceIntent = Intent(this, MyBoundService::class.java)

        bindService(boundServiceIntent, this, Context.BIND_AUTO_CREATE)

        findViewById<Button>(R.id.random_num_from_bound_service).setOnClickListener {
            val randomNumber = myBinder?.service?.getRandomNum()
            Toast.makeText(this, randomNumber.toString(), Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.start_service).setOnClickListener {
            Toast.makeText(this, "서비스 시작", Toast.LENGTH_SHORT).show()

            val serviceIntent = Intent(this, MyStartedService::class.java)
            serviceIntent.putExtra("from", 1000)
            serviceIntent.putExtra("until", 2000)

            startService(serviceIntent)
        }

        findViewById<Button>(R.id.stop_service).setOnClickListener {
            Toast.makeText(this, "서비스 정지", Toast.LENGTH_SHORT).show()

            val serviceIntent = Intent(this, MyStartedService::class.java)
            stopService(serviceIntent)
        }

        findViewById<Button>(R.id.random_service_from_started_service).setOnClickListener {
            Log.d("mytag", "send broadcast")

            val broadcastIntent = Intent("GENERATE_RANDOM_NUMBER")
            sendBroadcast(broadcastIntent)
        }

        broadcastReceiver = BroadCastReceiverForActivity()

        val filter = IntentFilter()
        filter.addAction("SEND_RANDOM_NUMBER")

        registerReceiver(broadcastReceiver, filter)

        systemBroadcastReceiver = SystemBroadcastMessageReceiver()
        val systemBroadcastFilter = IntentFilter()

        systemBroadcastFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        systemBroadcastFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)

        systemBroadcastFilter.addAction(Intent.ACTION_HEADSET_PLUG)

        systemBroadcastFilter.addAction(Intent.ACTION_SCREEN_ON)
        systemBroadcastFilter.addAction(Intent.ACTION_SCREEN_OFF)

        registerReceiver(systemBroadcastReceiver, systemBroadcastFilter)

    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        Log.d("mytag", "바운드 서비스 : onServiceConnected")

        myBinder = p1 as? MyBoundService.MyBinder
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Log.d("mytag", "바운드 서비스 : onServiceDisconnected")

        myBinder = null
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("mytag", "unbindService")

        unbindService(this)

        unregisterReceiver(broadcastReceiver)

        unregisterReceiver(systemBroadcastReceiver)
    }
}