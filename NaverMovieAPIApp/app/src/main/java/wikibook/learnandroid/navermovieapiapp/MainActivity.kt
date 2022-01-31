package wikibook.learnandroid.navermovieapiapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var searchEdit: EditText
    private lateinit var searchBtn: Button
    private lateinit var movieList: RecyclerView
    private lateinit var keywordsText: TextView
    private val clientId = "AlDnbQHge7MPvrPflEAu"
    private val clientSecret = "sAztHUeJws"
    private var datalist = mutableListOf<Movie>()
    private var keywords = mutableListOf<String>()
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEdit = findViewById(R.id.search_edit)
        searchBtn = findViewById(R.id.search_btn)
        movieList = findViewById(R.id.movie_list)
        keywordsText = findViewById(R.id.keywords)

        pref = getSharedPreferences("keywords", Context.MODE_PRIVATE)
        keywords = getKeywordsFromPref(pref)
        setKeywordsText(keywords)


        val retrofit = Retrofit.Builder().
        baseUrl("https://openapi.naver.com").
        addConverterFactory(GsonConverterFactory.create()).build()


        val apiService = retrofit.create(NaverSearchAPIService::class.java)

        val layoutManager = LinearLayoutManager(this)

        movieList.setHasFixedSize(true)
        movieList.layoutManager = layoutManager

        val adapter = MovieAdapter(datalist, this)
        movieList.adapter = adapter

        searchBtn.setOnClickListener {
            datalist.clear()

            var searchInput = searchEdit.text.toString()

            val apiCallForData = apiService.queryMovieInfo(clientId, clientSecret, searchInput, "10", "1")

            saveKeywordsFromPref(pref, searchInput)

            apiCallForData.enqueue(object: Callback<Movies> {
                override fun onResponse(
                    call: Call<Movies>,
                    response: Response<Movies>
                ) {
                    val data = response.body()
                    Log.d("mytag", "${data}")
                    if(data != null) {
                        val items = data.items
                        for(item in items) {
                            datalist.add(Movie(item.get("title"), item.get("userRating")?.toDouble()))
                            Log.d("mytag", "${item.get("title")}")
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<Movies>, t: Throwable) {
                    Toast.makeText(applicationContext, "에러 발생 : ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
            keywords = getKeywordsFromPref(pref)
            setKeywordsText(keywords)

        }
    }

    fun getKeywordsFromPref(pref: SharedPreferences) : MutableList<String> {
        var result = mutableListOf<String>()
        for(i in 1 .. 5) {
            var keyword = pref.getString("${i}", null)
            if(keyword != null) {
                result.add(keyword)
            }
        }
        return result
    }

    fun saveKeywordsFromPref(pref: SharedPreferences, newKeyword: String) {
        val editor = pref.edit()
        for(i in (1 .. 4).reversed()) {
            editor.putString("${i+1}", pref.getString("${i}", null))
        }
        editor.putString("1", newKeyword)
        editor.commit()
    }

    fun setKeywordsText(keywords: MutableList<String>) {
        keywordsText.text = if(keywords.isNotEmpty()) keywords.joinToString(",") else ""
    }
}