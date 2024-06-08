package com.juanmaGutierrez.carcare.model.localData

/**
 * Represents user data.
 * @property name The user's name.
 * @property surname The user's surname.
 * @property username The user's username.
 * @property email The user's email.
 * @property password The user's password.
 * @property repeatPassword The user's repeated password for confirmation.
 */
data class User(
    var name: String,
    var surname: String,
    var username: String,
    var email: String,
    var password: String,
    var repeatPassword: String
) {
    /**
     * Returns a string representation of the user.
     */
    override fun toString(): String {
        return "Name: $name\nSurname: $surname\nUsername: $username\nEmail: $email\nPassword: $password\nRepeat password: $repeatPassword\n\n"
    }
}