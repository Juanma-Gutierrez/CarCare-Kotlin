package com.juanmaGutierrez.carcare.model.firebase

import com.juanmaGutierrez.carcare.model.localData.VehiclePreview

/**
 * Represents a user in Firebase.
 * @property created The creation date of the user.
 * @property email The email of the user.
 * @property name The name of the user.
 * @property nickname The nickname of the user.
 * @property role The role of the user.
 * @property surname The surname of the user.
 * @property userId The ID of the user.
 * @property vehicles The list of vehicles associated with the user.
 */
data class UserFB(
    var created: String,
    var email: String,
    var name: String,
    var nickname: String,
    var role: String,
    var surname: String,
    var userId: String,
    var vehicles: List<VehiclePreview>
) {
    /**
     * Returns a string representation of the user.
     */
    override fun toString(): String {
        return "Created: $created\nEmail: $email\nName: $name\nNickname: $nickname\nRole: $role\nSurname: $surname\nUserId: $userId\nVehicles: $vehicles"
    }
}
