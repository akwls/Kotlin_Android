package wikibook.learnandroid.weatherdustchecker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherdustchecker.R

class WeatherMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_main_activity)

        supportActionBar?.hide()

        // val fragment = WeatherPageFragment.newInstance(37.579876, 126.976998)
        val fragment = DustPageFragment.newInstance(37.579876, 126.976998)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, fragment)

        transaction.commit()
    }
}