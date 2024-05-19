package com.example.step_counter_app_kotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setupNavigation()

        val historyLayout = findViewById<LinearLayout>(R.id.history_layout)
        val sharedPreferences = getSharedPreferences("WorkoutData", Context.MODE_PRIVATE)
        val workoutsJson = sharedPreferences.getString("workout_sessions", "[]")
        val formattedHistory = formatHistory(workoutsJson)
        historyLayout.removeAllViews() // Clear previous views if any
        formattedHistory.forEach { session ->
            val sessionView = TextView(this).apply {
                text = session
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16)
                }
                setBackgroundResource(R.drawable.rounded_background)
                setPadding(20, 20, 20, 20)
                textSize = 16f
//                typeface = resources.getFont(R.font.audiowide)
                setTextColor(getResources().getColor(R.color.my_custom_color))

            }
            historyLayout.addView(sessionView)
        }
    }

    private fun formatHistory(workoutsJson: String?): List<String> {
        if (workoutsJson.isNullOrEmpty() || workoutsJson == "[]") return listOf("No workout data recorded")

        val type: Type = object : TypeToken<List<WorkoutSession>>() {}.type
        val workouts: List<WorkoutSession> = Gson().fromJson(workoutsJson, type)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

        return workouts.sortedByDescending { it.startTime }.map { session ->
            val startTimeStr = dateFormat.format(Date(session.startTime))
            val endTimeStr = dateFormat.format(Date(session.endTime))
            "Start: $startTimeStr\nEnd: $endTimeStr\nSteps: ${session.stepsCount}\nCalories: ${session.caloriesBurned}\n"
        }
    }

    private fun setupNavigation() {
        findViewById<ImageView>(R.id.historyIcon).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<ImageView>(R.id.goalsIcon).setOnClickListener {
            startActivity(Intent(this, GoalsActivity::class.java))
        }

        findViewById<ImageView>(R.id.homeIcon).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}