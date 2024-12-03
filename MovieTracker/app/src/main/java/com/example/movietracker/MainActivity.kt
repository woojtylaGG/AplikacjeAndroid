package com.example.movietracker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var movieInput: EditText
    private lateinit var titleText: TextView
    private lateinit var moviePoster: ImageView
    private lateinit var descriptionText: TextView
    private lateinit var watchedCheckBox: CheckBox
    private lateinit var ratingSeekBar: SeekBar
    private lateinit var ratingLabel: TextView
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private val client = OkHttpClient()
    private val gson = Gson()
    private var currentMovie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        movieInput = findViewById(R.id.movieInput)
        titleText = findViewById(R.id.titleText)
        moviePoster = findViewById(R.id.moviePoster)
        descriptionText = findViewById(R.id.descriptionText)
        watchedCheckBox = findViewById(R.id.watchedCheckBox)
        ratingSeekBar = findViewById(R.id.ratingSeekBar)
        ratingLabel = findViewById(R.id.ratingLabel)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)

        // Nasłuchiwacze
        findViewById<Button>(R.id.searchButton).setOnClickListener { searchMovie(movieInput.text.toString()) }
        saveButton.setOnClickListener { saveCurrentMovie() }
        deleteButton.setOnClickListener { deleteCurrentMovie() }

        ratingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ratingLabel.text = "Ocena: $progress/10"
                currentMovie?.rating = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun searchMovie(title: String) {
        val url = "https://www.omdbapi.com/?apikey=364d19e7&t=${title.replace(" ", "+")}"
        client.newCall(Request.Builder().url(url).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { showToast("Błąd połączenia") }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { json ->
                    val movieJson = JSONObject(json)
                    currentMovie = Movie(
                        title = movieJson.optString("Title"),
                        plot = movieJson.optString("Plot"),
                        posterUrl = movieJson.optString("Poster")
                    )
                    runOnUiThread { updateUI() }
                }
            }
        })
    }

    private fun updateUI() {
        currentMovie?.let { movie ->
            titleText.text = movie.title
            descriptionText.text = movie.plot
            Glide.with(this).load(movie.posterUrl).into(moviePoster)


            val savedMovie = loadMovies().find { it.title == movie.title }
            if (savedMovie != null) {
                watchedCheckBox.isChecked = savedMovie.watched
                ratingSeekBar.progress = savedMovie.rating
                movie.watched = savedMovie.watched
                movie.rating = savedMovie.rating
            } else {
                watchedCheckBox.isChecked = false
                ratingSeekBar.progress = 0
            }


            showControls(true)
        }
    }



    private fun saveCurrentMovie() {
        currentMovie?.apply {
            watched = watchedCheckBox.isChecked
            saveMovie(this)
            showToast("Film zapisany")
        }
    }

    private fun deleteCurrentMovie() {
        currentMovie?.let {
            deleteMovie(it)
            clearUI()
            showToast("Film usunięty")
        }
    }

    private fun saveMovie(movie: Movie) {
        val movies = loadMovies().toMutableList()
        movies.removeIf { it.title == movie.title }
        movies.add(movie)
        saveMovies(movies)
    }

    private fun deleteMovie(movie: Movie) {
        val movies = loadMovies().toMutableList()
        movies.removeIf { it.title == movie.title }
        saveMovies(movies)
    }

    private fun loadMovies(): List<Movie> {
        val json = getPreferences(Context.MODE_PRIVATE).getString("SAVED_MOVIES", "[]")
        return gson.fromJson(json, object : TypeToken<List<Movie>>() {}.type) ?: emptyList()
    }

    private fun saveMovies(movies: List<Movie>) {
        val json = gson.toJson(movies)
        getPreferences(Context.MODE_PRIVATE).edit().putString("SAVED_MOVIES", json).apply()
    }

    private fun clearUI() {
        currentMovie = null
        titleText.text = ""
        descriptionText.text = ""
        moviePoster.setImageResource(0)
        watchedCheckBox.isChecked = false
        ratingSeekBar.progress = 0
        showControls(false)
    }

    private fun showControls(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        watchedCheckBox.visibility = visibility
        saveButton.visibility = visibility
        deleteButton.visibility = visibility
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    data class Movie(
        val title: String,
        val plot: String,
        val posterUrl: String,
        var watched: Boolean = false,
        var rating: Int = 0
    )
}
