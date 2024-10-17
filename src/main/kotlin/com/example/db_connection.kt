package com.example

import org.jetbrains.exposed.sql.Database
import java.sql.SQLException
import org.jetbrains.exposed.sql.transactions.transaction


fun connectToDatabase() {
    try {
        Database.connect(
            "jdbc:postgresql://localhost:5432/healthsync",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "12345"
        )
        println("Connected to database")

        transaction {
            exec("SELECT 1 ") { rs ->
                if (rs.next()) {
                    println("Connection validation query executed. Result: ${rs.getInt(1)}")
                }
            }
        }

    } catch (e: SQLException) {
        println("Failed to connect to database: ${e.message}")
    } catch (e: Exception) {
        println("An error occurred: ${e.message}")
    }


}


fun main() {
    connectToDatabase()
}