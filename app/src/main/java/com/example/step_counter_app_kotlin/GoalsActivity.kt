package com.example.step_counter_app_kotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class GoalsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)
        setupNavigation()

        val saveButton = findViewById<Button>(R.id.saveGoalButton)
        val goalInput = findViewById<EditText>(R.id.goalStepsInput)


        saveButton.setOnClickListener {
            val goal = goalInput.text.toString().toIntOrNull()
            goal?.let {
                val editor = getSharedPreferences("WorkoutData", Context.MODE_PRIVATE).edit()
                editor.putInt("dailyStepGoal", it)
                editor.apply()
                Toast.makeText(this, "Cel został zapisany: $it kroków", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK) // Oznaczenie, że cel został zmieniony
                finish()
            } ?: Toast.makeText(this, "Proszę wprowadzić prawidłową wartość", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigation() {
        findViewById<ImageView>(R.id.historyIcon).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<ImageView>(R.id.goalsIcon).setOnClickListener {
            startActivity(Intent(this, GoalsActivity::class.java))
        }

        // Jeśli masz przycisk domu i chcesz aby wracał do MainActivity lub innej aktywności
        findViewById<ImageView>(R.id.homeIcon).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}