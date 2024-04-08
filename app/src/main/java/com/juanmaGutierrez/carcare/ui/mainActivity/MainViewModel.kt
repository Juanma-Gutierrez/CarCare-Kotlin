package com.juanmaGutierrez.carcare.ui.mainActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanmaGutierrez.carcare.api.APIClient
import com.juanmaGutierrez.carcare.api.APIService
import com.juanmaGutierrez.carcare.model.Constants
import com.juanmaGutierrez.carcare.service.log
import com.juanmaGutierrez.carcare.ui.login.LoginActivity
import com.juanmaGutierrez.carcare.ui.onBoarding.OnBoardingActivity
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {
    private lateinit var apiService: APIService
    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        if (!isFirstTimeRun(context)) {
            val activity = Intent(context, LoginActivity::class.java)
            context.startActivity(activity)
        } else {
            showOnBoardingActivity(context)
        }
    }

    private fun showOnBoardingActivity(context: Context) {
        val activity = Intent(context, OnBoardingActivity::class.java)
        context.startActivity(activity)
    }

    private fun isFirstTimeRun(context: Context): Boolean {
        sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isFirstTimeRun", true)
    }

    fun getAllBrandsFromAPI() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(APIService::class.java)
        viewModelScope.launch {
            callBrandsFromAPI()
        }
    }

    private suspend fun callBrandsFromAPI() {
        try {
            val brandsResponse = APIClient.apiService.getAllBrands()
            log("$brandsResponse")
        } catch (e: Exception) {
            log("${Constants.ERROR_API_CALL} ${e.message}")
        }
    }


}
