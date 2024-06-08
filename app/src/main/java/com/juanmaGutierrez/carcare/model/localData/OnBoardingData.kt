package com.juanmaGutierrez.carcare.model.localData

/**
 * Represents data for onboarding screens.
 * @property title The title of the onboarding screen.
 * @property desc The description of the onboarding screen.
 * @property imageURL The resource ID of the image associated with the onboarding screen.
 */
class OnBoardingData(
    var title: String,
    var desc: String,
    var imageURL: Int
) {
    /**
     * Returns a string representation of the onboarding data.
     */
    override fun toString(): String {
        return "Title: $title\nDesc: $desc\nImageURL: $imageURL"
    }
}