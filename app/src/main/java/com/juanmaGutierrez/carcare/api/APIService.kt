package com.juanmaGutierrez.carcare.api

import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.api.BrandsResponseAPI
import com.juanmaGutierrez.carcare.model.api.VehicleListResponseAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface APIService {
    @GET(Constants.API_URL + "api/cars/brands")
    suspend fun getCarsBrands(): VehicleListResponseAPI

    @GET(Constants.API_URL + "api/motorcycles/brands")
    suspend fun getMotorcyclesBrands(): VehicleListResponseAPI

    @GET(Constants.API_URL + "api/vans/brands")
    suspend fun getVansBrands(): VehicleListResponseAPI

    @GET(Constants.API_URL + "api/truck/brands")
    suspend fun getTrucksBrands(): VehicleListResponseAPI

    @GET(Constants.API_URL + "api/brands")
    suspend fun getAllBrands(): BrandsResponseAPI
}

object APIClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(APIService::class.java)
}