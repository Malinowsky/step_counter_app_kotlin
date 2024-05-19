package com.example.step_counter_app_kotlin

data class WorkoutSession(
    val startTime: Long,
    val endTime: Long,
    val stepsCount: Int,
    val caloriesBurned: Int
)