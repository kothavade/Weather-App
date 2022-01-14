package com.example.weatherproject

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
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        var stateInfo = ""
        var zipCode = ""
        val zipCodeKey = "9f25f690-74ea-11ec-b891-e59a9acda4e4"
        var zipValid = true
        val country = "us"
        binding.button.setOnClickListener{
            binding.button.isEnabled=false
            val zipAPI = lifecycleScope.launch{
                zipCode = binding.input.text.toString()
                Log.d("TAG","zipCode: $zipCode")
                val zipCodeString = "https://app.zipcodebase.com/api/v1/search?apikey=$zipCodeKey&codes=$zipCode&country=${country.uppercase()}"
                Log.d("TAG","zipCodeString: $zipCodeString")
                val result = httpGet(zipCodeString)
                Log.d("TAG","result: $result")
                val dataJSON = parseToJsonElement(result)
                Log.d("TAG","dataJson: $dataJSON")
                try{
                    if(dataJSON.jsonObject.get("results")?.jsonObject?.get(zipCode)!=null){
                        zipValid=true
                        stateInfo = dataJSON.jsonObject.get("results")?.jsonObject?.get(zipCode)?.jsonArray?.get(0)?.jsonObject?.get("state_code").toString().replace("\"", "") + ", ${country.uppercase()}"
                        Log.d("TAG","country is true")
                    }
                    else {
                        zipValid = false
                    }
                }catch (_:IllegalArgumentException){
                    zipValid=false
                }

            }

            // zipCheck = """^\d{5}(-\d{4})?$""".toRegex()
            lifecycleScope.launch{
                zipAPI.join()
                Log.d("TAG","zipAPIJOINED")
                if(zipValid){
                    Log.d("TAG","ZIP IS VALID")

                    zipCode = binding.input.text.toString()

                    val key = "b78ef799a58e72c75824c2eeb7077223"//"a5cf2a811a281fccc912a1b0e55c03e2"
                    val units = "imperial"
                    val exclude = "minutely,hourly,daily"

                    val weatherString = "https://api.openweathermap.org/data/2.5/weather?zip=$zipCode,$country&appid=$key&units=$units"


                    try {
                        val result = httpGet(weatherString)
                        val dataJSON = parseToJsonElement(result)
                        println(dataJSON.toString())
                        Log.d("TAG","cod: ${dataJSON.jsonObject.get("cod").toString()}")

                        binding.location.text =
                            dataJSON.jsonObject.get("name").toString().replace("\"", "") + ", " + stateInfo

                        val lat = dataJSON.jsonObject.get("coord")?.jsonObject?.get("lat").toString()
                        val lon = dataJSON.jsonObject.get("coord")?.jsonObject?.get("lon").toString()
                        val oneCallString =
                            "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&appid=$key&exclude=$exclude&units=$units"
                        val result2 = httpGet(oneCallString)
                        val dataJSON2 = parseToJsonElement(result2)
                        //binding.temp.text =
                        //    dataJSON2.jsonObject.get("current")?.jsonObject?.get("weather")?.jsonArray?.get(0)?.jsonObject?.get("description").toString().replace("\"", "").capitalizeWords()
                        val temp = dataJSON2.jsonObject.get("current")?.jsonObject?.get("temp").toString().toDouble()
                        val tempAsInt = temp.toInt()
                        binding.temp.text =
                            "${tempAsInt}°F"
                    }
                    catch(_: FileNotFoundException) {
                        Toast.makeText(applicationContext,"Invalid Zip Code. Try Again.", Toast.LENGTH_LONG).show()
                        stateInfo = ""
                        binding.temp.text = ""
                        binding.location.text = ""
                    }
                }
                else {
                    Toast.makeText(applicationContext,"Invalid Zip Code. Try Again.", Toast.LENGTH_LONG).show()
                    stateInfo = ""
                    binding.temp.text = ""
                    binding.location.text = ""
                }
            }
            binding.button.isEnabled=true
        }


    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    } }

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

    private fun convertInputStreamToString(inputStream: InputStream): String {
        val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
        var line:String? = bufferedReader.readLine()
        var result = ""
        while (line != null) {
            result += line
            line = bufferedReader.readLine()
        }
        inputStream.close()
        return result
    }
}