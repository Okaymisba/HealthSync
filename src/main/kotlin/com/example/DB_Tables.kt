package com.example

import org.jetbrains.exposed.sql.Table

object HealthDataTable : Table("health_data") {
    val healthId = integer("id")
    val heartRate = integer("heart_rate")
    val stepCount = integer("step_count")
    val timestamp = long("timestamp")
}

object User : Table("user_data") {
    val id = integer("id")
    val user_name = varchar("user_name", 20)
    val password = varchar("password", 20)
}