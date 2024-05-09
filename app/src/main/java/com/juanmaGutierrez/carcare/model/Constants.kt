package com.juanmaGutierrez.carcare.model


class Constants {
    companion object {
        // Tags
        const val TAG = "Info-CarCare"
        const val TAG_ERROR = "Error-CarCare"

        // App
        const val DATE_FORMAT_LOCAL = "dd/MM/yyyy"
        const val DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val REQUEST_CODE_PERMISSIONS = 42
        const val SETTINGS_VEHICLES_LIST_COMPACT = "settings_vehicles_list_compact"
        const val SETTINGS_IS_FIRST_TIME_RUN = "isFirstTimeRun"
        const val SETTINGS_PROVIDERS_GRID_FORMAT = "settings_providers_list_grid_format"

        // API
        const val API_URL = "https://jumang.pythonanywhere.com/"

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

        // Log VEHICLE
        const val LOG_VEHICLE_CREATION_SUCCESSFULLY = "Creation of vehicle successfully"
        const val LOG_VEHICLE_CREATION_ERROR = "Error in vehicle creation"
        const val LOG_VEHICLE_EDITION_SUCCESSFULLY = "Edition of vehicle successfully"
        const val LOG_VEHICLE_EDITION_ERROR = "Error in vehicle edition"
        const val LOG_VEHICLE_DELETION_SUCCESSFULLY = "Deletion of vehicle successfully"
        const val LOG_VEHICLE_DELETION_ERROR = "Error in vehicle deletion"

        // Log PROVIDER
        const val LOG_PROVIDER_CREATION_SUCCESSFULLY = "Creation of provider successfully"
        const val LOG_PROVIDER_CREATION_ERROR = "Error in provider creation"
        const val LOG_PROVIDER_EDITION_SUCCESSFULLY = "Edition of provider successfully"
        const val LOG_PROVIDER_EDITION_ERROR = "Error in provider edition"
        const val LOG_PROVIDER_DELETION_SUCCESSFULLY = "Deletion of provider successfully"
        const val LOG_PROVIDER_DELETION_ERROR = "Error in vehicle provider"

        // Log SPENT
        const val LOG_SPENT_CREATION_SUCCESSFULLY = "Creation of spent successfully"
        const val LOG_SPENT_CREATION_ERROR = "Error in spent creation"
        const val LOG_SPENT_EDITION_SUCCESSFULLY = "Edition of spent successfully"
        const val LOG_SPENT_EDITION_ERROR = "Error in spent edition"
        const val LOG_SPENT_DELETION_SUCCESSFULLY = "Deletion of spent successfully"
        const val LOG_SPENT_DELETION_ERROR = "Error in spent deletion"

        // Register
        const val REGISTER_SUCCESSFULLY = "Register user successfully"
        const val REGISTER_USER_ERROR = "Error in register user"
        const val DEFAULT_ROLE = "user"

        // Errors
        const val ERROR_DATABASE = "Error in database operation"
        const val ERROR_CREATE_USER_WITH_EMAIL = "CreateUserWithEmail: failure"
        const val ERROR_EXCEPTION_PREFIX = "Get failed with: "
        const val ERROR_FIREBASE_CALL = "Error occurred during the data loading: "
        const val ERROR_DOCUMENT_DOESNT_EXISTS = "Error document doesn't exists"

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