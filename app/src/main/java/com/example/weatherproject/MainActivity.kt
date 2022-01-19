package com.example.weatherproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
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
import java.lang.Exception
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
            binding.location.text="Loading..."
            binding.coord.text="Loading..."


            binding.temp1.text="Loading..."
            binding.temp2.text="Loading..."
            binding.temp3.text="Loading..."
            binding.temp4.text="Loading..."

            binding.desc1.text="Loading..."
            binding.desc2.text="Loading..."
            binding.desc3.text="Loading..."
            binding.desc4.text="Loading..."

            binding.time1.text="Loading..."
            binding.time2.text="Loading..."
            binding.time3.text="Loading..."
            binding.time4.text="Loading..."

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
                    println(dataJSON.toString())
                    Log.d("TAG", "cod: ${dataJSON.jsonObject.get("cod").toString()}")

                    binding.location.text =
                        dataJSON.jsonObject.get("name").toString().replace("\"", "")

                    val lat = dataJSON.jsonObject.get("lat").toString()
                    val lon = dataJSON.jsonObject.get("lon").toString()
                    binding.coord.text = "Coordinates: $lat, $lon"
                    val oneCallString =
                        "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&appid=$key&exclude=$exclude&units=$units"
                    Log.d("Tag", oneCallString)
                    val result2 = httpGet(oneCallString)
                    val dataJSON2 = parseToJsonElement(result2)
                    Log.d("Tag", dataJSON2.toString())
                    for (i in 0..3) {
                        when (i) {
                            0 -> {
                                val temp = dataJSON2.jsonObject.get("hourly")?.jsonArray?.get(i)?.jsonObject
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
                                //binding.imageView1.setImageBitmap(imgGet(imgString))
                                binding.desc1.text = desc.capitalizeWords()
                            }
                            1 -> {
                                val temp = dataJSON2.jsonObject.get("hourly")?.jsonArray?.get(i)?.jsonObject
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
                                //binding.imageView2.setImageBitmap(imgGet(imgString))
                                binding.desc2.text = desc.capitalizeWords()
                            }
                            2 -> {
                                val temp = dataJSON2.jsonObject.get("hourly")?.jsonArray?.get(i)?.jsonObject
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
                                //binding.imageView3.setImageBitmap(imgGet(imgString))
                                binding.desc3.text = desc.capitalizeWords()
                            }
                            3 -> {
                                val temp = dataJSON2.jsonObject.get("hourly")?.jsonArray?.get(i)?.jsonObject
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
                                //binding.imageView4.setImageBitmap(imgGet(imgString))
                                binding.desc4.text = desc.capitalizeWords()
                            }
                        }
                    }
                } catch (_: FileNotFoundException) {
                    Toast.makeText(applicationContext, "Invalid Zip Code. Try Again.", Toast.LENGTH_LONG).show()
                    setLoading()
                }
            }
            binding.button.isEnabled = true
        }
    }

    private suspend fun httpGet(myURL: String): String {
        val result = withContext(Dispatchers.IO) {
            val inputStream: InputStream
            val url = URL(myURL)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.connect()
            inputStream = conn.inputStream
            convertInputStreamToString(inputStream)
        }
        return result
    }

    private suspend fun imgGet(myURL: String): Bitmap {
        val inputStream: InputStream
        withContext(Dispatchers.IO) {
            val url = URL(myURL)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.connect()
            inputStream = conn.inputStream
        }
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun convertInputStreamToString(inputStream: InputStream): String {
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