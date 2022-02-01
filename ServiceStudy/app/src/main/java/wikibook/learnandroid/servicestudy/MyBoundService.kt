package wikibook.learnandroid.servicestudy

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlin.random.Random

class MyBoundService : Service() {

    inner class MyBinder : Binder() {
        val service = this@MyBoundService
    }

    private val binder = MyBinder()

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("mytag", "service : onBind")

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("mytag", "service : onUnbind")

        return super.onUnbind(intent)
    }

    fun getRandomNum(from: Int, until: Int = 100) = Random.nextInt(from, until)

}