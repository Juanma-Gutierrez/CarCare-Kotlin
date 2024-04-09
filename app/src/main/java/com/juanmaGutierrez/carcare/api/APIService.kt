package com.juanmaGutierrez.carcare.api

import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.api.BrandsResponseAPI
import com.juanmaGutierrez.carcare.model.api.ModelListResponseAPI
import com.juanmaGutierrez.carcare.model.api.VehicleListResponseAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {
    @GET("${Constants.API_URL}api/cars/brands")
    suspend fun getCarsBrands(): VehicleListResponseAPI

    @GET("${Constants.API_URL}api/motorcycles/brands")
    suspend fun getMotorcyclesBrands(): VehicleListResponseAPI

    @GET("${Constants.API_URL}api/vans/brands")
    suspend fun getVansBrands(): VehicleListResponseAPI

    @GET("${Constants.API_URL}api/trucks/brands")
    suspend fun getTrucksBrands(): VehicleListResponseAPI

    @GET("${Constants.API_URL}api/brands")
    suspend fun getAllBrands(): BrandsResponseAPI

    @GET("${Constants.API_URL}api/{category}s/models/{brand}")
    suspend fun getModelsByBrand(
        @Path("category") category: String,
        @Path("brand") brand: String
    ): ModelListResponseAPI
}

object APIClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(APIService::class.java)
}