package com.juanmaGutierrez.carcare.model.firebase

import com.juanmaGutierrez.carcare.model.localData.VehiclePreview

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
    override fun toString(): String {
        return "Created: $created\nEmail: $email\nName: $name\nNickname: $nickname\nRole: $role\nSurname: $surname\nUserId: $userId\nVehicles: $vehicles"
    }
}
