package com.example.weatherproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
        binding.button.setOnClickListener {
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
                                binding.imageView1.setImageBitmap(imgGet(imgString))
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
                                binding.imageView2.setImageBitmap(imgGet(imgString))
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
                                binding.imageView3.setImageBitmap(imgGet(imgString))
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
                                binding.imageView4.setImageBitmap(imgGet(imgString))
                                binding.desc4.text = desc.capitalizeWords()
                            }
                        }
                    }
                } catch (_: FileNotFoundException) {
                    Toast.makeText(applicationContext, "Invalid Zip Code. Try Again.", Toast.LENGTH_LONG).show()
                    binding.temp1.text = ""
                    binding.location.text = ""
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

    private fun imageMappings(owmId: String): String {
        when (owmId) {
            "01d" -> return "f00d"
            "01n" -> return "f02e"
            "10d" -> return "f019"
            "13d" -> return "f01b"
            "50d" -> return "f014"
            "04d" -> return "f013"
            "03d" -> return "f002"
            "04n" -> return "f086"
            "11d" -> return "f01e"
            "02d" -> return "f00c"
            "09d" -> return "f017"
            "02n" -> return "f081"
            "03n" -> return "f07e"
            "09n" -> return "f026"
            "10n" -> return "f028"
            "11n" -> return "f02c"
            "13n" -> return "f02a"
            "50n" -> return "f04a"
            else -> return "moon1"
        }
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.lowercase().capitalize() }
}