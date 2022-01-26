package com.example.lottoexample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LottoListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lotto_list_activity)

        val pref = getSharedPreferences("lotto", Context.MODE_PRIVATE)

        var lottoNumbers = LottoMainActivity.getLottoNumbersFromPreference(pref)

        var recyclerView = findViewById<RecyclerView>(R.id.lotto_list)

        var layoutManager = LinearLayoutManager(this)

        var adapter = LottoListAdapter(lottoNumbers)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

    }
}