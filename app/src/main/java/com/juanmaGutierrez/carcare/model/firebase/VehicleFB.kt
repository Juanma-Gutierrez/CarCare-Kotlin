package com.juanmaGutierrez.carcare.model.firebase

data class VehicleFB(
    val available: Boolean,
    val brand: String,
    val category: String,
    val created: String,
    val imageURL: String? = null,
    val model: String,
    val plate: String,
    val registrationDate: String,
    val spents: List<SpentFB>,
    val userId: String,
    val vehicleId: String
) {
    override fun toString(): String {
        return "Categoría: $category\nMarca: $brand\nModelo: $model\nMatrícula: $plate\nVehículo disponible: $available\n" +
                "Fecha de registro: ${registrationDate}\nCreado: $created\nImageURL: $imageURL\nID del vehículo: $vehicleId\nNº Gastos: ${spents.size}\nUserID: $userId"
    }
}

