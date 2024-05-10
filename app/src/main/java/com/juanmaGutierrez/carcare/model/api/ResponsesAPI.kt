package com.juanmaGutierrez.carcare.model.api

data class VehicleListResponseAPI(
    val brands: List<String>,
) {
    override fun toString(): String {
        return "Brands: $brands"
    }
}

data class BrandsResponseAPI(
    val data: BrandsList,
    val status: String,
) {
    override fun toString(): String {
        return "Data: $data\nStatus: $status"
    }
}

data class BrandsList(
    val cars: List<String>,
    val motorcycles: List<String>,
    val trucks: List<String>,
    val vans: List<String>,
) {
    override fun toString(): String {
        return "Cars: $cars\nMotorcycles: $motorcycles\nTrucks: $trucks\nVans: $vans"
    }
}

data class ModelListResponseAPI(
    val models: List<String>,
) {
    override fun toString(): String {
        return "Models: $models"
    }
}
