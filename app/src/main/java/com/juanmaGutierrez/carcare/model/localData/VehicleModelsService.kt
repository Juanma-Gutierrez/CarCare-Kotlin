package com.juanmaGutierrez.carcare.model.localData

data class VehicleModelsService(
    val models: List<String> = emptyList()
){
    override fun toString(): String {
        return "Models: $models"
    }
}


