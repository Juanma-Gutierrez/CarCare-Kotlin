package com.juanmaGutierrez.carcare.model.api

/**
 * Represents the response from the API for the list of vehicles.
 * @property brands: List of vehicle brands.
 */
data class VehicleListResponseAPI(
    val brands: List<String>,
) {
    override fun toString(): String {
        return "Brands: $brands"
    }
}

/**
 * Represents the response from the API for the list of brands.
 * @property data: Object containing the list of brands.
 * @property status: Status of the response.
 */
data class BrandsResponseAPI(
    val data: BrandsList,
    val status: String,
) {
    override fun toString(): String {
        return "Data: $data\nStatus: $status"
    }
}

/**
 * Represents the list of brands.
 * @property cars: List of car brands.
 * @property motorcycles: List of motorcycle brands.
 * @property trucks: List of truck brands.
 * @property vans: List of van brands.
 */
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

/**
 * Represents the response from the API for the list of vehicle models.
 * @property models: List of vehicle models.
 */
data class ModelListResponseAPI(
    val models: List<String>,
) {
    override fun toString(): String {
        return "Models: $models"
    }
}
