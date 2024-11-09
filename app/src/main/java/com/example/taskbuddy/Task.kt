package com.example.taskbuddy

import java.time.LocalDate
import java.util.Date

//import java.util.Date

//import java.util.Date

data class Task(
    val id:Int,
    val title:String,
    val description:String,
    val priority: Priority,
    val deadline: Long
){
    enum class Priority {
            LOW, // Low priority task
            MEDIUM, // Medium priority task
            HIGH // High priority task
        }
}
