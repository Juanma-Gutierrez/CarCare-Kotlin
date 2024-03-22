package com.juanmaGutierrez.carcare.data

import android.content.Context
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.OnBoardingData

class OnBoardingData {
}

fun getOnBoardingDataValues(context: Context): MutableList<OnBoardingData> {
    val data: MutableList<OnBoardingData> = mutableListOf(
        OnBoardingData(
            context.getString(R.string.onBoarding_title_carcare),
            context.getString(R.string.onBoarding_text_carcare),
            R.drawable.logo_carcare_transparent
        ),
        OnBoardingData(
            context.getString(R.string.onBoarding_title1),
            context.getString(R.string.onBoarding_text1),
            R.drawable.onboarding1
        ),
        OnBoardingData(
            context.getString(R.string.onBoarding_title2),
            context.getString(R.string.onBoarding_text2),
            R.drawable.onboarding2
        ),
        OnBoardingData(
            context.getString(R.string.onBoarding_title3),
            context.getString(R.string.onBoarding_text3),
            R.drawable.onboarding3
        )
    )
    return data
}