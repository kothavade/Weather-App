package com.example.weatherproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.weatherproject.databinding.ActivityMainBinding
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val service = ApiInterface.create()

        val call: Call<JsonObject> = service.fetchFact()
        call.enqueue(object : retrofit2.Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val response = response.body()
                // rest of the keys...
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }
        })

        temp.text(response.)
    }
}