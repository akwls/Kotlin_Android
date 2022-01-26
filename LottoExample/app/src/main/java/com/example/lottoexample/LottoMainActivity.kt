package com.example.lottoexample

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class LottoMainActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences
    private lateinit var lottoNumber: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lotto_main_activity)

        pref = getSharedPreferences("lotto", Context.MODE_PRIVATE)
        lottoNumber = mutableListOf()
        lottoNumber = getLottoNumbersFromPreference(pref)

        var lottoText = findViewById<TextView>(R.id.lotto_text_main)
        var numberStr = createLottoNumbers()
        Log.d("lottoNumber", numberStr)
        lottoText.text = numberStr
        lottoNumber.add(numberStr)

        var lottoSaveBtn = findViewById<Button>(R.id.lotto_save_btn)
        lottoSaveBtn.setOnClickListener() {
            lottoNumber = getLottoNumbersFromPreference(pref)
            lottoNumber.add(lottoText.text.toString())
            saveLottoToPreference(pref, lottoNumber)
        }

        var lottoCreateBtn = findViewById<Button>(R.id.lotto_create_btn)
        lottoCreateBtn.setOnClickListener() {
            lottoText.text = createLottoNumbers()
        }

        var lottoListBtn = findViewById<Button>(R.id.lotto_list_btn)
        lottoListBtn.setOnClickListener() {
            var intent = Intent(this, LottoListActivity::class.java)
            startActivity(intent)
        }

        var lottoSiteBtn = findViewById<Button>(R.id.lotto_site_btn)
        lottoSiteBtn.setOnClickListener() {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dhlottery.co.kr/gameResult.do?method=byWin&wiselog=H_C_1_1"))
            startActivity(intent)
        }

    }

    fun createLottoNumbers() : String{
        var numbers = mutableListOf<Int>()
        for(i in 1..6) {
            var num = kotlin.random.Random.nextInt(45)+1
            numbers.add(num)
            Log.d("lottoNumber", "$num")
        }

        return numbers.joinToString("-")
    }

    companion object {
        fun getLottoNumbersFromPreference(pref: SharedPreferences) : MutableList<String> {
            var index = pref.getInt("idx", 0)
            var result = mutableListOf<String>()
            for(i in 0 until index) {
                var content = pref.getString("$i", "")!!
                if(content.isNotBlank()) {
                    result.add(content)
                }
            }
            return result
        }

        fun saveLottoToPreference(pref: SharedPreferences, numbers: List<String>) {
            var index = 0
            var editor = pref.edit()
            for(i in 0 until numbers.size) {
                if(numbers[i].isNotBlank()) {
                    editor.putString("${index++}", numbers[i])
                }
            }
            editor.putInt("idx", index)
            editor.apply()
        }
    }
}