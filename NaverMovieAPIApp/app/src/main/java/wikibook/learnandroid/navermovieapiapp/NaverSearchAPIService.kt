package wikibook.learnandroid.navermovieapiapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverSearchAPIService {
    @GET("/v1/search/movie.json")
    fun queryMovieInfo(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String,
        @Query("display") display: String,
        @Query("genre") genre: String
    ) : Call<Movies>

}

data class Movies(val items: List<Map<String, String>>)