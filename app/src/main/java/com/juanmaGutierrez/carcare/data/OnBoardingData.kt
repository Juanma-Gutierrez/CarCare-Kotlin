package com.juanmaGutierrez.carcare.data

import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.OnBoardingData

class OnBoardingData {
}

fun getOnBoardingDataValues(): MutableList<OnBoardingData> {
    val data: MutableList<OnBoardingData> = mutableListOf(
        OnBoardingData(
            "CarCare",
            "Carcare es tu aplicación para la gestión de gastos de tus vehículos",
            R.drawable.logo_carcare_transparent
        ),
        OnBoardingData(
            "Vehículos",
            "Da de alta todos los vehículos que tengas, también puedes gestionar los que has tenido anteriormente.",
            R.drawable.onboarding1
        ),
        OnBoardingData(
            "Proveedores",
            "Agrega los proveedores que te prestan servicio.",
            R.drawable.onboarding2
        ),
        OnBoardingData(
            "Gastos",
            "Añade los gastos que surjan en cada momento.",
            R.drawable.onboarding3
        )
    )
    return data
}