package com.juanmaGutierrez.carcare.service


class Constants {
    companion object {
        const val TAG = "Info-CarCare"
        const val TAG_ERROR = "Error-CarCare"

        // Database
        const val COLLECTION_LOG = "log"
        const val COLLECTION_LOG_DOC = "generalLog"
        const val COLLECTION_LOG_ARRAYLIST = "logs"

        // Login
        const val LOGIN_USER_LOGGED = "User logged"
        const val LOGIN_SUCCESFULLY = "Login successfully"
        const val LOGIN_ERROR = "Login error"
        const val LOGIN_FAILURE_SING_IN_WITH_EMAIL = "signInWithEmail:failure"

        // Errors
        const val ERROR_DATABASE = "Error in database operation"
        const val ERROR_CREATE_USER_WITH_EMAIL = "createUserWithEmail:failure"
    }
}