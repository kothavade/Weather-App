package com.example.weatherproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.weatherproject.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
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

        fun setLoading() {
            val loading = "Loading..."
            binding.location.text=loading
            binding.coord.text=loading


            binding.temp1.text=loading
            binding.temp2.text=loading
            binding.temp3.text=loading
            binding.temp4.text=loading

            binding.desc1.text=loading
            binding.desc2.text=loading
            binding.desc3.text=loading
            binding.desc4.text=loading

            binding.time1.text=loading
            binding.time2.text=loading
            binding.time3.text=loading
            binding.time4.text=loading

            Glide.with(this)
                .load(R.drawable.loading)
                .into(binding.imageView1)
            Glide.with(this)
                .load(R.drawable.loading)
                .into(binding.imageView2)
            Glide.with(this)
                .load(R.drawable.loading)
                .into(binding.imageView3)
            Glide.with(this)
                .load(R.drawable.loading)
                .into(binding.imageView4)

            binding.imageView.setImageDrawable(null)

        }
        fun setError() {
            binding.location.text="Waiting..."
            binding.coord.text=""

            binding.temp1.text=""
            binding.temp2.text=""
            binding.temp3.text=""
            binding.temp4.text=""

            binding.desc1.text=""
            binding.desc2.text=""
            binding.desc3.text=""
            binding.desc4.text=""

            binding.time1.text=""
            binding.time2.text=""
            binding.time3.text=""
            binding.time4.text=""

            binding.imageView1.setImageDrawable(null)
            binding.imageView2.setImageDrawable(null)
            binding.imageView3.setImageDrawable(null)
            binding.imageView4.setImageDrawable(null)

            Glide.with(this)
                .load(R.drawable.loading)
                .into(binding.imageView)

        }

        binding.button.setOnClickListener {
            setLoading()
            binding.button.isEnabled = false
            lifecycleScope.launch {

                val zipCode = binding.input.text.toString()
                val weatherString = "https://api.openweathermap.org/geo/1.0/zip?zip=$zipCode,$country&appid=$key"

                try {
                    val result = httpGet(weatherString)
                    val dataJSON = parseToJsonElement(result)

                    binding.location.text =
                        dataJSON.jsonObject.get("name").toString().replace("\"", "")

                    val lat = dataJSON.jsonObject.get("lat").toString()
                    val lon = dataJSON.jsonObject.get("lon").toString()
                    binding.coord.text = "Coordinates: $lat, $lon"
                    val oneCallString =
                        "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&appid=$key&exclude=$exclude&units=$units"
                    val result2 = httpGet(oneCallString)
                    val dataJSON2 = parseToJsonElement(result2)

                    for (i in 0..3) {
                        when (i) {
                            0 -> {
                                val temp = dataJSON2.jsonObject.get("hourly")?.jsonArray?.get(0)?.jsonObject
                                val timeString = temp?.get("dt").toString().toLong()
                                val time = SimpleDateFormat("hh:mm a").format((timeString * 1000))
                                val weather = temp?.get("weather")?.jsonArray?.get(0)?.jsonObject
                                val imgId = weather?.get("icon").toString().replace("\"", "")
                                val imgString =
                                    "https://openweathermap.org/img/wn/$imgId@4x.png"
                                val desc = weather?.get("description").toString().replace("\"", "")

                                binding.time1.text = time
                                binding.temp1.text = "${temp?.get("temp")}°F"
                                Glide.with(this@MainActivity)
                                    .load(imgString)
                                    .into(binding.imageView1)
                                //binding.imageView1.setImageBitmap(bitmapGet(imgString))
                                binding.desc1.text = desc.capitalizeWords()
                            }
                            1 -> {
                                val temp = dataJSON2.jsonObject.get("hourly")?.jsonArray?.get(1)?.jsonObject
                                val timeString = temp?.get("dt").toString().toLong()
                                val time = SimpleDateFormat("hh:mm a").format((timeString * 1000))
                                val weather = temp?.get("weather")?.jsonArray?.get(0)?.jsonObject
                                val imgId = weather?.get("icon").toString().replace("\"", "")
                                val imgString =
                                    "https://openweathermap.org/img/wn/$imgId@4x.png"
                                val desc = weather?.get("description").toString().replace("\"", "")

                                binding.time2.text = time
                                binding.temp2.text = "${temp?.get("temp")}°F"
                                Glide.with(this@MainActivity)
                                    .load(imgString)
                                    .into(binding.imageView2)
                                //binding.imageView2.setImageBitmap(bitmapGet(imgString))
                                binding.desc2.text = desc.capitalizeWords()
                            }
                            2 -> {
                                val temp = dataJSON2.jsonObject.get("hourly")?.jsonArray?.get(2)?.jsonObject
                                val timeString = temp?.get("dt").toString().toLong()
                                val time = SimpleDateFormat("hh:mm a").format((timeString * 1000))
                                val weather = temp?.get("weather")?.jsonArray?.get(0)?.jsonObject
                                val imgId = weather?.get("icon").toString().replace("\"", "")
                                val imgString =
                                    "https://openweathermap.org/img/wn/$imgId@4x.png"
                                val desc = weather?.get("description").toString().replace("\"", "")

                                binding.time3.text = time
                                binding.temp3.text = "${temp?.get("temp")}°F"
                                Glide.with(this@MainActivity)
                                    .load(imgString)
                                    .into(binding.imageView3)
                                //binding.imageView3.setImageBitmap(bitmapGet(imgString))
                                binding.desc3.text = desc.capitalizeWords()
                            }
                            3 -> {
                                val temp = dataJSON2.jsonObject.get("hourly")?.jsonArray?.get(3)?.jsonObject
                                val timeString = temp?.get("dt").toString().toLong()
                                val time = SimpleDateFormat("hh:mm a").format((timeString * 1000))
                                val weather = temp?.get("weather")?.jsonArray?.get(0)?.jsonObject
                                val imgId = weather?.get("icon").toString().replace("\"", "")
                                val imgString =
                                    "https://openweathermap.org/img/wn/$imgId@4x.png"
                                val desc = weather?.get("description").toString().replace("\"", "")

                                binding.time4.text = time
                                binding.temp4.text = "${temp?.get("temp")}°F"
                                Glide.with(this@MainActivity)
                                    .load(imgString)
                                    .into(binding.imageView4)
                                //binding.imageView4.setImageBitmap(bitmapGet(imgString))
                                binding.desc4.text = desc.capitalizeWords()
                            }
                        }
                    }
                } catch (_: FileNotFoundException) {
                    Toast.makeText(applicationContext, "Invalid Zip Code. Try Again.", Toast.LENGTH_LONG).show()
                    setError()
                }
            }
            binding.button.isEnabled = true
        }
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

    //Using Glide for images instead due to animated Gif support, but this works just as well for static images
    private suspend fun bitmapGet(inputURL: String): Bitmap {
        val inputStream: InputStream
        withContext(Dispatchers.IO) {
            val url = URL(inputURL)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.connect()
            inputStream = conn.inputStream
        }
        return BitmapFactory.decodeStream(inputStream)
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
        split(" ").joinToString(" ") { it.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
}