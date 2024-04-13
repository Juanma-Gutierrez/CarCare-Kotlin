package com.juanmaGutierrez.carcare.model


class Constants {
    companion object {
        // Tags
        const val TAG = "Info-CarCare"
        const val TAG_ERROR = "Error-CarCare"

        // API
        const val API_URL = "https://jumang.pythonanywhere.com/"

        // Messages
        const val ISLOADING_VISIBLE = "VISIBLE"
        const val ISLOADING_GONE = "GONE"

        // Database
        const val DATABASE_NAME = "app_database"
        const val COLLECTION_LOG = "log"
        const val COLLECTION_LOG_DOC = "generalLog"
        const val COLLECTION_LOG_ARRAYLIST = "logs"

        // Login
        const val LOGIN_USER_LOGGED = "User logged"
        const val LOGIN_SUCCESFULLY = "Login successfully"
        const val LOGIN_ERROR = "Login error"
        const val LOGIN_FAILURE_SING_IN_WITH_EMAIL = "signInWithEmail:failure"
        const val LOGOUT_SUCCESSFULLY = "Logout successfully"

        // Register
        const val REGISTER_SUCCESSFULLY = "Register user successfully"
        const val REGISTER_USER_ERROR = "Error in register user"
        const val DEFAULT_ROLE = "user"

        // Errors
        const val ERROR_DATABASE = "Error in database operation"
        const val ERROR_CREATE_USER_WITH_EMAIL = "CreateUserWithEmail: failure"
        const val ERROR_EXCEPTION_PREFIX = "Get failed with:"
        const val ERROR_API_CALL = "Error occurred during API call:"

        // Firebase Collections
        const val FB_COLLECTION_LOG = "log"
        const val FB_COLLECTION_PROVIDER = "provider"
        const val FB_COLLECTION_USER = "user"
        const val FB_COLLECTION_VEHICLE = "vehicle"

        // Firebase extra
        const val FB_EXTRA_VEHICLES = "vehicles"

        // Firebase Errors
        const val FB_NO_DOCUMENT = "No such document"
        const val FB_ERROR_DB_OPERATION = "Error in database operation"
    }
}