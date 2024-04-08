package com.juanmaGutierrez.carcare.model.localData

data class User(
    var name: String,
    var surname: String,
    var username: String,
    var email: String,
    var password: String,
    var repeatPassword: String
) {
    override fun toString(): String {
        return "Name: $name\nSurname: $surname\nUsername: $username\nEmail: $email\nPassword: $password\nRepeat password: $repeatPassword\n\n"
    }
}

data class UserFB(
    var created: String,
    var email: String,
    var name: String,
    var nickname: String,
    var role: String,
    var surname: String,
    var userId: String,
    var vehicles: List<Vehicle>
)