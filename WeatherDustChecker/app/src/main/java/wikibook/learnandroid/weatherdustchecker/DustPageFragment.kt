package wikibook.learnandroid.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weatherdustchecker.R
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URL

class DustPageFragment : Fragment() {
    private val APP_TOKEN = "3b273b5194e159fd1d1c1ee551b2688bc84fa2ae"

    lateinit var statusImage : ImageView
    lateinit var pm25StatusText: TextView
    lateinit var pm25IntensityText: TextView
    lateinit var pm10StatusText: TextView
    lateinit var pm10IntensityText: TextView

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
        pm10IntensityText = view.findViewById(R.id.dust_pm10_status_text)

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

    @JsonDeserialize(using = DustCheckerResponseDeserializer::class)
    data class DustCheckerResponse(val pm10: Int?, val pm25: Int?, val pm10Status: String, val pm25Status: String)

    class DustCheckerResponseDeserializer : StdDeserializer<DustCheckerResponse>(DustCheckerResponse::class.java) {
        private val checkCategory = { aqi: Int? -> when(aqi) {
            null -> "알 수 없음"
            in(0 .. 100) -> "좋음"
            in(101..200) -> "보통"
            in(201..300) -> "나쁨"
            else -> "매우 나쁨"
        } }

        override fun deserialize(
            p: JsonParser?,
            ctxt: DeserializationContext?
        ): DustCheckerResponse {
            val node : JsonNode? = p?.codec?.readTree(p)

            var dataNode: JsonNode? = node?.get("data")
            var iaqiNode = dataNode?.get("iaqi")
            var pm10Node = iaqiNode?.get("pm10")
            var pm25Node = iaqiNode?.get("pm25")
            var pm10 = pm10Node?.get("v")?.asInt()
            var pm25 = pm25Node?.get("v")?.asInt()

            // val pm10 = node?.get("iaqi")?.get("pm10")?.get("v")

            var pm10Status = checkCategory(pm10)
            var pm25Status = checkCategory(pm25)

            return DustCheckerResponse(pm10, pm25, pm10Status, pm25Status)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments!!.getDouble("lat")
        val lon = arguments!!.getDouble("lon")

        val url = "https://api.waqi.info/feed/geo:${lat};${lon}/?token=${APP_TOKEN}"

        APICall(object : APICall.APICallback {
            override fun onComplete(result: String) {
                var mapper = jacksonObjectMapper()
                val data = mapper.readValue<DustCheckerResponse>(result)

                Log.d("mytag", result)

                statusImage.setImageResource(when(data.pm25Status) {
                    "좋음" -> R.drawable.good
                    "보통" -> R.drawable.normal
                    "나쁨" -> R.drawable.bad
                    else -> R.drawable.very_bad
                })

                pm25IntensityText.text = data.pm25?.toString() ?: "알 수 없음"
                pm10IntensityText.text = data.pm10?.toString() ?: "알 수 없음"

                pm25StatusText.text = "${data.pm25Status} (초미세먼지)"
                pm10StatusText.text = "${data.pm10Status} (미세먼지)"
            }

        }).execute(URL(url))
    }
}