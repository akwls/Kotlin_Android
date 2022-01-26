package com.example.lottoexample

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LottoListAdapter(var datalist: MutableList<String>): RecyclerView.Adapter<LottoListAdapter.LottoItemViewHolder>() {

    class LottoItemViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var lottoNumber = view.findViewById<TextView>(R.id.lotto_text)
        var lottoDeleteBtn = view.findViewById<Button>(R.id.lotto_delete_btn)

        fun bind(numbers: String) {
            lottoNumber.text = numbers
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LottoItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return LottoItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: LottoItemViewHolder, position: Int) {
        holder.bind(datalist[position])
        holder.lottoDeleteBtn.setOnClickListener() {
            datalist.removeAt(position)
            var pref = it.context.getSharedPreferences("lotto", Context.MODE_PRIVATE)
            var editor = pref.edit()
            for(n in datalist) {
                Log.d("datalist", n)
            }
            editor.remove("$position")
            LottoMainActivity.saveLottoToPreference(pref, datalist)
            editor.apply()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.lotto_list_item
    }


}