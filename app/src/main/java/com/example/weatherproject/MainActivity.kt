package com.example.weatherproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.weatherproject.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val key = "b78ef799a58e72c75824c2eeb7077223"//"a5cf2a811a281fccc912a1b0e55c03e2"
        val units = "imperial"
        val exclude = "minutely,daily"
        val country = "us"

        val timeList: List<TextView> = listOf(binding.time1, binding.time2, binding.time3, binding.time4)
        val tempList: List<TextView> = listOf(binding.temp1, binding.temp2, binding.temp3, binding.temp4)
        val descList: List<TextView> = listOf(binding.desc1, binding.desc2, binding.desc3, binding.desc4)
        val imgList: List<ImageView> =
            listOf(binding.imageView1, binding.imageView2, binding.imageView3, binding.imageView4)

        fun setLoading() {
            val loading = "Loading..."
            binding.location.text = loading
            binding.coord.text = loading
            binding.imageView.setImageDrawable(null)

            for (i in 0..3) {
                timeList[i].text = loading
                tempList[i].text = loading
                descList[i].text = loading
                Glide.with(this).load(R.drawable.loading).into(imgList[i])
            }
        }

        fun setError() {
            binding.location.text = "Waiting..."
            binding.coord.text = ""
            Glide.with(this).load(R.drawable.loading).into(binding.imageView)

            for (i in 0..3) {
                timeList[i].text = ""
                tempList[i].text = ""
                descList[i].text = ""
                imgList[i].setImageDrawable(null)
            }
        }

        binding.button.setOnClickListener {
            setLoading()
            binding.button.isEnabled = false
            lifecycleScope.launch {

                val zipCode = binding.input.text.toString()
                val geoURL = "https://api.openweathermap.org/geo/1.0/zip?zip=$zipCode,$country&appid=$key"

                try {
                    val geoResult = httpGet(geoURL)
                    val geoJSON = JSONObject(geoResult)
                    //val geoJSON = parseToJsonElement(geoResult)

                    binding.location.text = geoJSON.get("name").toString().replace("\"", "")

                    val lat = geoJSON.get("lat").toString()
                    val lon = geoJSON.get("lon").toString()
                    binding.coord.text = "Coordinates: $lat, $lon"
                    val weatherURL =
                        "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&appid=$key&exclude=$exclude&units=$units"
                    val weatherResult = httpGet(weatherURL)
                    val weatherJSON = JSONObject(weatherResult)

                    for (i in 0..3) {
                        val temp = weatherJSON.getJSONArray("hourly").getJSONObject(i)
                        val timeString = temp.get("dt").toString().toLong()
                        val time = SimpleDateFormat("hh:mm a").format((timeString * 1000))
                        val weather = temp.getJSONArray("weather").getJSONObject(0)
                        val imgId = weather.get("icon").toString().replace("\"", "")
                        val imgString = "https://openweathermap.org/img/wn/$imgId@4x.png"
                        val desc = weather.get("description").toString().replace("\"", "")

                        timeList[i].text = time
                        tempList[i].text = "${temp?.get("temp")}°F"
                        Glide.with(this@MainActivity).load(imgString).into(imgList[i])
                        descList[i].text = desc.capitalizeWords()
                    }
                } catch (_: FileNotFoundException) {
                    Toast.makeText(applicationContext, "Invalid Zip Code. Try Again.", Toast.LENGTH_LONG).show()
                    setError()
                }
            }
            binding.button.isEnabled = true
        }
        binding.button.performClick()
    }

    private suspend fun httpGet(inputURL: String): String {
        val result = withContext(Dispatchers.IO) {
            val inputStream: InputStream
            val url = URL(inputURL)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.connect()
            inputStream = conn.inputStream
            inputStreamToString(inputStream)
        }
        return result
    }

    private fun inputStreamToString(inputStream: InputStream): String {
        val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
        var line: String? = bufferedReader.readLine()
        var result = ""
        while (line != null) {
            result += line
            line = bufferedReader.readLine()
        }
        inputStream.close()
        return result
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") {
        it.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}