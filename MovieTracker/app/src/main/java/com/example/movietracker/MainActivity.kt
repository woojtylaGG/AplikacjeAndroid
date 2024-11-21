package com.example.movietracker

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var ivPoster: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvPlot: TextView
    private lateinit var seekBarRating: SeekBar
    private lateinit var btnSaveRating: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        ivPoster = findViewById(R.id.ivPoster)
        tvTitle = findViewById(R.id.tvTitle)
        tvPlot = findViewById(R.id.tvPlot)
        seekBarRating = findViewById(R.id.seekBarRating)
        btnSaveRating = findViewById(R.id.btnSaveRating)

        // Search button click listener
        btnSearch.setOnClickListener {
            val movieTitle = etSearch.text.toString()
            if (movieTitle.isNotEmpty()) {
                MovieSearchTask().execute(movieTitle)
            } else {
                Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show()
            }
        }

        // Save rating button click listener
        btnSaveRating.setOnClickListener {
            val rating = seekBarRating.progress
            Toast.makeText(this, "Rating saved: $rating", Toast.LENGTH_SHORT).show()
        }
    }

    // AsyncTask class for background movie search
    private inner class MovieSearchTask : AsyncTask<String, Void, String?>() {
        override fun doInBackground(vararg params: String?): String? {
            val movieTitle = params[0]?.let { URLEncoder.encode(it, "UTF-8") } ?: return null
            val url = "https://www.omdbapi.com/?apikey=364d19e7&t=$movieTitle"

            // Log the URL being requested for debugging
            Log.d("MovieSearchTask", "Request URL: $url")

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            return try {
                val response = client.newCall(request).execute()

                // Log the response code and response body for debugging
                Log.d("MovieSearchTask", "Response Code: ${response.code}")
                val responseBody = response.body?.string() ?: "No response body"
                Log.d("MovieSearchTask", "Response Body: $responseBody")

                if (response.isSuccessful) {
                    // Return the response body
                    responseBody
                } else {
                    // Log the error response
                    Log.e("MovieSearchTask", "Error response code: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                Log.e("MovieSearchTask", "Exception occurred: ${e.localizedMessage}")
                null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                try {
                    val movieDetails = JSONObject(result)
                    val title = movieDetails.optString("Title", "No title available")
                    val plot = movieDetails.optString("Plot", "No plot available")
                    val posterUrl = movieDetails.optString("Poster", "")

                    // Update the UI with the movie details
                    tvTitle.text = title
                    tvPlot.text = plot

                    if (posterUrl.isNotEmpty()) {
                        Glide.with(this@MainActivity).load(posterUrl).into(ivPoster)
                    }
                } catch (e: Exception) {
                    Log.e("MovieSearchTask", "Error parsing JSON: ${e.localizedMessage}")
                    Toast.makeText(
                        this@MainActivity,
                        "Error parsing movie details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Log.e("MovieSearchTask", "Failed to fetch movie details. Response is null.")
                Toast.makeText(
                    this@MainActivity,
                    "Failed to fetch movie details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
