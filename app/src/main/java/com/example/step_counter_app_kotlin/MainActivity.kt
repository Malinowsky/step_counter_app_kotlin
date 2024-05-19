package com.example.step_counter_app_kotlin
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type



class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var stepsCountTextView: TextView
    private lateinit var stepsProgressBar: ProgressBar
    private lateinit var sensorManager: SensorManager
    private var running: Boolean = false
    private var totalSteps: Float = 0f
    private var previousTotalSteps: Float = 0f
    private var workoutActive = false
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var dailyStepGoal: Int = 10000
    private lateinit var goalStepsTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("WorkoutData", Context.MODE_PRIVATE)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        stepsCountTextView = findViewById(R.id.stepsCountTextView)
        stepsProgressBar = findViewById(R.id.stepsProgressBar)
        stepsProgressBar.max = dailyStepGoal
        goalStepsTextView = findViewById(R.id.goalStepsTextView)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            startWorkout()
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            stopWorkout()
        }

        setupNavigation()
    }
    private fun saveWorkoutSession(session: WorkoutSession) {
        val workoutsJson = sharedPreferences.getString("workout_sessions", "[]")
        val type: Type = object : TypeToken<List<WorkoutSession>>() {}.type
        val workouts: MutableList<WorkoutSession> = gson.fromJson(workoutsJson, type)

        workouts.add(session)
        sharedPreferences.edit().putString("workout_sessions", gson.toJson(workouts)).apply()
    }

    private fun calculateCalories(steps: Float): Int {
        return (steps * 0.04).toInt()
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

    private fun startWorkout() {
        workoutActive = true
        startTime = System.currentTimeMillis()
        if (!running) {
            subscribeToSensor()
        }
        Toast.makeText(this, "Workout started", Toast.LENGTH_SHORT).show()
    }

    private fun stopWorkout() {
        workoutActive = false
        val currentSteps = totalSteps - previousTotalSteps
        endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        val session = WorkoutSession(startTime, endTime, currentSteps.toInt(), calculateCalories(currentSteps))
        saveWorkoutSession(session)
        Toast.makeText(this, "Workout stopped. Total steps: $currentSteps", Toast.LENGTH_LONG).show()
        previousTotalSteps = totalSteps
    }

    private fun subscribeToSensor() {
        running = true
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }
    private fun loadStepGoal() {
        val prefs = getSharedPreferences("WorkoutData", Context.MODE_PRIVATE)
        dailyStepGoal = prefs.getInt("dailyStepGoal", 10000)  // Odczyt zapisanego celu
        stepsProgressBar.max = dailyStepGoal  // Aktualizacja maksymalnej wartości paska postępu
        goalStepsTextView.text = "Cel: $dailyStepGoal kroków"  // Wyświetlenie ustawionego celu
    }


    private fun updateProgressBar() {
        stepsProgressBar.progress = totalSteps.toInt()
    }

    override fun onResume() {
        super.onResume()
        loadStepGoal()
        if (workoutActive) {
            subscribeToSensor()

        }

    }

    override fun onPause() {
        super.onPause()
        if (running) {
            sensorManager.unregisterListener(this)
            running = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = event.values[0]
            val currentSteps = totalSteps - previousTotalSteps
            stepsProgressBar.progress = currentSteps.toInt()  // Aktualizacja postępu na pasku
            stepsCountTextView.text = "Kroki: ${currentSteps.toInt()} / $dailyStepGoal"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }

}

