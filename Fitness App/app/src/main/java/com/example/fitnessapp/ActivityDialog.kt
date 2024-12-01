package com.example.fitnessapp

import android.app.AlertDialog
import android.content.Context

class ActivityDialog(private val context: Context) {
    fun show(activity: ActivityModel) {
        AlertDialog.Builder(context)
            .setTitle("Activity Details")
            .setMessage("Type: ${activity.type}\nDistance: ${activity.distance} km\nDuration: ${activity.duration} min\nCalories: ${activity.calories} kcal\nIntensity: ${activity.intensity}")
            .setPositiveButton("OK", null)
            .show()
    }
}