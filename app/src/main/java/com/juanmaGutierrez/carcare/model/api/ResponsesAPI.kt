package com.juanmaGutierrez.carcare.model.api

data class VehicleListResponseAPI(
    val brands: List<String>,
)

data class BrandsResponseAPI(
    val data: BrandsList,
    val status: String,
)

data class BrandsList(
    val cars: List<String>,
    val motorcycles: List<String>,
    val trucks: List<String>,
    val vans: List<String>,
)
