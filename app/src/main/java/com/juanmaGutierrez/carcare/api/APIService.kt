package com.juanmaGutierrez.carcare.api

import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.model.api.BrandsResponseAPI
import com.juanmaGutierrez.carcare.model.api.ModelListResponseAPI
import com.juanmaGutierrez.carcare.model.api.VehicleListResponseAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface for defining API endpoints
 */
interface APIService {
    /**
     * Retrieves a list of car brands from the API
     * @return VehicleListResponseAPI: Response containing car brands
     */
    @GET("${Constants.API_URL}api/cars/brands")
    suspend fun getCarsBrands(): VehicleListResponseAPI

    /**
     * Retrieves a list of motorcycle brands from the API
     * @return VehicleListResponseAPI: Response containing motorcycle brands
     */
    @GET("${Constants.API_URL}api/motorcycles/brands")
    suspend fun getMotorcyclesBrands(): VehicleListResponseAPI

    /**
     * Retrieves a list of van brands from the API
     * @return VehicleListResponseAPI: Response containing van brands
     */
    @GET("${Constants.API_URL}api/vans/brands")
    suspend fun getVansBrands(): VehicleListResponseAPI

    /**
     * Retrieves a list of truck brands from the API
     * @return VehicleListResponseAPI: Response containing truck brands
     */
    @GET("${Constants.API_URL}api/trucks/brands")
    suspend fun getTrucksBrands(): VehicleListResponseAPI

    /**
     * Retrieves a list of all vehicle brands from the API
     * @return BrandsResponseAPI: Response containing all vehicle brands
     */
    @GET("${Constants.API_URL}api/brands")
    suspend fun getAllBrands(): BrandsResponseAPI

    /**
     * Retrieves a list of vehicle models for a specific brand and category from the API
     * @param category: The category of the vehicle (e.g., car, motorcycle)
     * @param brand: The brand of the vehicle
     * @return ModelListResponseAPI: Response containing vehicle models
     */
    @GET("${Constants.API_URL}api/{category}s/models/{brand}")
    suspend fun getModelsByBrand(
        @Path("category") category: String,
        @Path("brand") brand: String
    ): ModelListResponseAPI
}

/**
 * Singleton object for accessing the API service
 */
object APIClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(APIService::class.java)
}