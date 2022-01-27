package wikibook.learnandroid.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weatherdustchecker.R
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class DustPageFragment : Fragment() {
    private val APP_TOKEN = "3b273b5194e159fd1d1c1ee551b2688bc84fa2ae"

    lateinit var statusImage : ImageView
    lateinit var pm25StatusText: TextView
    lateinit var pm25IntensityText: TextView
    lateinit var pm10StatusText: TextView
    lateinit var pm10IntensityText: TextView
    lateinit var co2: TextView
    lateinit var o3: TextView
    lateinit var no2: TextView
    lateinit var cityName: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dust_page_fragment, container, false)

        statusImage = view.findViewById(R.id.dust_status_icon)
        pm25StatusText = view.findViewById(R.id.dust_pm25_status_text)
        pm25IntensityText = view.findViewById(R.id.dust_pm25_intensity_text)
        pm10StatusText = view.findViewById(R.id.dust_pm10_status_text)
        pm10IntensityText = view.findViewById(R.id.dust_pm10_intensity_text)
        co2 = view.findViewById(R.id.co2)
        o3 = view.findViewById(R.id.o3)
        no2 = view.findViewById(R.id.no2)
        cityName = view.findViewById(R.id.city_name)

        return view
    }

    companion object {
        fun newInstance(lat: Double, lon: Double) : DustPageFragment {
            val fragment = DustPageFragment()

            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            fragment.arguments = args

            return fragment
        }
    }

    fun startAnimation() {
        val fadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        statusImage.startAnimation(fadeIn)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments!!.getDouble("lat")
        val lon = arguments!!.getDouble("lon")

        // val url = "https://api.waqi.info/feed/geo:${lat};${lon}/?token=${APP_TOKEN}"

        val retrofit = Retrofit.Builder().baseUrl("https://api.waqi.info").addConverterFactory(GsonConverterFactory.create(
            GsonBuilder().registerTypeAdapter(
                DustCheckResponseFromGSON::class.java,
                DustCheckerResponseDeserializerGSON()
            ).create()
        )).build()

        val apiService = retrofit.create(DustCheckAPIService::class.java)
        val apiCallForData = apiService.getDustStatusInfo(lat, lon, APP_TOKEN)


        apiCallForData.enqueue(object : Callback<DustCheckResponseFromGSON> {
            override fun onResponse(
                call: Call<DustCheckResponseFromGSON>,
                response: Response<DustCheckResponseFromGSON>
            ) {
                val data = response.body()

                if(data != null) {
                    statusImage.setImageResource(when(data.pm25Status) {
                        "좋음" -> R.drawable.ic_good
                        "보통" -> R.drawable.ic_normal
                        "나쁨" -> R.drawable.ic_bad
                        else -> R.drawable.ic_very_bad
                    })

                    pm25IntensityText.text = data.pm25?.toString() ?: "알 수 없음"
                    pm10IntensityText.text = data.pm10?.toString() ?: "알 수 없음"

                    pm25StatusText.text = "${data.pm25Status} (초미세먼지)"
                    pm10StatusText.text = "${data.pm10Status} (미세먼지)"

                    co2.text = "이산화 탄소 : ${data.co2}"
                    o3.text = "오존 : ${data.o3}"
                    no2.text = "이산화 질소 : ${data.no2}"

                    cityName.text = data.cityName
                }
            }

            override fun onFailure(call: Call<DustCheckResponseFromGSON>, t: Throwable) {
                Toast.makeText(activity, "에러 발생 : ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

        /*
        APICall(object : APICall.APICallback {
            override fun onComplete(result: String) {
                var mapper = jacksonObjectMapper()
                val data = mapper.readValue<DustCheckResponseFromGSON>(result)

                Log.d("mytag", result)

                statusImage.setImageResource(when(data.pm25Status) {
                    "좋음" -> R.drawable.ic_good
                    "보통" -> R.drawable.ic_normal
                    "나쁨" -> R.drawable.ic_bad
                    else -> R.drawable.ic_very_bad
                })

                pm25IntensityText.text = data.pm25?.toString() ?: "알 수 없음"
                pm10IntensityText.text = data.pm10?.toString() ?: "알 수 없음"

                pm25StatusText.text = "${data.pm25Status} (초미세먼지)"
                pm10StatusText.text = "${data.pm10Status} (미세먼지)"
            }

        }).execute(URL(url))

         */
    }
}