package wikibook.learnandroid.weatherdustchecker

import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type

interface DustCheckAPIService {
    @GET("/feed/geo:{lat};{lon}/")
    fun getDustStatusInfo(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double,
        @Query("token") token: String
    ) : Call<DustCheckResponseFromGSON>
}

data class DustCheckResponseFromGSON(val pm10: Int, val pm25: Int, val pm10Status: String, val pm25Status: String, val co2: Double, val o3: Double, val no2: Double, val cityName: String)

class DustCheckerResponseDeserializerGSON : JsonDeserializer<DustCheckResponseFromGSON> {
    private fun changedCheckCategory(aqi: Int?) : String {
        var beforeMin = 0
        var beforeMax = 0
        var newMin = 0
        var newMax = 0
        when(aqi) {
            null -> "알 수 없음"
            in(0 .. 50) -> {
                beforeMax = 50
                newMax = 54
            }
            in(51 .. 100) -> {
                beforeMin = 51
                beforeMax = 100
                newMin = 55
                newMax = 154
            }
            in(101..150) -> {
                beforeMin = 101
                beforeMax = 150
                newMin = 155
                newMax = 254
            }
            in(151 .. 200) -> {
                beforeMin = 151
                beforeMax = 200
                newMin = 255
                newMax = 354
            }
            in(201..300) -> {
                beforeMin = 201
                beforeMax = 300
                newMin = 355
                newMax = 424
            }
            else -> {
                beforeMin = 301
                beforeMax = 500
                newMin = 425
                newMax = 604
            }
        }
        val m3 = (((aqi!! - beforeMin) * (newMax - newMin)) / (beforeMax - beforeMin) + newMin)
        Log.d("dust", "${aqi} ${m3}")
        return when(m3) {
            in(0 .. 154) -> "좋음"
            in(155 .. 254) -> "보통"
            in(255 .. 354) -> "나쁨"
            else -> "매우 나쁨"
        }
    }

    private val checkCategory = { aqi: Int? -> when(aqi) {
        null -> "알 수 없음"
        in(0 .. 100) -> "좋음"
        in(101..200) -> "보통"
        in(201..300) -> "나쁨"
        else -> "매우 나쁨"
    } }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): DustCheckResponseFromGSON {
        val root = json?.asJsonObject

        var dataNode = root?.getAsJsonObject("data")
        var cityNode = dataNode?.getAsJsonObject("city")
        var iaqiNode = dataNode?.getAsJsonObject("iaqi")
        var pm10Node = iaqiNode?.getAsJsonObject("pm10")
        var pm25Node = iaqiNode?.getAsJsonObject("pm25")
        var co2Node = iaqiNode?.getAsJsonObject("co")
        var o3Node = iaqiNode?.getAsJsonObject("o3")
        var no2Node = iaqiNode?.getAsJsonObject("no2")

        var pm10 = pm10Node?.get("v")?.asInt
        var pm25 = pm25Node?.get("v")?.asInt
        var co2 = co2Node?.get("v")?.asDouble
        var o3 = o3Node?.get("v")?.asDouble
        var no2 = no2Node?.get("v")?.asDouble
        var cityName = cityNode?.get("name")?.asString?.split(",")?.get(0)
        Log.d("cityname", "$cityName")

        return DustCheckResponseFromGSON(pm10!!, pm25!!, checkCategory(pm10), changedCheckCategory(pm25), co2!!, o3!!, no2!!, cityName!!)
    }

    /*
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?
    ): DustCheckResponseFromGSON {
        val root =

        var dataNode: JsonNode? = node?.get("data")
        var iaqiNode = dataNode?.get("iaqi")
        var pm10Node = iaqiNode?.get("pm10")
        var pm25Node = iaqiNode?.get("pm25")
        var pm10 = pm10Node?.get("v")?.asInt()
        var pm25 = pm25Node?.get("v")?.asInt()

        // val pm10 = node?.get("iaqi")?.get("pm10")?.get("v")

        var pm10Status = checkCategory(pm10)
        var pm25Status = checkCategory(pm25)

        return DustCheckResponseFromGSON(pm10!!, pm25!!, pm10Status, pm25Status)
    }

     */

}