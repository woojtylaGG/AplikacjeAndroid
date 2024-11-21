package com.example.fitnessapp

class Activity {
    data class Activity(
        val distance: Double,
        val time: Double,
        val calories: Double,
        val intensity: Int,
        val activityType: String
    )
}