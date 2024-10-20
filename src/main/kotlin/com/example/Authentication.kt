package com.example

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun retrieveUsernameFromDatabase (username: String ): String? {

    Database.connect(
        "jdbc:postgresql://localhost:5432/healthsync",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "12345"
    )

    val user = transaction {
        User.select {User.user_name eq username }
            .map { it[User.password] }
            .singleOrNull()
    }
    return user
}