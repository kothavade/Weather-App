package com.example.weatherproject

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weatherproject.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.jsonObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val zipCode = "08852"
        val key = "a5cf2a811a281fccc912a1b0e55c03e2"
        val country = "us"
        val units = "imperial"


        val urlString = "https://api.openweathermap.org/data/2.5/weather?zip=$zipCode,$country&appid=$key&units=$units"

        lifecycleScope.launch {
            val result = httpGet(urlString)
            val dataJSON = parseToJsonElement(result).jsonObject
            //val main = dataJSON.jsonObject.get(:)
            findViewById<TextView>(R.id.location).text= dataJSON.toString()
//            val main = dataJSON["main"]
//            val temp = main["temp"]
//            findViewById<TextView>(R.id.temp).text=


        }
//        val dataJSON = Json.decodeFromString<weatherData>(dataString)
//        findViewById<TextView>(R.id.location).text = dataJSON.name
//        findViewById<TextView>(R.id.temp) = dataJSON.main.temp.toString()






    }

    private suspend fun httpGet(myURL: String): String {

        val result = withContext(Dispatchers.IO) {
            val inputStream: InputStream


            // create URL
            val url: URL = URL(myURL)

            // create HttpURLConnection
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

            // make GET request to the given URL
            conn.connect()

            // receive response as inputStream
            inputStream = conn.inputStream

            // convert inputstream to string
            convertInputStreamToString(inputStream)


        }
        return result

    }

    private fun convertInputStreamToString(inputStream: InputStream): String {
        val bufferedReader: BufferedReader? = BufferedReader(InputStreamReader(inputStream))

        var line:String? = bufferedReader?.readLine()
        var result:String = ""

        while (line != null) {
            result += line
            line = bufferedReader?.readLine()
        }

        inputStream.close()
        return result
    }
}