package com.juanmaGutierrez.carcare.model.localData

class OnBoardingData(
    var title: String,
    var desc: String,
    var imageURL: Int
){
    override fun toString(): String {
        return "Title: $title\nDesc: $desc\nImageURL: $imageURL"
    }
}