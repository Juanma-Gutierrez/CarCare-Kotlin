package com.juanmaGutierrez.carcare.model

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
