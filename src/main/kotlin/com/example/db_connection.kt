package com.example

import org.jetbrains.exposed.sql.Database
import java.sql.SQLException


fun connectToDatabase() {
    try {
        Database.connect(
            "jdbc:postgresql://localhost:5432/healthsync",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "12345"
        )

    } catch (e: SQLException) {
        println("Failed to connect to database: ${e.message}")
    } catch (e: Exception) {
        println("An error occurred: ${e.message}")
    }

}