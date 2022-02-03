package wikibook.learnandroid.servicestudy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BroadcastReceiverForService(val service: MyStartedService) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action == "GENERATE_RANDOM_NUMBER") {
            val broadcastIntent = Intent("SEND_RANDOM_NUMBER")
            broadcastIntent.putExtra("num", service.getRandomNum(service.from ?: 0, service.until ?: 100))

            p0?.sendBroadcast(broadcastIntent)
        }
    }

}