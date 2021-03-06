package wikibook.learnandroid.weatherdustchecker

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.weatherdustchecker.R
import java.util.jar.Manifest

class WeatherDustMainActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager
    private lateinit var loadingScreen: RelativeLayout
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    private val PERMISSION_REQUEST_CODE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_dust_main_activity)

        supportActionBar?.hide()

        mPager = findViewById(R.id.pager)
        loadingScreen = findViewById(R.id.loading_screen)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                lat = p0.latitude
                lon = p0.longitude

                locationManager.removeUpdates(this)

                val pagerAdapter = MyPagerAdapter(supportFragmentManager)
                mPager.adapter = pagerAdapter

                mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {

                    }

                    override fun onPageSelected(position: Int) {
                        if(position == 0) {
                            val fragment = mPager.adapter?.instantiateItem(mPager, position) as WeatherPageFragment
                            fragment.startAnimation()
                        }
                        else if(position == 1) {
                            val fragment = mPager.adapter?.instantiateItem(mPager, position) as DustPageFragment
                            fragment.startAnimation()
                        }
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }


                })
                loadingScreen.visibility = View.GONE
            }

        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) === PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PERMISSION_REQUEST_CODE -> {
                var allPermissionGranted = true
                for(result in grantResults) {
                    allPermissionGranted = (result == PackageManager.PERMISSION_GRANTED)
                    if(!allPermissionGranted) break
                }
                if(allPermissionGranted) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
                }
                else {
                    Toast.makeText(applicationContext, "?????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> WeatherPageFragment.newInstance(lat, lon)
                1 -> DustPageFragment.newInstance(lat, lon)
                else -> {
                    throw Exception("???????????? ???????????? ??????.")
                }
            }
        }

    }
}