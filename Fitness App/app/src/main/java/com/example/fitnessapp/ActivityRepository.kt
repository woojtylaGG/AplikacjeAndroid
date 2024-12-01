package com.example.fitnessapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ActivityRepository(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("activities", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveActivities(activities: List<ActivityModel>) {
        val json = gson.toJson(activities)
        sharedPreferences.edit().putString("activities_list", json).apply()
    }

    fun getActivities(): List<ActivityModel> {
        val json = sharedPreferences.getString("activities_list", null) ?: return emptyList()
        val type = object : TypeToken<List<ActivityModel>>() {}.type
        return gson.fromJson(json, type)
    }
}