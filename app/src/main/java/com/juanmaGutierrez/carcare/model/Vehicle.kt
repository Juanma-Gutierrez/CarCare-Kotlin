package com.juanmaGutierrez.carcare.model

import com.google.firebase.firestore.DocumentReference
import com.juanmaGutierrez.carcare.localData.VehicleEntity
import java.util.Random
import java.util.UUID

data class Vehicle(
    var available: Boolean,
    var brand: String,
    var category: String,
    var created: String,
    var model: String,
    var plate: String,
    var ref: DocumentReference,
    var registrationDate: String,
    var vehicleId: String
)


fun createRandomVehicleList(): List<VehicleEntity> {
    val vehicleList = mutableListOf<VehicleEntity>()

    for (i in 1..3) {
        val vehicleId = UUID.randomUUID().toString()
        val userId = UUID.randomUUID().toString()
        val ref = "Ref_${Random().nextInt(1000)}"
        val created = "Fecha_${Random().nextInt(1000)}"
        val registrationDate = "Fecha_${Random().nextInt(1000)}"
        val available = Random().nextBoolean()
        val model = "Modelo_${Random().nextInt(100)}"
        val plate = "Placa_${Random().nextInt(100)}"
        val category = "Categoría_${Random().nextInt(10)}"
        val brand = "Marca_${Random().nextInt(50)}"

        val vehicle = VehicleEntity(
            vehicleId,
            userId,
            ref,
            created,
            registrationDate,
            available,
            model,
            plate,
            category,
            brand
        )
        vehicleList.add(vehicle)
    }
    return vehicleList
}

