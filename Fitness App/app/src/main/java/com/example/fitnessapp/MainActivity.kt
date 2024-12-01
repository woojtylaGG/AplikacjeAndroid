package com.example.fitnessapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var repository: ActivityRepository
    private lateinit var adapter: ActivityAdapter
    private lateinit var activities: MutableList<ActivityModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = ActivityRepository(this)
        activities = repository.getActivities().toMutableList()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewActivities)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ActivityAdapter(activities) { activity ->
            ActivityDialog(this).show(activity)
        }
        recyclerView.adapter = adapter

        val editTextDistance: EditText = findViewById(R.id.editTextDistance)
        val editTextTime: EditText = findViewById(R.id.editTextTime)
        val editTextCalories: EditText = findViewById(R.id.editTextCalories)
        val seekBarIntensity: SeekBar = findViewById(R.id.seekBarIntensity)
        val radioGroupActivity: RadioGroup = findViewById(R.id.radioGroupActivity)
        val buttonAddActivity: Button = findViewById(R.id.buttonAddActivity)

        buttonAddActivity.setOnClickListener {
            val distance = editTextDistance.text.toString().toFloat()
            val duration = editTextTime.text.toString().toFloat()
            val calories = editTextCalories.text.toString().toFloat()
            val intensity = seekBarIntensity.progress
            val type = findViewById<RadioButton>(radioGroupActivity.checkedRadioButtonId).text.toString()

            val activity = ActivityModel(distance, duration, calories, intensity, type)
            activities.add(activity)
            repository.saveActivities(activities)
            adapter.notifyDataSetChanged()
        }
    }
}