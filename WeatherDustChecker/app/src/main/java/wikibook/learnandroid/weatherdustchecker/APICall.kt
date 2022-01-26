package wikibook.learnandroid.weatherdustchecker

import android.os.AsyncTask
import java.net.HttpURLConnection
import java.net.URL

class APICall(val callback: APICallback)  : AsyncTask<URL, Void, String>() {
    // 구현체에서 메소드를 재정의할 수 있도록 인터페이스 생성
    interface APICallback {
        fun onComplete(result: String)
    }

    // UI 작업을 벗어나 독립적으로 실행됨. 종료 후 onPostExecute 실행
    override fun doInBackground(vararg p0: URL?): String {
        val url = p0.get(0)
        val conn : HttpURLConnection = url?.openConnection() as HttpURLConnection
        conn.connect()

        var body = conn?.inputStream.bufferedReader().use { it.readText() }

        conn.disconnect()

        return body
    }

    // doInBackground가 반환한 내용을 매개변수로 가져옴.
    // UI 작업으로 돌아옴
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        if(result != null) {
            callback.onComplete(result)
        }
    }

}