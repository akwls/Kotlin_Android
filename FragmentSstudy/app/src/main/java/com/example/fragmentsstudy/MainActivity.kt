package com.example.fragmentsstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity(), CurrencyConverterFragment3.CurrencyCalculationListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()

        // transaction.add(R.id.fragment_container, CurrencyConverterFragment1())

        /*
        transaction.add(R.id.fragment_container, CurrencyConverterFragment2.newInstance("KRW", "USD"))
        transaction.add(R.id.fragment_container, CurrencyConverterFragment2.newInstance("JPY", "KRW"))
        transaction.add(R.id.fragment_container, CurrencyConverterFragment2.newInstance("EUR", "JPY"))
         */

        transaction.add(R.id.fragment_container, CurrencyConverterFragment3.newInstance("KRW", "USD"))
        transaction.add(R.id.fragment_container, CurrencyConverterFragment3.newInstance("JPY", "KRW"))
        transaction.add(R.id.fragment_container, CurrencyConverterFragment3.newInstance("EUR", "JPY"))

        transaction.commit()
    }

    override fun onCalculate(result: Double, amuont: Double, from: String, to: String) {
        Toast.makeText(applicationContext, "${String.format("%.5f", amuont)}($from) -> ${String.format("%.5f", result)}($to)", Toast.LENGTH_SHORT).show()
    }
}