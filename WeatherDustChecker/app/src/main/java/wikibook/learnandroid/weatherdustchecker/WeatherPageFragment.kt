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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class WeatherPageFragment : Fragment() {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class OpenWeatherAPIJSONResponse(val main: Map<String, String>, val weather: List<Map<String, String>>)

    private val APP_ID = "3938012447f7887179975b01562ef9da"
    lateinit var weatherImage : ImageView
    lateinit var statusText : TextView
    lateinit var temperatureText : TextView
    lateinit var pressure: TextView
    lateinit var humidity: TextView
    lateinit var cityName: TextView

    companion object {
        fun newInstance(lat: Double, lon: Double) : WeatherPageFragment {
            val fragment = WeatherPageFragment()

            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            fragment.arguments = args

            return fragment
        }
    }

    fun startAnimation() {
        val fadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        weatherImage.startAnimation(fadeIn)

        val textAnimation = AnimationUtils.loadAnimation(activity, R.anim.text_anim)
        statusText.startAnimation(textAnimation)
        temperatureText.startAnimation(textAnimation)
        pressure.startAnimation(textAnimation)
        humidity.startAnimation(textAnimation)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.weather_page_fragment, container, false)

        weatherImage = view.findViewById(R.id.weather_icon)
        statusText = view.findViewById(R.id.weather_status_text)
        temperatureText = view.findViewById(R.id.weather_temp_text)
        pressure = view.findViewById(R.id.pressure)
        humidity = view.findViewById(R.id.humidity)
        cityName = view.findViewById(R.id.city_name)

        /*
        weatherImage.setImageResource(arguments!!.getInt("res_id"))
        statusText.text = arguments!!.getString("status")
        temperatureText.text = "${arguments!!.getDouble("temperature")}º"
         */
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var lat = arguments!!.getDouble("lat")
        var lon = arguments!!.getDouble("lon")
        // var url = "http://api.openweathermap.org/data/2.5/weather?units=metric&lat=${lat}&lon=${lon}&appid=${APP_ID}"

        val retrofit = Retrofit.Builder().
                baseUrl("http://api.openweathermap.org").
                addConverterFactory(GsonConverterFactory.create()).build()

        val apiService = retrofit.create(WeatherAPIServcie::class.java)

        val apiCallForData = apiService.getWeatherStatusInfo(APP_ID, lat, lon)

        apiCallForData.enqueue(object: Callback<OpenWeatherAPIJSONResponseFromGSON> {
            override fun onResponse(
                call: Call<OpenWeatherAPIJSONResponseFromGSON>,
                response: Response<OpenWeatherAPIJSONResponseFromGSON>
            ) {
                val data = response.body()
                if(data != null) {
                    val temp = data.main.get("temp")
                    temperatureText.text = temp

                    val id = data.weather[0].get("id")
                    if(id != null) {
                        statusText.text = when {
                            id.startsWith("2") -> {
                                weatherImage.setImageResource(R.drawable.ic_flash)
                                "천둥, 번개"
                            }
                            id.startsWith("3") -> {
                                weatherImage.setImageResource(R.drawable.ic_rain)
                                "이슬비"
                            }
                            id.startsWith("5") -> {
                                weatherImage.setImageResource(R.drawable.ic_rain)
                                "비"
                            }
                            id.startsWith("6") -> {
                                weatherImage.setImageResource(R.drawable.ic_snow)
                                "눈"
                            }
                            id.startsWith("7") -> {
                                weatherImage.setImageResource(R.drawable.ic_cloudy)
                                "흐림"
                            }
                            id.startsWith("800") -> {
                                weatherImage.setImageResource(R.drawable.ic_sun)
                                "화창"
                            }
                            id.startsWith("8") -> {
                                weatherImage.setImageResource(R.drawable.ic_cloud)
                                "구름 낌"
                            }
                            else -> "알 수 없음"
                        }
                    }
                    pressure.text = "기압 : ${data.main.get("pressure")}"
                    humidity.text = "습도 : ${data.main.get("humidity")}"
                    cityName.text = data.name
                }
            }

            override fun onFailure(call: Call<OpenWeatherAPIJSONResponseFromGSON>, t: Throwable) {
                Toast.makeText(activity, "에러 발생 : ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

        /*
        APICall(object : APICall.APICallback {
            override fun onComplete(result: String) {
                val mapper = jacksonObjectMapper()
                var data = mapper?.readValue<OpenWeatherAPIJSONResponse>(result)

                val tmp = data.main.get("temp")
                temperatureText.text = tmp

                val id = data.weather[0].get("id")
                if(id != null) {
                    statusText.text = when {
                        id.startsWith("2") -> {
                            weatherImage.setImageResource(R.drawable.ic_flash)
                            "천둥, 번개"
                        }
                        id.startsWith("3") -> {
                            weatherImage.setImageResource(R.drawable.ic_rain)
                            "이슬비"
                        }
                        id.startsWith("5") -> {
                            weatherImage.setImageResource(R.drawable.ic_rain)
                            "비"
                        }
                        id.startsWith("6") -> {
                            weatherImage.setImageResource(R.drawable.ic_snow)
                            "눈"
                        }
                        id.startsWith("7") -> {
                            weatherImage.setImageResource(R.drawable.ic_cloudy)
                            "흐림"
                        }
                        id.startsWith("800") -> {
                            weatherImage.setImageResource(R.drawable.ic_sun)
                            "화창"
                        }
                        id.startsWith("8") -> {
                            weatherImage.setImageResource(R.drawable.ic_cloud)
                            "구름 낌"
                        }
                        else -> "알 수 없음"
                    }
                }
            }
        }).execute(URL(url))
         */
    }
}