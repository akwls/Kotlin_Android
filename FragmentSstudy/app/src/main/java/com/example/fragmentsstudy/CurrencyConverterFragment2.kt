package com.example.fragmentsstudy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import java.util.zip.Inflater

class CurrencyConverterFragment2 : Fragment() {
    private val currencyAmountExchangeMap = mapOf("USD" to 1.0, "EUR" to 0.9, "JPY" to 110.0, "KRW" to 1150.0)

    private fun calculateCurrency(amount: Double, from:String, to:String) : Double {
        val USDAmount = if(from != "USD") (amount / currencyAmountExchangeMap[from]!!) else amount

        return currencyAmountExchangeMap[to]!! * USDAmount
    }

    lateinit var fromCurrency : String
    lateinit var toCurrency: String

    companion object {
        fun newInstance(from: String, to: String) : CurrencyConverterFragment2 {
            val fragment = CurrencyConverterFragment2()

            val args = Bundle()
            args.putString("from", from)
            args.putString("to", to)

            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.currency_converter_fragment2, container, false)

        val calculateBtn = view.findViewById<Button>(R.id.calculate)
        val amount = view.findViewById<EditText>(R.id.amount)
        val result = view.findViewById<TextView>(R.id.result)

        fromCurrency = arguments?.getString("from", "USD")!!
        toCurrency = arguments?.getString("to", "USD")!!
        view.findViewById<TextView>(R.id.exchange_type).text = "${fromCurrency} -> ${toCurrency} 변환"

        calculateBtn.setOnClickListener {
            result.text = calculateCurrency(amount.text.toString().toDouble(), fromCurrency, toCurrency).toString()
        }

        return view
    }
}