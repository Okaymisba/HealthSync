package com.example

import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.system.exitProcess

data class HealthData(val id: Int, val heart_rate: Int, val step_count: Int, val timestamp: Long)

fun mqttReadingAndDataBaseAddition () {
    val brokerUrl = "ssl://4dbbebee01cb4916af953cf932ac5313.s1.eu.hivemq.cloud:8883"
    val clientId = MqttClient.generateClientId()
    val mqttClient = MqttClient(brokerUrl, clientId)

    val options = MqttConnectOptions().apply {
        userName = "Reader"
        password = "Reader123".toCharArray()
        isCleanSession = true
    }

    try {
        mqttClient.connect(options)

        mqttClient.subscribe("test/topic") { topic, message ->
            val payload = String(message.payload)

            val gson = Gson()
            val healthData = gson.fromJson(payload, HealthData::class.java)
            println(healthData)

            insertToDatabase(healthData)
        }

        while (true) {
            Thread.sleep(10000)
        }

    } catch (e: MqttException) {
        e.printStackTrace()
        exitProcess(1)
    } finally {
        mqttClient.disconnect()
    }
}

private fun insertToDatabase(healthData: HealthData) {

    Database.connect(
        "jdbc:postgresql://localhost:5432/healthsync",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "12345"
    )

        try {
            transaction {
                HealthDataTable.insert {
                    it[id] = healthData.id
                    it[heartRate] = healthData.heart_rate
                    it[stepCount] = healthData.step_count
                    it[timestamp] = healthData.timestamp
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

