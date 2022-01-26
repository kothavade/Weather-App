package com.example.weatherproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.Coil
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import coil.load
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


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val context = binding.root.context
        setContentView(binding.root)

        val imageLoader = ImageLoader.Builder(context)
            .componentRegistry{
                add(ImageDecoderDecoder(context = context))
            }
            .build()
        Coil.setImageLoader(imageLoader)


        val key = "b78ef799a58e72c75824c2eeb7077223"//"a5cf2a811a281fccc912a1b0e55c03e2"
        val exclude = "minutely,daily"
        val country = "us"

        val timeList: List<TextView> = listOf(binding.time1, binding.time2, binding.time3, binding.time4)
        val tempList: List<TextView> = listOf(binding.temp1, binding.temp2, binding.temp3, binding.temp4)
        val descList: List<TextView> = listOf(binding.desc1, binding.desc2, binding.desc3, binding.desc4)
        val imgList: List<ImageView> =
            listOf(binding.imageView1, binding.imageView2, binding.imageView3, binding.imageView4)


        binding.button.setOnClickListener {
            val loading = "Loading..."
            binding.location.text = loading
            binding.coord.text = loading
            binding.progressBar.visibility = View.VISIBLE
            for (i in 0..3) {
                timeList[i].text = loading
                tempList[i].text = loading
                descList[i].text = loading
                //imgList[i].load(android.R.drawable.progress_medium_material)
            }
            binding.button.isEnabled = false
            lifecycleScope.launch {

                val zipCode = binding.input.text.toString()
                val geoURL = "https://api.openweathermap.org/geo/1.0/zip?zip=$zipCode,$country&appid=$key"
                val checkedId = binding.unitSelect.checkedRadioButtonId
                val units =
                    if (checkedId == R.id.fahrenheit) "imperial" else if (checkedId == R.id.celcius) "metric" else "standard"
                val unitSign = if (checkedId == R.id.fahrenheit) "°F" else if (checkedId == R.id.celcius) "°C" else "K"
                Log.d("Tag", binding.unitSelect.checkedRadioButtonId.toString())

                try {
                    val geoResult = httpGet(geoURL)
                    val geoJSON = JSONObject(geoResult)

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
                        val timeInt = temp.get("dt").toString().toInt()
                        //Docs say time returns UTC, yet it gives current time-zone accurate time, not sure why
                        //val timeOffset = weatherJSON.get("timezone_offset").toString().toInt()
                        val timeLong = (timeInt).toLong()
                        val time = SimpleDateFormat("hh:mm a").format((timeLong * 1000))
                        val weather = temp.getJSONArray("weather").getJSONObject(0)
                        val imgId = weather.get("icon").toString().replace("\"", "")
                        val imgString = "https://openweathermap.org/img/wn/$imgId@4x.png"
                        val desc = weather.get("description").toString().replace("\"", "")

                        timeList[i].text = time
                        tempList[i].text = temp.get("temp").toString() + unitSign
                        imgList[i].load(imgString)
                        descList[i].text = desc.capitalizeWords()
                    }
                    binding.progressBar.visibility = View.GONE
                } catch (_: FileNotFoundException) {
                    Toast.makeText(applicationContext, "Invalid Zip Code. Try Again.", Toast.LENGTH_LONG).show()
                    binding.location.text = "Enter valid Zip Code..."
                    binding.coord.text = ""
                    //binding.imageView.load(R.drawable.loading)
                    binding.progressBar.visibility = View.VISIBLE
                    for (i in 0..3) {
                        timeList[i].text = ""
                        tempList[i].text = ""
                        descList[i].text = ""
                        imgList[i].setImageDrawable(null)
                    }
                }
            }
            binding.button.isEnabled = true
        }
        binding.fahrenheit.performClick()
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

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.lowercase().capitalize() }
}