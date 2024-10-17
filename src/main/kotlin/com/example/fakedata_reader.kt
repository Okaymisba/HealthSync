package com.example

import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.*
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.system.exitProcess

data class HealthData(val health_id: Int, val heart_rate: Int, val step_count: Int, val timestamp: Long)

object HealthDataTable : Table("health_data") {
    val healthId = integer("health_id")
    val heartRate = integer("heart_rate")
    val stepCount = integer("step_count")
    val timestamp = long("timestamp")
}

fun `mqtt reading and database addition`() {
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

            connectToDatabase()
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
    transaction {
        HealthDataTable.insert {
            it[healthId] = healthData.health_id
            it[heartRate] = healthData.heart_rate
            it[stepCount] = healthData.step_count
            it[timestamp] = healthData.timestamp
        }
    }
}
